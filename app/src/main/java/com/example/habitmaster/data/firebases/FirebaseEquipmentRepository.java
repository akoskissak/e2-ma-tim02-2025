package com.example.habitmaster.data.firebases;


import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.models.Weapon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseEquipmentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseEquipmentRepository() {
    }

    public void addEquipment(UserEquipment equipment, OnCompleteListener<Void> listener) {
        db.collection("equipment")
                .document(equipment.getId())
                .set(equipment)
                .addOnCompleteListener(listener);
    }

    public void addWeapon(Weapon weapon, OnCompleteListener<Void> listener) {
        db.collection("equipment")
                .document(weapon.getId())
                .set(weapon)
                .addOnCompleteListener(listener);
    }

    public void activateEquipment(String equipmetnId, boolean activated) {
        db.collection("equipment")
                .document(equipmetnId)
                .update("activated", activated ? 1 : 0);
    }

    public void decrementDuration(UserEquipment updated, String equipmentId) {
        if(updated != null) {
            db.collection("equipment")
                    .document(updated.getId())
                    .set(updated);
        } else {
            db.collection("equipment")
                    .document(equipmentId)
                    .delete();
        }
    }

    public void updateArmor(UserEquipment armor, OnCompleteListener<Void> listener) {
        db.collection("equipment")
                .document(armor.getId())
                .update("bonusValue", armor.getBonusValue())
                .addOnCompleteListener(listener);
    }

}
