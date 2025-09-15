package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAllianceRepository {
    private final FirebaseFirestore db;

    public FirebaseAllianceRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void createAlliance(Alliance alliance, OnCompleteListener<Void> listener) {
        Map<String, Object> allianceData = new HashMap<>();
        allianceData.put("id", alliance.getId());
        allianceData.put("name", alliance.getName());
        allianceData.put("leaderId", alliance.getLeaderId());
        allianceData.put("missionStarted", alliance.isMissionStarted());

        db.collection("alliances")
                .document(alliance.getId())
                .set(allianceData)
                .addOnCompleteListener(listener);
    }

    public void addMemberToAlliance(String allianceId, String memberId, OnCompleteListener<Void> listener) {
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("userId", memberId);

        db.collection("alliances")
                .document(allianceId)
                .collection("members")
                .document(memberId)
                .set(memberData)
                .addOnCompleteListener(listener);
    }

    public void addInvitation(AllianceInvitation invitation, OnCompleteListener<Void> listener) {
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("id", invitation.getId());
        inviteData.put("allianceId", invitation.getAllianceId());
        inviteData.put("fromUserId", invitation.getFromUserId());
        inviteData.put("toUserId", invitation.getToUserId());
        inviteData.put("status", invitation.getStatus().name());

        db.collection("alliances")
                .document(invitation.getAllianceId())
                .collection("invites")
                .document(invitation.getId())
                .set(inviteData)
                .addOnCompleteListener(listener);
    }

    public void deleteAlliance(String allianceId, OnCompleteListener<Void> listener) {
        DocumentReference allianceRef = db.collection("alliances").document(allianceId);

        allianceRef.collection("members").get().addOnSuccessListener(memberSnapshot -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : memberSnapshot.getDocuments()) {
                batch.delete(doc.getReference());
            }

            allianceRef.collection("invites").get().addOnSuccessListener(inviteSnapshot -> {
                for (DocumentSnapshot doc : inviteSnapshot.getDocuments()) {
                    batch.delete(doc.getReference());
                }

                batch.delete(allianceRef);

                batch.commit().addOnCompleteListener(listener);
            }).addOnFailureListener(e -> {
                listener.onComplete(null);
            });

        }).addOnFailureListener(e -> {
            listener.onComplete(null);
        });
    }

    public void removeMemberFromAlliance(String memberId) {
        db.collectionGroup("members")
                .whereEqualTo("userId", memberId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit();
                });
    }

    public void declineOtherInvites(String memberId, String acceptedInviteId) {
        db.collectionGroup("invites")
                .whereEqualTo("toUserId", memberId)
                .whereNotEqualTo("id", acceptedInviteId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        batch.update(doc.getReference(), "status", "DECLINED");
                    }
                    batch.commit();
                });
    }

    public void acceptInvite(String invitationId,String allianceId) {
        DocumentReference inviteRef = db.collection("alliances")
                .document(allianceId)
                .collection("invites")
                .document(invitationId);
        inviteRef.update("status", "ACCEPTED");
    }

    public void declineInvite(String invitationId, String allianceId) {
        DocumentReference inviteRef = db.collection("alliances")
                .document(allianceId)
                .collection("invites")
                .document(invitationId);

        inviteRef.update("status", "DECLINED");
    }
}
