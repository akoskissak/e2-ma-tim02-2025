package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.data.firebases.FirebaseEquipmentRepository;
import com.example.habitmaster.domain.models.Armor;
import com.example.habitmaster.domain.models.BonusType;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.models.EquipmentType;
import com.example.habitmaster.domain.models.Potion;
import com.example.habitmaster.domain.models.Weapon;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    private final DatabaseHelper helper;
    private final FirebaseEquipmentRepository firebaseRepo;

    public EquipmentRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
        this.firebaseRepo = new FirebaseEquipmentRepository();
    }

    public void addEquipment(UserEquipment equipment) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("id", equipment.getId());
            cv.put("userId", equipment.getUserId());
            cv.put("equipmentId", equipment.getEquipmentId());
            cv.put("name", equipment.getName());
            cv.put("type", equipment.getType().toString());
            cv.put("activated", equipment.isActivated() ? 1 : 0);
            cv.put("duration", equipment.getDuration());
            cv.put("bonusValue", equipment.getBonusValue());
            cv.put("bonusType", equipment.getBonusType().toString());
            db.insert(DatabaseHelper.T_EQUIPMENT, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        firebaseRepo.addEquipment(equipment, task -> {
            if(!task.isSuccessful()){
                Log.e("EquipmentRepository", "Greska pri dodavanju opreme", task.getException());
            }
        });
    }

    public void addWeapon(Weapon weapon) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", weapon.getId());
            cv.put("name", weapon.getName());
            cv.put("equipmentId", weapon.getEquipmentId());
            cv.put("type", weapon.getType().toString());
            cv.put("activated", weapon.isActivated() ? 1 : 0);
            cv.put("bonusType", weapon.getBonusType().toString());
            cv.put("bonusValue", weapon.getBonusValue());
            cv.put("duration", weapon.getDuration());
            cv.put("upgradeLevel", weapon.getUpgradeLevel());

            db.insert(DatabaseHelper.T_EQUIPMENT, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        firebaseRepo.addWeapon(weapon, task -> {
            if(!task.isSuccessful()){
                Log.e("EquipmentRepository", "Greska pri dodavanju oruzja", task.getException());
            }
        });
    }

    public void updateWeapon(Weapon weapon) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("upgradeLevel", weapon.getUpgradeLevel());
        cv.put("bonusValue", weapon.getBonusValue());

        db.update(DatabaseHelper.T_EQUIPMENT, cv, "id = ?", new String[]{weapon.getId()});
        db.close();
    }

    public void updateBonusValue(UserEquipment equipment) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("bonusValue", equipment.getBonusValue());

        db.update(DatabaseHelper.T_EQUIPMENT, cv, "id = ?", new String[]{equipment.getId()});
        db.close();
    }

    public List<UserEquipment> getAllEquipmentForUser(String userId) {
        List<UserEquipment> equipmentList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.T_EQUIPMENT, null, "userId = ?", new String[]{userId}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String equipmentId = cursor.getString(cursor.getColumnIndexOrThrow("equipmentId"));
                EquipmentType type = EquipmentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                boolean activated = cursor.getInt(cursor.getColumnIndexOrThrow("activated")) == 1;
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
                double bonusValue = cursor.getDouble(cursor.getColumnIndexOrThrow("bonusValue"));
                BonusType bonusType = BonusType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("bonusType")));
                UserEquipment equipment;
                if(type == EquipmentType.POTION) {
                    boolean isPermanent = duration == -1;
                    equipment = new Potion(name, bonusValue, isPermanent);
                } else if (type == EquipmentType.ARMOR) {
                    equipment = new Armor(name, bonusValue, bonusType);
                } else {
                    // weapon
                    Weapon weapon = new Weapon(name, bonusValue, bonusType);

                    int upgradeLevel = cursor.getInt(cursor.getColumnIndexOrThrow("upgradeLevel"));
                    weapon.setUpgradeLevel(upgradeLevel);

                    equipment = weapon;
                }

                equipment.setId(id);
                equipment.setEquipmentId(equipmentId);
                equipment.setUserId(userId);
                equipment.setActivated(activated);
                equipment.setDuration(duration);
                equipmentList.add(equipment);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return equipmentList;
    }

    public void updateEquipmentActivated(String equipmentId, boolean activated) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("activated", activated ? 1 : 0);

        db.update(DatabaseHelper.T_EQUIPMENT, cv, "id = ?", new String[]{equipmentId});
        db.close();

        firebaseRepo.activateEquipment(equipmentId, activated);
    }

    public void decrementDuration(String equipmentId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseHelper.T_EQUIPMENT, new String[]{"duration"}, "id = ?", new String[]{equipmentId}, null, null, null);

        if (cursor.moveToFirst()) {
            int currentDuration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            if(currentDuration > 0) {
                int newDuration = currentDuration - 1;
                ContentValues cv = new ContentValues();
                cv.put("duration", newDuration);

                db.update(DatabaseHelper.T_EQUIPMENT, cv, "id = ?", new String[]{equipmentId});

                if(newDuration == 0) {
                    deleteEquipment(equipmentId);
                }
            }
        }

        cursor.close();
        db.close();

        UserEquipment updated = getEquipmentById(equipmentId);
        if(updated != null) {
            firebaseRepo.decrementDuration(updated, equipmentId);
        }
    }

    public void deleteEquipment(String equipmentId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.T_EQUIPMENT, "id = ?", new String[]{equipmentId});
        db.close();
    }

    public UserEquipment getEquipmentById(String equipmentId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.T_EQUIPMENT, null, "id = ?", new String[]{equipmentId}, null, null, null);

        UserEquipment equipment = null;
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            EquipmentType type = EquipmentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("type")));
            boolean activated = cursor.getInt(cursor.getColumnIndexOrThrow("activated")) == 1;
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            double bonusValue = cursor.getDouble(cursor.getColumnIndexOrThrow("bonusValue"));
            BonusType bonusType = BonusType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("bonusType")));

            if (type == EquipmentType.POTION) {
                boolean isPermanent = duration == -1;
                equipment = new Potion(name, bonusValue, isPermanent);
            } else if (type == EquipmentType.ARMOR) {
                equipment = new Armor(name, bonusValue, bonusType);
            } else {
                // weapon
                equipment = new Weapon(name, bonusValue, bonusType);
            }

            equipment.setId(id);
            equipment.setActivated(activated);
            equipment.setDuration(duration);
        }

        cursor.close();
        db.close();
        return equipment;
    }

    public void updateArmor(UserEquipment armor) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("bonusValue", armor.getBonusValue());

            db.update(
                    DatabaseHelper.T_EQUIPMENT,
                    cv,
                    "id = ?",
                    new String[]{armor.getId()}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        firebaseRepo.updateArmor(armor, task -> {
            if (!task.isSuccessful()) {
                Log.e("EquipmentRepository", "Greška pri ažuriranju armora", task.getException());
            }
        });
    }

}
