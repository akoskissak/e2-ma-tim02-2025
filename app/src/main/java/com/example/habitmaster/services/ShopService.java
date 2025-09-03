package com.example.habitmaster.services;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.domain.models.DisplayEquipment;
import com.example.habitmaster.domain.models.Equipment;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.usecases.AddEquipmentUseCase;
import com.example.habitmaster.domain.usecases.UpdateUserCoinsUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopService {
    private final User currentUser;
    private final AddEquipmentUseCase addEquipmentUC;
    private final UpdateUserCoinsUseCase updateUserCoinsUC;

    public ShopService(User user, Context ctx) {
        this.currentUser = user;
        this.addEquipmentUC = new AddEquipmentUseCase(ctx);
        this.updateUserCoinsUC = new UpdateUserCoinsUseCase(ctx);
    }

    public int calculateCost(Equipment equipment, int previousReward) {
        return (equipment.getCostPercent() * previousReward) / 100;
    }

    public boolean canAfford(int calculatedCost) {
        return currentUser.getCoins() >= calculatedCost;
    }

    public void buyItem(Equipment equipment, ICallback<UserEquipment> callback) {
        int calculatedCost = calculateCost(equipment, currentUser.getPreviousLevelReward());
        Log.d("BUYING", String.valueOf(calculatedCost));
        if (!canAfford(calculatedCost)) {
            callback.onError("You do not have enough coins to buy this item");
            return;
        }

        int newCoins = currentUser.getCoins() - calculatedCost;
        currentUser.setCoins(newCoins);

        UserEquipment userEquipment = new UserEquipment(
                UUID.randomUUID().toString(),
                currentUser.getId(),
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                false,
                equipment.getDuration(),
                equipment.getBonusValue(),
                equipment.getBonusType()
        ) {};


        updateUserCoinsUC.execute(currentUser.getId(),newCoins);
        addEquipmentUC.execute(userEquipment);

        callback.onSuccess(userEquipment);
    }

    public List<DisplayEquipment> getDisplayEquipmentList(List<Equipment> equipmentList) {
        List<DisplayEquipment> result = new ArrayList<>();
        for (Equipment eq : equipmentList) {
            int realCost = eq.calculateCost(currentUser);
            result.add(new DisplayEquipment(eq, realCost));
        }
        return result;
    }
}
