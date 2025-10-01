package com.example.habitmaster.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.AllianceUserMission;

import java.util.List;
import java.util.Map;

public class MembersProgressAdapter extends RecyclerView.Adapter<MembersProgressAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String username, AllianceUserMission mission);
    }

    private List<AllianceUserMission> missions;
    private Map<String, String> userMap; // userId -> username
    private OnItemClickListener listener;

    public MembersProgressAdapter(List<AllianceUserMission> missions,
                                 Map<String, String> userMap,
                                 OnItemClickListener listener) {
        this.missions = missions;
        this.userMap = userMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllianceUserMission mission = missions.get(position);
        String username = userMap.getOrDefault(mission.getUserId(), "Unknown");

        holder.tvUsername.setText(username);
        holder.tvTotalDamage.setText(String.format("%d dmg", mission.calculateTotalDamage()));

        holder.btnMoreInfo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(username, mission);
            }
        });
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvTotalDamage;
        Button btnMoreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvMemberUsername);
            tvTotalDamage = itemView.findViewById(R.id.tvMemberDamage);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }

    }
}
