package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.usecases.AcceptAllianceInviteUseCase;
import com.example.habitmaster.domain.usecases.CreateAllianceUseCase;
import com.example.habitmaster.domain.usecases.DeclineAllianceInviteUseCase;
import com.example.habitmaster.domain.usecases.DeleteAllianceUseCase;
import com.example.habitmaster.domain.usecases.GetAllianceByUserIdUseCase;
import com.example.habitmaster.domain.usecases.GetAllianceInvitationByIdUseCase;
import com.example.habitmaster.domain.usecases.GetAllianceMembersUseCase;
import com.example.habitmaster.domain.usecases.GetAllianceUseCase;

import java.util.List;
import java.util.Set;

public class AllianceService {
    private final CreateAllianceUseCase createAllianceUC;
    private final GetAllianceUseCase getAllianceUC;
    private final GetAllianceByUserIdUseCase getAllianceByUserIdUC;
    private final DeleteAllianceUseCase deleteAllianceUC;
    private final GetAllianceMembersUseCase getAllianceMembersUC;
    private final GetAllianceInvitationByIdUseCase getAllianceInvitationByIdUC;
    private final AcceptAllianceInviteUseCase acceptAllianceInviteUC;
    private final DeclineAllianceInviteUseCase declineAllianceInviteUC;

    public AllianceService(Context ctx) {
        this.createAllianceUC = new CreateAllianceUseCase(ctx);
        this.getAllianceUC = new GetAllianceUseCase();
        this.getAllianceByUserIdUC = new GetAllianceByUserIdUseCase();
        this.deleteAllianceUC = new DeleteAllianceUseCase(ctx);
        this.getAllianceMembersUC = new GetAllianceMembersUseCase();
        this.getAllianceInvitationByIdUC = new GetAllianceInvitationByIdUseCase();
        this.acceptAllianceInviteUC = new AcceptAllianceInviteUseCase(ctx);
        this.declineAllianceInviteUC = new DeclineAllianceInviteUseCase(ctx);
    }

    public void createAlliance(String allianceName, String leaderId, String leaderUsername, Set<String> memberIds, ICallback<String> callback) {
        createAllianceUC.execute(allianceName, leaderId, leaderUsername, memberIds, callback);
    }

    public void getAlliance(String allianceId, ICallback<Alliance> callback) {
        getAllianceUC.execute(allianceId, callback);
    }

    public void getAllianceByUserId(String userId, ICallback<Alliance> callback) {
        getAllianceByUserIdUC.execute(userId, callback);
    }

    public void deleteAlliance(String leaderId, ICallbackVoid callback) {
        deleteAllianceUC.execute(leaderId, callback);
    }

    public void getAllianceMembers(String allianceId, ICallback<List<String>> callback) {
        getAllianceMembersUC.execute(allianceId, callback);
    }

    public void getAllianceInvitationById(String allianceInvitationId, String allianceId, ICallback<AllianceInvitation> callback) {
        getAllianceInvitationByIdUC.execute(allianceInvitationId, allianceId, callback);
    }

    public void acceptAllianceInvite(String inviteId, String userId, String allianceId) {
        acceptAllianceInviteUC.execute(inviteId, userId, allianceId);
    }

    public void declineAllianceInvite(String inviteId, String allianceId) {
        declineAllianceInviteUC.execute(inviteId, allianceId);
    }
}
