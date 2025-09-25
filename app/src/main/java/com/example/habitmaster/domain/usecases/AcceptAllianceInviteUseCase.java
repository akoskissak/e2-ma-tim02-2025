package com.example.habitmaster.domain.usecases;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;

public class AcceptAllianceInviteUseCase {
    private final AllianceRepository repo;
    private final FirebaseAllianceRepository firebaseRepo;

    public AcceptAllianceInviteUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
        this.firebaseRepo = new FirebaseAllianceRepository();
    }

    public void execute(String inviteId, String userId, String allianceId) {
        repo.leaveAlliance(userId);

        firebaseRepo.removeMemberFromAlliance(userId)
                .addOnSuccessListener(aVoid -> {

                    repo.addMemberToAlliance(allianceId, userId);
                    firebaseRepo.addMemberToAlliance(allianceId, userId)
                            .addOnSuccessListener(aVoid2 -> {

                                repo.declineOtherInvites(userId, inviteId);
                                firebaseRepo.declineOtherInvites(userId, inviteId);

                                repo.acceptInvitation(inviteId);
                                firebaseRepo.acceptInvite(inviteId, allianceId);
                            })
                            .addOnFailureListener(e -> Log.e("AllianceRepo", "Greška pri dodavanju membera", e));
                })
                .addOnFailureListener(e -> Log.e("AllianceRepo", "Greška pri brisanju starog membera", e));

    }
}
