package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.services.ICallback;

public class GetAllianceInvitationByIdUseCase {
    private final FirebaseAllianceRepository repo;

    public GetAllianceInvitationByIdUseCase() {
        this.repo = new FirebaseAllianceRepository();
    }

    public void execute(String invitationId, String allianceId, ICallback<AllianceInvitation> callback) {
        repo.getAllianceInvitationById(invitationId, allianceId, new ICallback<>() {
            @Override
            public void onSuccess(AllianceInvitation invitation) {
                callback.onSuccess(invitation);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
