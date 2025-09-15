package com.example.habitmaster.domain.usecases;

import android.content.Context;

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

        firebaseRepo.removeMemberFromAlliance(userId);

        repo.declineOtherInvites(userId, inviteId);

        firebaseRepo.declineOtherInvites(userId, inviteId);

        repo.addMemberToAlliance(allianceId, userId);
        repo.acceptInvitation(inviteId);

        firebaseRepo.acceptInvite(inviteId, allianceId);
    }
}
