package com.example.habitmaster.data.firebases;

import android.util.Log;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.models.AllianceInviteStatus;
import com.example.habitmaster.services.AllianceChatListenerService;
import com.example.habitmaster.services.AllianceInviteListenerService;
import com.example.habitmaster.services.AllianceMemberListenerService;
import com.example.habitmaster.services.ICallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseAllianceRepository {
    private final FirebaseFirestore db;
    private ListenerRegistration inviteListener;
    private ListenerRegistration messageListener;
    private ListenerRegistration messageSubListener;
    private ListenerRegistration memberListener;
    private ListenerRegistration membersSubListener;

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

    public Task<Void> addMemberToAlliance(String allianceId, String memberId) {
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("userId", memberId);

        return db.collection("alliances")
                .document(allianceId)
                .collection("members")
                .document(memberId)
                .set(memberData)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseRepo", "Member " + memberId + " dodan u alliance " + allianceId))
                .addOnFailureListener(e -> Log.e("FirebaseRepo", "Greška pri dodavanju membera " + memberId, e));
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

    public Task<Void> removeMemberFromAlliance(String memberId) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        db.collectionGroup("members")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        tcs.setResult(null);
                        return;
                    }

                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String userIdField = doc.getString("userId");
                        if (memberId.equals(doc.getId()) || memberId.equals(userIdField)) {
                            batch.delete(doc.getReference());
                        }
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FirebaseRepo", "Svi odgovarajući members obrisani");
                                tcs.setResult(null);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirebaseRepo", "Greška pri brisanju members", e);
                                tcs.setException(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseRepo", "Greška pri dohvatanju members", e);
                    tcs.setException(e);
                });
        return tcs.getTask();
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

    public void acceptInvite(String invitationId, String allianceId) {
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

    public void getAllianceById(String allianceId, OnSuccessListener<Alliance> onSuccess, OnFailureListener onFailure) {
        DocumentReference docRef = db.collection("alliances").document(allianceId);

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        boolean missionStarted = documentSnapshot.getBoolean("missionStarted") != null
                                && documentSnapshot.getBoolean("missionStarted");

                        Alliance alliance = new Alliance(
                                documentSnapshot.getString("id"),
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("leaderId"),
                                missionStarted
                        );
                        onSuccess.onSuccess(alliance);
                    } else {
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void getAllianceByUserId(String userId, OnSuccessListener<Alliance> onSuccess) {
        Log.d("Pr", "provera za userid: " + userId);
        db.collection("alliances")
                .whereEqualTo("leaderId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(leaderSnapshot -> {

                    if (!leaderSnapshot.isEmpty()) {
                        Log.d("Pr", "Našao alliance gde je user leader");
                        DocumentSnapshot allianceDoc = leaderSnapshot.getDocuments().get(0);
                        Alliance alliance = toAlliance(allianceDoc);
                        onSuccess.onSuccess(alliance);
                    } else {
                        Log.d("Pr", "Nije leader, proveravam members...");
                        db.collectionGroup("members")
                                .get()
                                .addOnSuccessListener(memberSnapshot -> {
                                    DocumentSnapshot foundDoc = null;

                                    for (DocumentSnapshot doc : memberSnapshot.getDocuments()) {
                                        Log.d("DEBUG", "Member doc id=" + doc.getId() + ", userId=" + doc.getString("userId"));

                                        if (userId.equals(doc.getString("userId")) || userId.equals(doc.getId())) {
                                            foundDoc = doc;
                                            break;
                                        }
                                    }

                                    if (foundDoc != null) {
                                        // parent of parent = alliance doc
                                        DocumentReference allianceRef = foundDoc.getReference().getParent().getParent();
                                        allianceRef.get().addOnSuccessListener(allianceDoc -> {
                                            Alliance alliance = toAlliance(allianceDoc);
                                            onSuccess.onSuccess(alliance);
                                        });
                                    } else {
                                        onSuccess.onSuccess(null);
                                    }
                                });
                    }
                });

    }

    private Alliance toAlliance(DocumentSnapshot doc) {
        boolean missionStarted = doc.getBoolean("missionStarted") != null && doc.getBoolean("missionStarted");
        return new Alliance(
                doc.getId(),
                doc.getString("name"),
                doc.getString("leaderId"),
                missionStarted
        );
    }

    public void getAllianceInvitationById(String invitationId, String allianceId, ICallback<AllianceInvitation> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("alliances")
                .document(allianceId)
                .collection("invites")
                .document(invitationId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && AllianceInviteStatus.PENDING.name().equals(doc.getString("status"))) {
                        AllianceInvitation invitation = new AllianceInvitation();
                        invitation.setId(doc.getId());
                        invitation.setAllianceId(allianceId);
                        invitation.setFromUserId(doc.getString("fromUserId"));
                        invitation.setToUserId(doc.getString("toUserId"));
                        invitation.setStatus(AllianceInviteStatus.PENDING);

                        callback.onSuccess(invitation);
                    } else {
                        callback.onError("Invitation not found or not pending");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void startInviteListener(String currentUserId, AllianceInviteListenerService service) {
        inviteListener = db.collectionGroup("invites")
                .whereEqualTo("toUserId", currentUserId)
                .whereEqualTo("status", AllianceInviteStatus.PENDING.name())
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("AllianceRepo", "Invite listener error", e);
                        return;
                    }
                    if (querySnapshot == null || querySnapshot.isEmpty()) return;

                    for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            processInvite(change.getDocument(), currentUserId, service);
                        }
                    }
                });
    }

    private void processInvite(DocumentSnapshot inviteDoc, String currentUserId, AllianceInviteListenerService service) {
        String fromUserId = inviteDoc.getString("fromUserId");
        String toUserId = inviteDoc.getString("toUserId");
        String inviteId = inviteDoc.getId();
        String allianceId = inviteDoc.getReference().getParent().getParent().getId();

        if (fromUserId != null && Objects.equals(toUserId, currentUserId)) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(fromUserId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        String fromUsername = userDoc.getString("username");
                        if (fromUsername == null) fromUsername = "Nepoznat korisnik";

                        service.showInviteNotification(inviteId, allianceId, fromUsername);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseRepo", "Greška pri dohvatanju username-a: ", e);
                        service.showInviteNotification(inviteId, allianceId, "Nepoznat korisnik");
                    });
        } else {
            service.showInviteNotification(inviteId, allianceId, "Nepoznat korisnik");
        }
    }

    public void stopInviteListener() {
        if (inviteListener != null) {
            inviteListener.remove();
            inviteListener = null;
            Log.d("FirebaseRepo", "Invite listener removed");
        }
    }

    public void startAllianceMembersListener(String currentUserId, AllianceMemberListenerService service) {
        memberListener = db.collection("alliances")
                .whereEqualTo("leaderId", currentUserId)
                .limit(1)
                .addSnapshotListener((alliancesSnapshot, e) -> {
                    if (e != null) {
                        Log.e("AllianceRepo", "Greška u alliances listeneru", e);
                        return;
                    }
                    if (alliancesSnapshot == null || alliancesSnapshot.isEmpty()) {
                        Log.d("AllianceRepo", "Nema alliances gde je leaderId = " + currentUserId);
                        return;
                    }
                    Log.d("AllianceRepo", "Prosao sam dalje:  " + currentUserId);

                    DocumentSnapshot allianceDoc = alliancesSnapshot.getDocuments().get(0);
                    String allianceId = allianceDoc.getId();
                    Log.d("AllianceRepo", "Pronađen alliance za leadera: " + allianceId);

                    membersSubListener = allianceDoc.getReference()
                            .collection("members")
                            .addSnapshotListener((membersSnapshot, ex) -> {
                                if (ex != null) {
                                    Log.e("AllianceRepo", "Greška u members listeneru", ex);
                                    return;
                                }
                                if (membersSnapshot == null) {
                                    Log.w("AllianceRepo", "Members snapshot null");
                                    return;
                                }

                                if (!currentUserId.equals(allianceDoc.getString("leaderId"))) {
                                    Log.d("AllianceRepo", "Trenutni user nije leader, ne startujem members listener.");
                                    return;
                                }

                                for (DocumentChange change : membersSnapshot.getDocumentChanges()) {
                                    if (change.getType() == DocumentChange.Type.ADDED) {
                                        String newMemberUserId = change.getDocument().getString("userId");
                                        Log.d("AllianceRepo", "Novi član dodan, userId=" + newMemberUserId);

                                        if (newMemberUserId == null || newMemberUserId.isEmpty()) {
                                            Log.w("AllianceRepo", "Nema userId u member dokumentu");
                                            continue;
                                        }

                                        String leaderId = allianceDoc.getString("leaderId");
                                        if (!currentUserId.equals(leaderId)) {
                                            Log.d("AllianceRepo", "Trenutni user nije leader, preskačem notifikaciju.");
                                            continue;
                                        }

                                        db.collection("users")
                                                .whereEqualTo("id", newMemberUserId)
                                                .get()
                                                .addOnSuccessListener(querySnapshot -> {
                                                    String newMemberName = "Nepoznat korisnik";
                                                    if (!querySnapshot.isEmpty()) {
                                                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                                                        String username = userDoc.getString("username");
                                                        if (username != null)
                                                            newMemberName = username;
                                                    }
                                                    Log.d("AllianceRepo", "Pronađen username=" + newMemberName);

                                                    service.showLeaderNewMemberNotification(
                                                            allianceId,
                                                            leaderId,
                                                            newMemberName
                                                    );
                                                })
                                                .addOnFailureListener(e2 -> {
                                                    Log.e("AllianceRepo", "Greška kod dohvatanja usera: " + newMemberUserId, e2);
                                                });
                                    }
                                }
                            });
                });
    }

    public void stopMemberListener() {
        if (memberListener != null) {
            memberListener.remove();
            memberListener = null;
            Log.d("FirebaseRepo", "Member listener removed");
        }
        if (membersSubListener != null) {
            membersSubListener.remove();
            membersSubListener = null;
            Log.d("FirebaseRepo", "Members listener removed");
        }
    }

    public void startChatListener(String currentUserId, AllianceChatListenerService service) {
        messageListener = db.collection("alliances")
                .addSnapshotListener((alliancesSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (alliancesSnapshot == null) return;

                    for (DocumentSnapshot allianceDoc : alliancesSnapshot.getDocuments()) {
                        String leaderId = allianceDoc.getString("leaderId");

                        messageSubListener = allianceDoc.getReference()
                                .collection("messages")
                                .addSnapshotListener((messagesSnapshot, ex) -> {
                                    if (ex != null || messagesSnapshot == null) return;

                                    for (DocumentChange change : messagesSnapshot.getDocumentChanges()) {
                                        if (change.getType() == DocumentChange.Type.ADDED) {
                                            String senderId = change.getDocument().getString("senderId");
                                            String content = change.getDocument().getString("content");
                                            String senderUsername = change.getDocument().getString("senderUsername");
                                            String messageId = change.getDocument().getId();

                                            if (senderId == null || content == null) continue;
                                            Log.d("AllianceRepo", "Senderid: ," + senderId + "currentuserid: " + currentUserId);
                                            if (senderId.equals(currentUserId)) {
                                                continue;
                                            }

                                            allianceDoc.getReference()
                                                    .collection("members")
                                                    .document(currentUserId)
                                                    .get()
                                                    .addOnSuccessListener(memberDoc -> {
                                                        boolean isMember = memberDoc.exists();
                                                        boolean isLeader = leaderId != null && leaderId.equals(currentUserId);

                                                        if (isMember || isLeader) {
                                                            service.showMessageNotification(messageId, senderUsername, content);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }

    public void stopChatListener() {
        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
            Log.d("FirebaseRepo", "Chat listener removed");
        }
        if (messageSubListener != null) {
            messageSubListener.remove();
            messageSubListener = null;
            Log.d("FirebaseRepo", "Chat listener removed");
        }
    }

    public void getMemberUsernamesByAllianceId(String allianceId, OnSuccessListener<List<String>> onSuccess) {
        db.collection("alliances")
                .document(allianceId)
                .collection("members")
                .get()
                .addOnSuccessListener(memberSnapshot -> {
                    if (memberSnapshot.isEmpty()) {
                        onSuccess.onSuccess(new ArrayList<>());
                        return;
                    }

                    List<String> memberIds = new ArrayList<>();
                    for (DocumentSnapshot memberDoc : memberSnapshot.getDocuments()) {
                        String userId = memberDoc.getString("userId");
                        if (userId != null) memberIds.add(userId);
                    }


                    List<String> usernames = new ArrayList<>();
                    Task<List<String>> task = Tasks.forResult(new ArrayList<>());

                    for (String userId : memberIds) {
                        task = task.continueWithTask(t ->
                                db.collection("users")
                                        .document(userId)
                                        .get()
                                        .continueWith(userTask -> {
                                            DocumentSnapshot userDoc = userTask.getResult();
                                            if (userDoc != null && userDoc.exists()) {
                                                String username = userDoc.getString("username");
                                                if (username != null) usernames.add(username);
                                            }
                                            return null;
                                        })
                        );
                    }

                    task.addOnCompleteListener(t -> onSuccess.onSuccess(usernames));
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseRepo", "Greška pri dohvatanju members", e);
                    onSuccess.onSuccess(new ArrayList<>());
                });
    }

    public void getMemberIdsByAllianceId(String allianceId, OnSuccessListener<List<String>> onSuccess) {
        db.collection("alliances")
                .document(allianceId)
                .collection("members")
                .get()
                .addOnSuccessListener(memberSnapshot -> {
                    if (memberSnapshot.isEmpty()) {
                        onSuccess.onSuccess(new ArrayList<>());
                        return;
                    }

                    List<String> memberIds = new ArrayList<>();
                    for (DocumentSnapshot memberDoc : memberSnapshot.getDocuments()) {
                        String userId = memberDoc.getString("userId");
                        if (userId != null) {
                            memberIds.add(userId);
                        }
                    }

                    onSuccess.onSuccess(memberIds);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseRepo", "Greška pri dohvatanju memberId-jeva", e);
                    onSuccess.onSuccess(new ArrayList<>());
                });
    }


    public void updateMissionStarted(String allianceId, boolean missionStarted) {
        db.collection("alliances")
                .document(allianceId)
                .update("missionStarted", missionStarted)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseRepo", "missionStarted ažuriran na " + missionStarted + " za allianceId=" + allianceId))
                .addOnFailureListener(e -> Log.e("FirebaseRepo", "Greška pri ažuriranju missionStarted za allianceId=" + allianceId, e));
    }

}
