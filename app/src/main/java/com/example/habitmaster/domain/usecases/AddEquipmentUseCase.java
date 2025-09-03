package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.UserEquipment;

public class AddEquipmentUseCase {
    private final EquipmentRepository repository;

    public AddEquipmentUseCase(Context ctx) {
        this.repository = new EquipmentRepository(ctx);
    }

    public void execute(UserEquipment equipment) {
        repository.addEquipment(equipment);
    }
}
