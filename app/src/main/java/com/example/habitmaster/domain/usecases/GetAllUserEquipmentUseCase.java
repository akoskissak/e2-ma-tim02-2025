package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetAllUserEquipmentUseCase {
    private final EquipmentRepository repository;

    public GetAllUserEquipmentUseCase(Context ctx) {
        this.repository = new EquipmentRepository(ctx);
    }

    public void execute(String userId, ICallback<List<UserEquipment>> callback) {
        try {
            List<UserEquipment> userEquipment = repository.getAllEquipmentForUser(userId);
            callback.onSuccess(userEquipment);
        } catch (Exception e) {
            callback.onError("Failed to fetch equipments: " + e.getMessage());
        }
    }
}
