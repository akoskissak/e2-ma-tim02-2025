package com.example.habitmaster.domain.models;

public class DisplayEquipment extends Equipment {
    private final Equipment original;

    public DisplayEquipment(Equipment original, int calculatedCost) {
        super(original);
        this.original = original;
        setCostPercent(calculatedCost);
    }

    public Equipment getOriginal() {
        return original;
    }
}
