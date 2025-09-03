package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.models.Weapon;
import com.example.habitmaster.domain.usecases.DeactivateEquipmentUseCase;
import com.example.habitmaster.domain.usecases.GetAllUserEquipmentUseCase;
import com.example.habitmaster.domain.usecases.InventoryActivateItemUseCase;
import com.example.habitmaster.domain.usecases.UpdateEquipmentAfterBattleUseCase;
import com.example.habitmaster.domain.usecases.UpdateUserCoinsUseCase;
import com.example.habitmaster.domain.usecases.UpgradeWeaponUseCase;

import java.util.List;
import java.util.Map;

public class UserEquipmentService {
    private final GetAllUserEquipmentUseCase getAllUserEquipmentUC;
    private final InventoryActivateItemUseCase inventoryActivateItemUC;
    private final UpdateEquipmentAfterBattleUseCase updateEquipmentAfterBattleUC;
    private final DeactivateEquipmentUseCase deactivateEquipmentUC;
    private final UpgradeWeaponUseCase upgradeWeaponUC;
    private final UpdateUserCoinsUseCase updateUserCoinsUC;

    private static final Map<String, Integer> equipmentIconMap = Map.of(
            "sword", R.drawable.sword,
            "bow_and_arrow", R.drawable.bow_and_arrow,
            "shield", R.drawable.shield,
            "potionPerm5", R.drawable.potion5,
            "potionPerm10", R.drawable.potion10,
            "potion20", R.drawable.potion20,
            "potion40", R.drawable.potion40,
            "boots", R.drawable.boots,
            "gloves", R.drawable.gloves
    );

    public UserEquipmentService(Context ctx) {
        this.getAllUserEquipmentUC = new GetAllUserEquipmentUseCase(ctx);
        this.inventoryActivateItemUC = new InventoryActivateItemUseCase(ctx);
        this.updateEquipmentAfterBattleUC = new UpdateEquipmentAfterBattleUseCase(ctx);
        this.deactivateEquipmentUC = new DeactivateEquipmentUseCase(ctx);
        this.upgradeWeaponUC = new UpgradeWeaponUseCase(ctx);
        this.updateUserCoinsUC = new UpdateUserCoinsUseCase(ctx);
    }

    public void getAllUserEquipment(String userId, ICallback<List<UserEquipment>> callback) {
        getAllUserEquipmentUC.execute(userId, callback);
    }

    public void activateItem(UserEquipment item, List<UserEquipment> inventoryList) {
        inventoryActivateItemUC.execute(item, inventoryList);
    }

    public int getIconForEquipment(UserEquipment userEquipment) {
        return equipmentIconMap.getOrDefault(userEquipment.getEquipmentId(), R.drawable.default_equipment);
    }

    public void upgradeWeapon(UserEquipment equipment, User user, ICallback<Weapon> callback) {
        if(!(equipment instanceof Weapon)){
            callback.onError("Oprema nije oruzje");
            return;
        }

        Weapon weapon = (Weapon) equipment;

        int calculatedCost = weapon.calculateUpgradeCost(user);
        int newCoins;
        if(user.getCoins() >= calculatedCost){
            newCoins = user.getCoins() - calculatedCost;
            user.setCoins(newCoins);
        } else {
            callback.onError("Not enough coins");
            return;
        }

        updateUserCoinsUC.execute(user.getId(), newCoins);

        weapon.upgrade();

        upgradeWeaponUC.execute(weapon);

        callback.onSuccess(weapon);
    }

    // todo: ovo treba da se pozove na kraju funkcije finishBattle()
    public void updateAfterBattle(String userId){
        updateEquipmentAfterBattleUC.execute(userId);
    }

    public void deactivateEquipment(String equipmentId) {
        deactivateEquipmentUC.execute(equipmentId);
    }

}
