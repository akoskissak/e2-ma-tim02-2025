package com.example.habitmaster.services;

import com.example.habitmaster.domain.models.BattleStatsBoost;
import com.example.habitmaster.domain.models.UserEquipment;

import java.util.List;

public class EquipmentEffectService {
    // todo: ovo ce racunati efekte sa kojima korisnik pokrece boss fight
    public BattleStatsBoost calculateEffects(List<UserEquipment> activeEquipment) {
        BattleStatsBoost boost = new BattleStatsBoost();

        for(UserEquipment item : activeEquipment) {
            if(!item.isActivated()) continue;

            switch (item.getBonusType()) {
                case TEMP_PP_INCREASE:
                    boost.tempPPIncrease += item.getBonusValue();
                    break;
                case PERM_PP_INCREASE:
                    boost.permPPIncrease += item.getBonusValue();
                    break;
                case ATTACK_CHANCE_INCREASE:
                    boost.attackChanceIncrease += item.getBonusValue();
                    break;
                case EXTRA_ATTACK_CHANCE:
                    // Svaka oprema ovog tipa daje sanse za jedan pokusaj za dodatni napad (40%)
                    boost.extraAttackRolls++;
                    break;
                case PERM_COINS_INCREASE:
                    boost.coinsIncrease += item.getBonusValue();
                    break;
            }
        }
        return boost;
    }
}
