package com.example.habitmaster.ui.activities;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.AllianceChatService;
import com.example.habitmaster.services.AllianceMissionService;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ICallbackVoid;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.AllianceChatAdapter;
import com.example.habitmaster.ui.adapters.AllianceMembersAdapter;
import com.example.habitmaster.utils.NotificationHelper;
import com.example.habitmaster.utils.Prefs;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class AllianceActivity extends AppCompatActivity {
    private TextView tvAllianceName, tvLeaderName, tvMembersTitle, tvLeaderTitle, tvChatTitle;
    private RecyclerView recyclerViewMembers, recyclerChat;
    private EditText input;
    private AllianceMembersAdapter membersAdapter;
    private AllianceChatAdapter chatAdapter;
    private AllianceChatService chatService;
    private Button btnDeleteAlliance, btnSend, btnAllianceMission;
    private Alliance currentAlliance;
    private String currentUserId, currentUsername;
    private ListenerRegistration listenerRegistration;
    private List<String> membersList = new ArrayList<>();
    private AllianceMissionService allianceMissionService;
    private AllianceService allianceService;
    private boolean isLeader = false;

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
        tvChatTitle = findViewById(R.id.tvChatTitle);
        input = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        recyclerChat = findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new AllianceMembersAdapter(membersList);
        recyclerViewMembers.setAdapter(membersAdapter);

        btnAllianceMission = findViewById(R.id.btnAllianceMission);

        String allianceId = getIntent().getStringExtra("allianceId");
        allianceService = new AllianceService(this);
        chatService = new AllianceChatService(this);

        Prefs prefs = new Prefs(this);
        currentUserId = prefs.getUid();
        currentUsername = prefs.getUsername();

        chatAdapter = new AllianceChatAdapter(currentUserId);
        recyclerChat.setAdapter(chatAdapter);

        allianceMissionService = new AllianceMissionService(this);

        if (allianceId != null) {
            loadAllianceById(allianceId, allianceService);
        } else {
            loadAllianceByUserId(currentUserId, allianceService);
        }

        btnDeleteAlliance.setOnClickListener(v -> {
            if (currentAlliance != null) {
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

    private void loadOngoingAllianceMission(String allianceId) {
        allianceMissionService.getOngoingAllianceMissionByAllianceId(allianceId, new ICallback<AllianceMission>() {
            @Override
            public void onSuccess(AllianceMission result) {
                runOnUiThread(() -> {
                    btnAllianceMission.setText(R.string.mission_progress);
                    btnAllianceMission.setOnClickListener(v -> openMissionProgress(result));
                    btnAllianceMission.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    if (isLeader) {
                        btnAllianceMission.setText(R.string.start_mission);
                        btnAllianceMission.setOnClickListener(v -> startAllianceMission());
                        btnAllianceMission.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void openMissionProgress(AllianceMission allianceMission) {
        Intent intent = new Intent(getBaseContext(), AllianceMissionProgressActivity.class);
        intent.putExtra(AllianceMissionProgressActivity.ALLIANCE_MISSION_ID, allianceMission.getId());
        intent.putExtra(AllianceMissionProgressActivity.BOSS_MAX_HP, allianceMission.getBossMaxHp());
        intent.putExtra(AllianceMissionProgressActivity.BOSS_CURRENT_HP, allianceMission.getBossCurrentHp());
        startActivity(intent);
    }

    private void startAllianceMission() {
        allianceService.startAllianceMission(currentUserId, new ICallback<AllianceMission>() {
            @Override
            public void onSuccess(AllianceMission result) {
                runOnUiThread(() -> {
                    btnAllianceMission.setText(R.string.mission_progress);
                    btnAllianceMission.setOnClickListener(v -> openMissionProgress(result));
                    Toast.makeText(AllianceActivity.this, "Mission started", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(AllianceActivity.this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadAllianceById(String allianceId, AllianceService allianceService) {
        allianceService.getAlliance(allianceId, new ICallback<>() {
            @Override
            public void onSuccess(Alliance result) {
                loadLeaderAndShowAlliance(result);
                // za chat
                btnSend.setOnClickListener(v -> sendMessage(result.getId(), currentUsername));
                loadAllianceMembers(allianceId, allianceService);
                loadMessagesAndSubscribe(result.getId());
                isLeader = result.getLeaderId().equals(currentUserId);

                loadOngoingAllianceMission(allianceId);
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
                if (result != null) {
                    loadLeaderAndShowAlliance(result);
                    // za chat
                    btnSend.setOnClickListener(v -> sendMessage(result.getId(), currentUsername));
                    loadAllianceMembers(result.getId(), allianceService);
                    loadMessagesAndSubscribe(result.getId());
                    isLeader = result.getLeaderId().equals(currentUserId);

                    loadOngoingAllianceMission(result.getId());
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
        tvChatTitle.setVisibility(View.GONE);
        input.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);
        recyclerChat.setVisibility(View.GONE);
        tvAllianceName.setText("Niste još u savezu");
        tvLeaderName.setText("");
        tvLeaderTitle.setVisibility(View.GONE);
        recyclerViewMembers.setVisibility(View.GONE);
        tvMembersTitle.setVisibility(View.GONE);
    }

    private void checkDeleteButtonVisibility() {
        if (currentAlliance == null) return;

        if (currentAlliance.getLeaderId().equals(currentUserId) && !currentAlliance.isMissionStarted()) {
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

    private void sendMessage(String allianceId, String currentUsername) {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            input.setError("Unesite tekst");
            return;
        }

        chatService.sendMessage(allianceId, currentUserId, currentUsername, text, new ICallbackVoid() {
            @Override
            public void onSuccess() {

                input.setText("");
                NotificationHelper.notifyNewMessage(AllianceActivity.this, currentUsername, text);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, "Greska: " + errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadMessagesAndSubscribe(String allianceId) {
        chatService.loadAllMessages(allianceId, new ICallback<>() {
            @Override
            public void onSuccess(List<AllianceMessage> messages) {

                for (AllianceMessage msg : messages) {
                    Log.d("AllianceChatDebug", "Message: " + msg.getContent()
                            + ", User: " + msg.getSenderUsername()
                            + ", Timestamp: " + msg.getTimestamp());
                }


                chatAdapter.setMessages(messages);
                recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);

                subscribeToMessages(allianceId);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, "Greska pri ucitavanju poruka: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void subscribeToMessages(String allianceId) {
        listenerRegistration = chatService.subscribeToMessages(allianceId, new ICallback<>() {
            @Override
            public void onSuccess(AllianceMessage message) {
                chatAdapter.addMessage(message);
                recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AllianceActivity.this, "Greska: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
