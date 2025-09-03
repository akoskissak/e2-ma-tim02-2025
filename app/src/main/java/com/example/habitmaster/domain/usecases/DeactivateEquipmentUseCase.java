package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;

public class DeactivateEquipmentUseCase {
    private final EquipmentRepository repo;

    public DeactivateEquipmentUseCase(Context ctx) {
        this.repo = new EquipmentRepository(ctx);
    }

    public void execute(String equipmentId) {
        repo.updateEquipmentActivated(equipmentId, false);
    }
}
