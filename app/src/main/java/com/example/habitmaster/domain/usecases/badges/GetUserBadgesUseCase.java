package com.example.habitmaster.domain.usecases.badges;

import android.content.Context;

import com.example.habitmaster.data.dtos.BadgeDTO;
import com.example.habitmaster.data.repositories.AllianceMissionRepository;
import com.example.habitmaster.data.repositories.BadgeRepository;
import com.example.habitmaster.domain.models.Badge;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetUserBadgesUseCase {
    private final BadgeRepository localRepo;
    private final AllianceMissionRepository localMissionRepo;

    public GetUserBadgesUseCase(Context context) {
        this.localRepo = new BadgeRepository(context);
        this.localMissionRepo = new AllianceMissionRepository(context);
    }

    public List<BadgeDTO> getAllByUserId(String userId) {
        var badges = localRepo.getAllByUserId(userId);
        List<BadgeDTO> displayBadges = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Badge b : badges) {
            var mission = localMissionRepo.getById(b.getMissionId());

            BadgeDTO dto = new BadgeDTO();
            dto.id = b.getId();
            dto.userId = b.getUserId();
            dto.imageName = b.getImageName();
            dto.shopPurchases = b.getShopPurchases();
            dto.bossFightHits = b.getBossFightHits();
            dto.solvedTasks = b.getSolvedTasks();
            dto.solvedOtherTasks = b.getSolvedOtherTasks();
            dto.noUnresolvedTasks = b.isNoUnresolvedTasks();
            dto.messagesSentDays = b.getMessagesSentDays();
            dto.totalDamage = b.getTotalDamage();

            if (mission != null) {
                dto.missionStartDate = mission.getStartDateTime() != null
                        ? mission.getStartDateTime().format(formatter)
                        : null;
                dto.missionEndDate = mission.getEndDateTime() != null
                        ? mission.getEndDateTime().format(formatter)
                        : null;
            }

            displayBadges.add(dto);
        }

        return displayBadges;
    }
}
