package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;

public class DeclineAllianceInviteUseCase {
    private final AllianceRepository repo;
    private final FirebaseAllianceRepository firebaseRepo;

    public DeclineAllianceInviteUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
        this.firebaseRepo = new FirebaseAllianceRepository();
    }

    public void execute(String invitationId, String allianceId) {
        repo.declineInvite(invitationId);
        firebaseRepo.declineInvite(invitationId, allianceId);
    }
}
