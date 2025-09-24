package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.AllianceMissionProgressType;
import com.example.habitmaster.domain.models.AllianceMissionStatus;
import com.example.habitmaster.domain.models.AllianceUserMission;
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
    private final AllianceMissionService allianceMissionService;
    private final AllianceUserMissionService allianceUserMissionService;
    private final HasUserSentMessageTodayUseCase hasUserSentMessageTodayUC;

    public AllianceService(Context ctx) {
        this.createAllianceUC = new CreateAllianceUseCase(ctx);
        this.getAllianceUC = new GetAllianceUseCase();
        this.getAllianceByUserIdUC = new GetAllianceByUserIdUseCase();
        this.deleteAllianceUC = new DeleteAllianceUseCase(ctx);
        this.getAllianceMembersUC = new GetAllianceMembersUseCase();
        this.getAllianceInvitationByIdUC = new GetAllianceInvitationByIdUseCase();
        this.acceptAllianceInviteUC = new AcceptAllianceInviteUseCase(ctx);
        this.declineAllianceInviteUC = new DeclineAllianceInviteUseCase(ctx);
        this.allianceMissionService = new AllianceMissionService(ctx);
        this.allianceUserMissionService = new AllianceUserMissionService(ctx);
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

    public void tryUpdateAllianceProgress(String userId, AllianceMissionProgressType progressType) {
        getAllianceByUserId(userId, new ICallback<Alliance>() {
            @Override
            public void onSuccess(Alliance alliance) {
                if (!alliance.isMissionStarted()) {
                    return;
                }

                allianceMissionService.getOngoingAllianceMissionByAllianceId(alliance.getId(), new ICallback<AllianceMission>() {
                    @Override
                    public void onSuccess(AllianceMission allianceMission) {
                        var userMission = allianceUserMissionService.getByUserIdAndMissionId(userId, allianceMission.getId());
                        if (userMission == null) {
                            return;
                        }

                        switch (progressType) {
                            case SHOP_PURCHASE:
                                if (userMission.tryIncreaseShopPurchases()) {
                                    userMission.calculateTotalDamage();
                                    allianceUserMissionService.update(userMission);
                                    allianceMission.decreaseCurrentHp(AllianceUserMission.DAMAGE_SHOP_PURCHASE);
                                }
                                break;
                            case BOSS_FIGHT_HIT:
                                if (userMission.tryIncreaseBossFightHits()) {
                                    userMission.calculateTotalDamage();
                                    allianceUserMissionService.update(userMission);
                                    allianceMission.decreaseCurrentHp(AllianceUserMission.DAMAGE_BOSS_FIGHT_HIT);
                                }
                                break;
                            case SOLVED_TASK1:
                                if (userMission.tryIncreaseSolvedTasks1()) {
                                    userMission.calculateTotalDamage();
                                    allianceUserMissionService.update(userMission);
                                    allianceMission.decreaseCurrentHp(AllianceUserMission.DAMAGE_SOLVED_TASK);
                                }
                                break;
                            case SOLVED_TASK2:
                                if (userMission.tryIncreaseSolvedTasks2()) {
                                    userMission.calculateTotalDamage();
                                    allianceUserMissionService.update(userMission);
                                    allianceMission.decreaseCurrentHp(2 * AllianceUserMission.DAMAGE_SOLVED_TASK);
                                }
                                break;
                            case SOLVED_OTHER_TASK:
                                if (userMission.tryIncreaseSolvedOtherTasks()) {
                                    userMission.calculateTotalDamage();
                                    allianceUserMissionService.update(userMission);
                                    allianceMission.decreaseCurrentHp(AllianceUserMission.DAMAGE_SOLVED_OTHER_TASK);
                                }
                                break;
                            case MESSAGE_SENT:
                                userMission.setMessagesSentDays(userMission.getMessagesSentDays() + 1);
                                userMission.calculateTotalDamage();
                                allianceUserMissionService.update(userMission);
                                allianceMission.decreaseCurrentHp(AllianceUserMission.DAMAGE_MESSAGE_SENT);
                                break;
                        }

                        allianceMissionService.update(allianceMission);
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void startAllianceMission(String leaderId, ICallback<AllianceMission> callback) {
        getAllianceByUserId(leaderId, new ICallback<Alliance>() {
            @Override
            public void onSuccess(Alliance alliance) {
                if (!alliance.getLeaderId().equals(leaderId)) {
                    callback.onError("User is not a leader of alliance");
                    return;
                }

                allianceMissionService.startAllianceMission(alliance, callback);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
