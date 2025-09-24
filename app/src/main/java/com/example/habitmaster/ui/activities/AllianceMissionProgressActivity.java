package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.AllianceUserMission;
import com.example.habitmaster.services.AllianceMissionService;
import com.example.habitmaster.services.AllianceUserMissionService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.MembersProgressAdapter;
import com.example.habitmaster.ui.fragments.MemberDetailsDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllianceMissionProgressActivity extends AppCompatActivity {

    public static final String ALLIANCE_MISSION_ID = "alliance_mission_id";
    public static final String BOSS_MAX_HP = "boss_max_hp";
    public static final String BOSS_CURRENT_HP = "boss_current_hp";
    ProgressBar progressAllianceBar;
    TextView tvProgressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alliance_mission_progress);

        progressAllianceBar = findViewById(R.id.progressBarAlliance);
        tvProgressText = findViewById(R.id.tvProgressText);

        String missionId = getIntent().getStringExtra(ALLIANCE_MISSION_ID);

        loadData(missionId);
    }

    private void loadData(String missionId) {
        loadMission(missionId);

        AllianceUserMissionService allianceUserMissionService = new AllianceUserMissionService(this);
        allianceUserMissionService.getAllByMissionId(missionId, new ICallback<List<AllianceUserMission>>() {
            @Override
            public void onSuccess(List<AllianceUserMission> allianceUserMissions) {
                List<String> userIds = new ArrayList<>();
                for (AllianceUserMission mission : allianceUserMissions) {
                    userIds.add(mission.getUserId());
                }

                UserService userService = new UserService(AllianceMissionProgressActivity.this);
                userService.mapIdsToUsernames(userIds, new ICallback<Map<String, String>>() {
                    @Override
                    public void onSuccess(Map<String, String> userIdToUsernameMap) {
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMembersProgress);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AllianceMissionProgressActivity.this));

                        MembersProgressAdapter adapter = new MembersProgressAdapter(
                                allianceUserMissions,   // lista misija iz baze
                                userIdToUsernameMap,    // npr. { "u1" -> "Marko", "u2" -> "Jelena" }
                                (username, mission) -> {
                                    MemberDetailsDialogFragment dialog = MemberDetailsDialogFragment.newInstance(username, mission);
                                    dialog.show(getSupportFragmentManager(), "memberDetails");
                                }
                        );
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AllianceMissionProgressActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceMissionProgressActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMission(String missionId) {
        AllianceMissionService allianceMissionService = new AllianceMissionService(this);
        allianceMissionService.getMissionById(missionId, new ICallback<AllianceMission>() {
            @Override
            public void onSuccess(AllianceMission result) {
                int bossMaxHp = result.getBossMaxHp();
                int bossCurrentHp = result.getBossCurrentHp();

                progressAllianceBar.setProgress(bossCurrentHp / bossMaxHp);
                tvProgressText.setText(String.format("%d/%d dmg", bossMaxHp - bossCurrentHp, bossMaxHp));
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
}