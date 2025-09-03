package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.Weapon;

public class UpgradeWeaponUseCase {
    private final EquipmentRepository repo;

    public UpgradeWeaponUseCase(Context ctx) {
        this.repo = new EquipmentRepository(ctx);
    }

    public void execute(Weapon weapon) {
        repo.updateWeapon(weapon);
    }
}
