package com.example.habitmaster.data.dtos;

import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.UserEquipment;

public class BossFightResult {
    private final Boss boss;
    private final UserEquipment rewardedEquipment;
    private final boolean success;

    public BossFightResult(Boss boss, UserEquipment rewardedEquipment, boolean success) {
        this.boss = boss;
        this.rewardedEquipment = rewardedEquipment;
        this.success = success;
    }

    public Boss getBoss() {
        return boss;
    }

    public UserEquipment getRewardedEquipment() {
        return rewardedEquipment;
    }

    public boolean isSuccess() {
        return success;
    }
}
