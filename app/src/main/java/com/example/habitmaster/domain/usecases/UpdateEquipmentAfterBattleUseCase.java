package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.EquipmentRepository;
import com.example.habitmaster.domain.models.UserEquipment;
import java.util.List;

public class UpdateEquipmentAfterBattleUseCase {
    private final EquipmentRepository repo;

    public UpdateEquipmentAfterBattleUseCase(Context ctx) {
        this.repo = new EquipmentRepository(ctx);
    }

    public void execute(String userId) {
        List<UserEquipment> inventoryList = repo.getAllEquipmentForUser(userId);

       for(UserEquipment item : inventoryList) {
           if(item.isActivated() && item.getDuration() > 0) {
               repo.decrementDuration(item.getId());
           }
       }
    }
}
