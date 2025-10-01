package com.example.habitmaster.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.AllianceUserMission;

public class MemberDetailsDialogFragment extends DialogFragment {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_USER_MISSION = "userMission";

    public static MemberDetailsDialogFragment newInstance(String username, AllianceUserMission mission) {
        MemberDetailsDialogFragment dialog = new MemberDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putSerializable(ARG_USER_MISSION, mission); // klasa mora da implementira Serializable
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_member_progress_details, container, false);

        TextView tvUsername = v.findViewById(R.id.tvDialogUsername);
        TextView tvShopPurchases = v.findViewById(R.id.tvShopPurchases);
        TextView tvBossHits = v.findViewById(R.id.tvBossHits);
        TextView tvSolvedTasks = v.findViewById(R.id.tvSolvedTasks);
        TextView tvSolvedOtherTasks = v.findViewById(R.id.tvSolvedOtherTasks);
        TextView tvNoUnresolvedTasks = v.findViewById(R.id.tvNoUnresolvedTasks);
        TextView tvMessagesSentDays = v.findViewById(R.id.tvMessagesSentDays);
        TextView tvTotalDamage = v.findViewById(R.id.tvTotalDamage);

        String username = getArguments().getString(ARG_USERNAME);
        AllianceUserMission mission = (AllianceUserMission) getArguments().getSerializable(ARG_USER_MISSION);

        tvUsername.setText(username);

        if (mission != null) {
            tvShopPurchases.setText("Shop purchases: " + mission.getShopPurchases() + "/5");
            tvBossHits.setText("Boss fight hits: " + mission.getBossFightHits() + "/10");
            tvSolvedTasks.setText("Solved easier tasks: " + mission.getSolvedTasks() + "/10");
            tvSolvedOtherTasks.setText("Solved harder tasks: " + mission.getSolvedOtherTasks() + "/6");
            tvNoUnresolvedTasks.setText("No unresolved tasks: " + (mission.isNoUnresolvedTasks() ? "Yes (+10 HP)" : "No"));
            tvMessagesSentDays.setText("Messages sent days: " + mission.getMessagesSentDays());
            tvTotalDamage.setText("Total damage: " + mission.getTotalDamage());
        }

        return v;
    }
}
