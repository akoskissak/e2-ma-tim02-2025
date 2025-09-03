package com.example.habitmaster.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.DisplayEquipment;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {
    private final List<DisplayEquipment> equipmentList;
    private final OnBuyClickListener listener;

    public ShopAdapter(List<DisplayEquipment> equipmentList, OnBuyClickListener listener) {
        this.equipmentList = equipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayEquipment equipment = equipmentList.get(position);
        holder.icon.setImageResource(equipment.getIconResId());
        holder.nameText.setText(equipment.getName());
        holder.typeText.setText(equipment.getType().toString());
        holder.costText.setText(String.valueOf(equipment.getCostPercent()));
        holder.buyButton.setOnClickListener(v -> listener.onBuyClick(equipment));
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, typeText, costText;
        ImageView icon;
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
            nameText = itemView.findViewById(R.id.equipmentName);
            typeText = itemView.findViewById(R.id.equipmentType);
            costText = itemView.findViewById(R.id.equipmentCost);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }

    public interface OnBuyClickListener {
        void onBuyClick(DisplayEquipment equipment);
    }
}
