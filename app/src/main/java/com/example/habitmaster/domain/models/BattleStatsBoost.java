package com.example.habitmaster.domain.models;

import androidx.annotation.NonNull;

public class BattleStatsBoost {
    // ovo su svi bonusi u procentima (ako postoje)
    public double tempPPIncrease = 0;
    public double permPPIncrease = 0;
    public double attackChanceIncrease = 0;
    public int extraAttackRolls = 0;
    public double coinsIncrease = 0;

    // prvo racuna za perm, a posle sa temp
    public double calculateFinalPP(double basePP) {
        double ppWithPerm = basePP;

        if(permPPIncrease != 0) {
            ppWithPerm *= (1 + permPPIncrease);
        }

        double finalPP = ppWithPerm;

        if(tempPPIncrease != 0) {
            finalPP *= (1 + tempPPIncrease);
        }

        return finalPP;
    }

    @NonNull
    @Override
    public String toString() {
        return "tempPP: " + tempPPIncrease +
                ", permPP: " + permPPIncrease +
                ", attackChance: " + attackChanceIncrease +
                ", extraAttackChance: " + extraAttackRolls +
                ", coins: " + coinsIncrease;
    }
}
