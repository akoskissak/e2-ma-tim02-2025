package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.BonusType;
import com.example.habitmaster.domain.models.EquipmentType;
import com.example.habitmaster.domain.models.UserEquipment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryActivateItemUseCase {
    private final EquipmentRepository repository;

    public InventoryActivateItemUseCase(Context ctx) {
        this.repository = new EquipmentRepository(ctx);
    }

    public void execute(UserEquipment item, List<UserEquipment> inventoryList) {
        if(item.isActivated()) return;
        EquipmentType type = item.getType();

        if(type == EquipmentType.POTION && item.getBonusType() == BonusType.TEMP_PP_INCREASE) {
                List<UserEquipment> activeTempPP = inventoryList.stream().filter(e -> e.getBonusType() == BonusType.TEMP_PP_INCREASE && e.isActivated()).collect(Collectors.toList());
                // deaktiviram postojeci
                if(!activeTempPP.isEmpty()) {
                    UserEquipment oldItem = activeTempPP.get(0);
                    repository.updateEquipmentActivated(oldItem.getId(), false);
                    oldItem.setActivated(false);
            }
        }

        item.setActivated(true);
        repository.updateEquipmentActivated(item.getId(), true);
    }
}
