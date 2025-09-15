package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.services.ICallback;

public class GetAllianceInvitationByIdUseCase {
    private final AllianceRepository repo;

    public GetAllianceInvitationByIdUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
    }

    public void execute(String invitationId, ICallback<AllianceInvitation> callback) {
        AllianceInvitation invitation = repo.getAllianceInvitationById(invitationId);
        if(invitation == null) {
            callback.onError("Nema pozivnica za savez");
            return;
        }

        callback.onSuccess(invitation);
    }
}
