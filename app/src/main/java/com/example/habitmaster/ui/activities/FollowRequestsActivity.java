package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.services.FriendService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.Prefs;
import com.example.habitmaster.ui.adapters.FollowRequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowRequestsActivity extends AppCompatActivity {

    private FollowRequestAdapter adapter;
    private final List<FollowRequestWithUsername> requestList = new ArrayList<>();
    private FriendService friendService;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendService = new FriendService(this);

        adapter = new FollowRequestAdapter(requestList, new FollowRequestAdapter.OnRequestClickListener() {
            @Override
            public void onAccept(FollowRequest request) {
                acceptRequest(request);
            }

            @Override
            public void onDecline(FollowRequest request) {
                declineRequest(request);
            }
        });
        recyclerView.setAdapter(adapter);

        Prefs prefs = new Prefs(this);
        currentUserId = prefs.getUid();

        loadRequests();
    }

    private void loadRequests() {
        requestList.clear();
        friendService.getFollowRequests(currentUserId, new ICallback<>() {
            @Override
            public void onSuccess(List<FollowRequestWithUsername> result) {
                requestList.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void acceptRequest(FollowRequest request) {
        friendService.respondFollowRequest(request, true, currentUserId, new ICallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(FollowRequestsActivity.this, "Prijatelj prihvacen", Toast.LENGTH_SHORT).show();
                loadRequests();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(FollowRequestsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void declineRequest(FollowRequest request) {
        friendService.respondFollowRequest(request, false, currentUserId, new ICallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(FollowRequestsActivity.this, "Zahtev odbijen", Toast.LENGTH_SHORT).show();
                loadRequests();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(FollowRequestsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
