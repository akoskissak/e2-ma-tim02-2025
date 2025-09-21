package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.UserEquipment;

public class UpdateUserEquipmentUseCase {
    private final EquipmentRepository localRepo;

    public UpdateUserEquipmentUseCase(Context context) {
        this.localRepo = new EquipmentRepository(context);
    }

    public void updateBonusValue(UserEquipment equipment) {
        localRepo.updateBonusValue(equipment);
    }

    public void updateArmor(UserEquipment armor) {
        localRepo.updateArmor(armor);
    }
}
