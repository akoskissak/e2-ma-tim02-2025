package com.example.habitmaster.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.domain.models.Weapon;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserEquipmentService;


import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<UserEquipment> items;
    private final OnActivateClickListener activateClickListener;
    private final OnWeaponUpgradedListener weaponUpgradedListener;
    private final UserEquipmentService equipmentService;
    private final User currentUser;


    public InventoryAdapter(List<UserEquipment> items, UserEquipmentService equipmentService, User currentUser, OnActivateClickListener activateClickListener, OnWeaponUpgradedListener weaponUpgradedListener) {
        this.items = items;
        this.activateClickListener = activateClickListener;
        this.weaponUpgradedListener = weaponUpgradedListener;
        this.equipmentService = equipmentService;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        UserEquipment userEquipment = items.get(position);
        int icon = equipmentService.getIconForEquipment(userEquipment);
        holder.nameText.setText(userEquipment.getName());
        holder.icon.setImageResource(icon);
        holder.durationText.setText(userEquipment.formatDuration());


        if(userEquipment.isActivated()) {
            holder.activateButton.setText("Active");
            holder.activateButton.setEnabled(false);
        } else {
            holder.activateButton.setText("Activate");
            holder.activateButton.setEnabled(true);
        }

        holder.activateButton.setOnClickListener(v -> activateClickListener.onActivateClick(userEquipment));

        if (userEquipment instanceof Weapon) {
            holder.upgradeButton.setVisibility(View.VISIBLE);
            holder.upgradeButton.setOnClickListener(v -> showUpgradePopup(userEquipment, icon, holder.itemView.getContext()));
        } else {
            holder.upgradeButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, durationText;
        Button activateButton;
        ImageView icon, upgradeButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.inventoryItemIcon);
            nameText = itemView.findViewById(R.id.inventoryItemName);
            activateButton = itemView.findViewById(R.id.activateBtn);
            durationText = itemView.findViewById(R.id.durationText);
            upgradeButton = itemView.findViewById(R.id.upgradeButton);
        }
    }

    private void showUpgradePopup(UserEquipment equipment, int icon, Context context) {
        Weapon weapon = (Weapon) equipment;

        // Kreiraj Popup
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_upgrade_weapon, null);

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int popupWidth = (int) (screenWidth * 0.8);
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        final PopupWindow popup = new PopupWindow(popupView, popupWidth, popupHeight, true);

        TextView weaponName = popupView.findViewById(R.id.weaponName);
        ImageView weaponIcon = popupView.findViewById(R.id.weaponIcon);
        TextView levelText = popupView.findViewById(R.id.levelText);
        Button upgradeBtn = popupView.findViewById(R.id.upgradeBtn);
        TextView upgradeCostText = popupView.findViewById(R.id.upgradeCostText);

        levelText.setText("Level: " + weapon.getUpgradeLevel());
        weaponName.setText(weapon.getName());
        weaponIcon.setImageResource(icon);
        int cost = weapon.calculateUpgradeCost(currentUser);
        upgradeCostText.setText("Upgrade Cost: " + cost + " coins");

        upgradeBtn.setOnClickListener(v -> {
            equipmentService.upgradeWeapon(weapon, currentUser, new ICallback<>() {
                @Override
                public void onSuccess(Weapon result) {
                    levelText.setText("Level: " + weapon.getUpgradeLevel());
                    int newCoins = currentUser.getCoins();
                    Toast.makeText(context, "Weapon upgraded to level " + result.getUpgradeLevel(), Toast.LENGTH_SHORT).show();
                    if (weaponUpgradedListener != null) {
                        weaponUpgradedListener.onWeaponUpgraded(newCoins);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(context, "Upgrade failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });


        });

        popup.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public interface OnWeaponUpgradedListener {
        void onWeaponUpgraded(int newCoins);
    }

    public interface OnActivateClickListener {
        void onActivateClick(UserEquipment equipment);
    }
}

