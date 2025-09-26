package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Equipment;
import com.example.habitmaster.domain.models.Shop;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.models.Weapon;
import com.example.habitmaster.domain.usecases.AddEquipmentUseCase;
import com.example.habitmaster.domain.usecases.DeactivateEquipmentUseCase;
import com.example.habitmaster.domain.usecases.GetAllUserEquipmentUseCase;
import com.example.habitmaster.domain.usecases.InventoryActivateItemUseCase;
import com.example.habitmaster.domain.usecases.UpdateEquipmentAfterBattleUseCase;
import com.example.habitmaster.domain.usecases.UpdateUserCoinsUseCase;
import com.example.habitmaster.domain.usecases.UpdateUserEquipmentUseCase;
import com.example.habitmaster.domain.usecases.UpgradeWeaponUseCase;
import com.example.habitmaster.utils.ShopUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserEquipmentService {
    private final GetAllUserEquipmentUseCase getAllUserEquipmentUC;
    private final InventoryActivateItemUseCase inventoryActivateItemUC;
    private final UpdateEquipmentAfterBattleUseCase updateEquipmentAfterBattleUC;
    private final DeactivateEquipmentUseCase deactivateEquipmentUC;
    private final UpgradeWeaponUseCase upgradeWeaponUC;
    private final UpdateUserCoinsUseCase updateUserCoinsUC;
    private final AddEquipmentUseCase addEquipmentUC;
    private final UpdateUserEquipmentUseCase updateUserEquipmentUC;

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
        this.addEquipmentUC = new AddEquipmentUseCase(ctx);
        this.updateUserEquipmentUC = new UpdateUserEquipmentUseCase(ctx);
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
        if (!(equipment instanceof Weapon)) {
            callback.onError("Oprema nije oruzje");
            return;
        }

        Weapon weapon = (Weapon) equipment;

        int calculatedCost = weapon.calculateUpgradeCost(user);
        int newCoins;
        if (user.getCoins() >= calculatedCost) {
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

    public void updateAfterBattle(String userId) {
        updateEquipmentAfterBattleUC.execute(userId);
    }

    public void deactivateEquipment(String equipmentId) {
        deactivateEquipmentUC.execute(equipmentId);
    }

    public void addRewardedWeapon(String userId, Equipment weapon, ICallback<UserEquipment> callback) {
        UserEquipment userWeaponEquipment = new UserEquipment(
                UUID.randomUUID().toString(),
                userId,
                weapon.getId(),
                weapon.getName(),
                weapon.getType(),
                false,
                weapon.getDuration(),
                weapon.getBonusValue(),
                weapon.getBonusType()) {
        };

        getAllUserEquipmentUC.execute(userId, new ICallback<List<UserEquipment>>() {
            @Override
            public void onSuccess(List<UserEquipment> allUserEquipments) {
                UserEquipment existingWeapon = allUserEquipments.stream()
                        .filter(eq -> eq.getEquipmentId().equals(weapon.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingWeapon != null) {
                    existingWeapon.setBonusValue(existingWeapon.getBonusValue() + 0.0002);
                    updateUserEquipmentUC.updateBonusValue(existingWeapon);
                    callback.onSuccess(existingWeapon);
                } else {
                    allUserEquipments.add(userWeaponEquipment);
                    addEquipmentUC.execute(userWeaponEquipment);
                    callback.onSuccess(userWeaponEquipment);
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void addRewardedArmor(String userId, String equipmentId, ICallback<UserEquipment> callback) {
        var shop = new Shop();
        var armor = shop.getItemById(equipmentId);

        UserEquipment userArmorEquipment = new UserEquipment(
                UUID.randomUUID().toString(),
                userId,
                armor.getId(),
                armor.getName(),
                armor.getType(),
                false,
                armor.getDuration(),
                armor.getBonusValue(),
                armor.getBonusType()) {
        };

        getAllUserEquipmentUC.execute(userId, new ICallback<List<UserEquipment>>() {
            @Override
            public void onSuccess(List<UserEquipment> allUserEquipments) {
                UserEquipment existingArmor = allUserEquipments.stream()
                        .filter(eq -> eq.getEquipmentId().equals(equipmentId))
                        .findFirst()
                        .orElse(null);

                if (existingArmor != null) {
                    existingArmor.setBonusValue(existingArmor.getBonusValue() + 0.1);
                    updateUserEquipmentUC.updateArmor(existingArmor);
                    callback.onSuccess(existingArmor);
                } else {
                    allUserEquipments.add(userArmorEquipment);
                    addEquipmentUC.execute(userArmorEquipment);
                    callback.onSuccess(userArmorEquipment);
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void addMissionRewards(List<String> allMembers) {
        Equipment[] equipments = ShopUtils.getRandomPotionAndArmor(new Shop());
        Equipment randomPotion = equipments[0];
        Equipment randomArmor = equipments[1];

        for (String userId : allMembers) {
            if (userId == null || userId.isEmpty()) continue;

            if (randomPotion != null) {
                UserEquipment potionEquipment = new UserEquipment(
                        UUID.randomUUID().toString(),
                        userId,
                        randomPotion.getId(),
                        randomPotion.getName(),
                        randomPotion.getType(),
                        false, // activated
                        randomPotion.getDuration(),
                        randomPotion.getBonusValue(),
                        randomPotion.getBonusType()
                ) {
                };
                addEquipmentUC.execute(potionEquipment);
            }

            if (randomArmor != null) {
                UserEquipment armorEquipment = new UserEquipment(
                        UUID.randomUUID().toString(),
                        userId,
                        randomArmor.getId(),
                        randomArmor.getName(),
                        randomArmor.getType(),
                        false,
                        randomArmor.getDuration(),
                        randomArmor.getBonusValue(),
                        randomArmor.getBonusType()
                ) {
                };
                addEquipmentUC.execute(armorEquipment);
            }
        }
    }
}
