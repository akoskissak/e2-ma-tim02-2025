package com.example.habitmaster.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.UserEquipmentService;

import java.util.List;

public class ActiveEquipmentAdapter extends RecyclerView.Adapter<ActiveEquipmentAdapter.ActiveViewHolder> {

    private final List<UserEquipment> activeEquipmentList;
    private final UserEquipmentService equipmentService;
    private OnDeactivateClickListener listener;

    public ActiveEquipmentAdapter(List<UserEquipment> activeEquipmentList, UserEquipmentService equipmentService, OnDeactivateClickListener listener) {
        this.activeEquipmentList = activeEquipmentList;
        this.equipmentService = equipmentService;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_equipment, parent, false);
        return new ActiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveViewHolder holder, int position) {
        UserEquipment userEquipment = activeEquipmentList.get(position);

        holder.equipmentName.setText(userEquipment.getName());
        holder.equipmentIcon.setImageResource(equipmentService.getIconForEquipment(userEquipment));
        holder.durationText.setText(userEquipment.formatDuration());

        holder.equipmentIcon.setOnClickListener(v -> {
            if(listener != null) listener.onDeactivate(userEquipment);
        });
    }

    @Override
    public int getItemCount() {
        return activeEquipmentList.size();
    }

    public void updateItems(List<UserEquipment> newItems) {
        activeEquipmentList.clear();
        activeEquipmentList.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ActiveViewHolder extends RecyclerView.ViewHolder {
        ImageView equipmentIcon;
        TextView equipmentName, durationText;

        public ActiveViewHolder(@NonNull View itemView) {
            super(itemView);
            equipmentIcon = itemView.findViewById(R.id.equipmentIcon);
            equipmentName = itemView.findViewById(R.id.equipmentName);
            durationText = itemView.findViewById(R.id.activeDurationText);
        }
    }

    public interface OnDeactivateClickListener {
        void onDeactivate(UserEquipment equipment);
    }
}

