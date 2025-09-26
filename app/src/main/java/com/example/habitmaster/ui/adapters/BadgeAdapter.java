package com.example.habitmaster.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.BadgeDTO;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {

    private final Context context;
    private final List<BadgeDTO> badges;

    public BadgeAdapter(Context context, List<BadgeDTO> badges) {
        this.context = context;
        this.badges = badges;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        BadgeDTO badge = badges.get(position);

        int resId = context.getResources().getIdentifier(
                badge.imageName, "drawable", context.getPackageName()
        );
        if (resId != 0) {
            holder.imageBadge.setImageResource(resId);
        }

        holder.itemView.setOnClickListener(v -> showBadgeDialog(badge, badge.missionStartDate + " - " + badge.missionEndDate));
    }

    private void showBadgeDialog(BadgeDTO badge, String headerText) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_badge_details, null);

        ImageView badgeDetailImage = dialogView.findViewById(R.id.badgeDetailImage);
        TextView badgeDetailMission = dialogView.findViewById(R.id.badgeDetailMission);
        TextView badgeDetailInfo = dialogView.findViewById(R.id.badgeDetailInfo);

        int resId = context.getResources().getIdentifier(
                badge.imageName, "drawable", context.getPackageName()
        );
        if (resId != 0) {
            badgeDetailImage.setImageResource(resId);
        }

        badgeDetailMission.setText(headerText);

        String info = "Shop Purchases: " + badge.shopPurchases + "\n" +
                "Boss Hits: " + badge.bossFightHits + "\n" +
                "Solved Tasks: " + badge.solvedTasks + "\n" +
                "Other Tasks: " + badge.solvedOtherTasks + "\n" +
                "No Unresolved: " + (badge.noUnresolvedTasks ? "Yes" : "No") + "\n" +
                "Messages Sent: " + badge.messagesSentDays + "\n" +
                "Total Damage: " + badge.totalDamage;

        badgeDetailInfo.setText(info);

        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBadge;

        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBadge = itemView.findViewById(R.id.badgeImage);
        }
    }
}
