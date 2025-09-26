package com.example.habitmaster.domain.usecases.users;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseBadgeRepository;
import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.data.repositories.BadgeRepository;
import com.example.habitmaster.domain.models.AllianceUserMission;
import com.example.habitmaster.domain.models.Badge;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

import java.util.List;
import java.util.UUID;

public class AddBadgesAndHalfBossRewardUseCase {

    private final FirebaseUserRepository repository;
    private final BadgeRepository localBadgeRepo;
    private final FirebaseBadgeRepository remoteBadgeRepo;

    public AddBadgesAndHalfBossRewardUseCase(Context context) {
        this.repository = new FirebaseUserRepository(context);
        this.localBadgeRepo = new BadgeRepository(context);
        this.remoteBadgeRepo = new FirebaseBadgeRepository();
    }

    public void execute(List<String> memberIds, List<AllianceUserMission> allianceUserMissions, int bossMaxHp) {
        for (String id: memberIds) {
            repository.getUserById(id, new ICallback<User>() {
                @Override
                public void onSuccess(User user) {
                    user.setBadgesCount(user.getBadgesCount() + 1);
                    user.setCoins(user.getCoins() + Boss.calculateHalfNextReward(user.getLevel()));
                    repository.update(user);

                    AllianceUserMission mission = null;
                    for (AllianceUserMission m : allianceUserMissions) {
                        if (m.getUserId().equals(id)) {
                            mission = m;
                            break;
                        }
                    }

                    if (mission != null) {
                        String avatarName = determineAvatarName(mission.getTotalDamage(), bossMaxHp);

                        Badge badge = new Badge(
                                UUID.randomUUID().toString(),
                                mission.getUserId(),
                                mission.getMissionId(),
                                avatarName,
                                mission.getShopPurchases(),
                                mission.getBossFightHits(),
                                mission.getSolvedTasks(),
                                mission.getSolvedOtherTasks(),
                                mission.isNoUnresolvedTasks(),
                                mission.getMessagesSentDays(),
                                mission.getTotalDamage()
                        );

                        localBadgeRepo.insert(badge);
                        remoteBadgeRepo.insert(badge);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.d("Add badges and half boss reward", "onError: " + errorMessage);
                }
            });

        }
    }

    private String determineAvatarName(int totalDamage, int bossMaxHp) {
        if (bossMaxHp <= 0) return "badge1";

        double percentage = (totalDamage * 100.0) / bossMaxHp;

        if (percentage >= 80) return "badge5";
        else if (percentage >= 60) return "badge4";
        else if (percentage >= 40) return "badge3";
        else if (percentage >= 20) return "badge2";
        else return "badge1";
    }
}
