package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ICallbackVoid;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.AllianceMembersAdapter;
import com.example.habitmaster.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class AllianceActivity extends AppCompatActivity {
    private TextView tvAllianceName, tvLeaderName, tvMembersTitle, tvLeaderTitle;
    private RecyclerView recyclerViewMembers;
    private AllianceMembersAdapter membersAdapter;
    private Button btnDeleteAlliance;
    private Alliance currentAlliance;
    private String currentUserId;
    private List<String> membersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alliance);

        tvAllianceName = findViewById(R.id.tvAllianceName);
        tvLeaderName = findViewById(R.id.tvLeaderName);
        btnDeleteAlliance = findViewById(R.id.btnDeleteAlliance);
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        tvMembersTitle = findViewById(R.id.tvMembersTitle);
        tvLeaderTitle = findViewById(R.id.tvLeaderTitle);

        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new AllianceMembersAdapter(membersList);
        recyclerViewMembers.setAdapter(membersAdapter);

        String allianceId = getIntent().getStringExtra("allianceId");
        AllianceService allianceService = new AllianceService(this);

        Prefs prefs = new Prefs(this);
        currentUserId = prefs.getUid();

        if(allianceId != null) {
            loadAllianceById(allianceId, allianceService);
        } else {
            loadAllianceByUserId(currentUserId, allianceService);
        }

        btnDeleteAlliance.setOnClickListener(v -> {
            if(currentAlliance != null) {
                allianceService.deleteAlliance(currentUserId, new ICallbackVoid() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AllianceActivity.this, "Savez obrisan", Toast.LENGTH_SHORT).show();
                        showNoAlliance();
                        btnDeleteAlliance.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AllianceActivity.this, "Greska: " + errorMessage, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void loadAllianceById(String allianceId, AllianceService allianceService) {
        allianceService.getAlliance(allianceId, new ICallback<>() {
            @Override
            public void onSuccess(Alliance result) {
                loadLeaderAndShowAlliance(result);
                loadAllianceMembers(allianceId, allianceService);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showNoAlliance();
            }
        });
    }

    private void loadAllianceByUserId(String currentUserId, AllianceService allianceService) {
        allianceService.getAllianceByUserId(currentUserId, new ICallback<>() {
            @Override
            public void onSuccess(Alliance result) {
                if(result != null) {
                    loadLeaderAndShowAlliance(result);
                    loadAllianceMembers(result.getId(), allianceService);
                } else {
                    showNoAlliance();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showNoAlliance();
            }
        });
    }

    private void loadLeaderAndShowAlliance(Alliance alliance) {
        currentAlliance = alliance;
        UserService userService = new UserService(AllianceActivity.this);
        userService.findUserById(alliance.getLeaderId(), new ICallback<>() {
            @Override
            public void onSuccess(User leader) {
                membersList.clear();
                membersList.add(leader.getUsername());
                showAlliance(alliance.getName(), leader.getUsername());
                checkDeleteButtonVisibility();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showNoAlliance();
            }
        });
    }

    private void showAlliance(String allianceName, String leaderName) {
        tvAllianceName.setText(allianceName);
        tvLeaderName.setText(leaderName);
        tvMembersTitle.setVisibility(View.VISIBLE);
        recyclerViewMembers.setVisibility(View.VISIBLE);
    }

    private void showNoAlliance() {
        tvAllianceName.setText("Niste još u savezu");
        tvLeaderName.setText("");
        tvLeaderTitle.setVisibility(View.GONE);
        recyclerViewMembers.setVisibility(View.GONE);
        tvMembersTitle.setVisibility(View.GONE);
    }

    private void checkDeleteButtonVisibility() {
        if(currentAlliance == null) return;

        if(currentAlliance.getLeaderId().equals(currentUserId) && !currentAlliance.isMissionStarted()) {
            btnDeleteAlliance.setVisibility(View.VISIBLE);
        } else {
            btnDeleteAlliance.setVisibility(View.GONE);
        }
    }

    private void loadAllianceMembers(String allianceId, AllianceService allianceService) {
        allianceService.getAllianceMembers(allianceId, new ICallback<>() {
            @Override
            public void onSuccess(List<String> result) {
                membersList.addAll(result);
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
