package com.example.habitmaster.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.FriendService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.ui.activities.AllianceActivity;
import com.example.habitmaster.ui.adapters.FriendAdapter;
import com.example.habitmaster.utils.Prefs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateAllianceDialogFragment extends DialogFragment {
    private EditText etAllianceName;
    private TextView tvInviteFriends;
    private RecyclerView rvFriends;
    private Button btnCreate, btnCancel;
    private final List<Friend> followedFriends = new ArrayList<>();
    private Set<String> invitedFriendIds = new HashSet<>();

    private FriendService friendService;
    private AllianceService allianceService;
    private FriendAdapter friendAdapter;
    private String currentUserId;
    private String currentUsername;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendService = new FriendService(requireContext());
        allianceService = new AllianceService(requireActivity().getApplicationContext()); // ovo zbog notifikacije

        Prefs prefs = new Prefs(requireContext());
        currentUserId = prefs.getUid();
        currentUsername = prefs.getUsername();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void loadFollowedFriends() {
        friendService.getFriends(currentUserId, new ICallback<>() {
            @Override
            public void onSuccess(List<Friend> result) {
                followedFriends.clear();
                followedFriends.addAll(result);
                if(friendAdapter != null) {
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                tvInviteFriends.setVisibility(View.GONE);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_alliance, container, false);
        etAllianceName = view.findViewById(R.id.etAllianceName);
        tvInviteFriends = view.findViewById(R.id.tvInviteFriends);

        rvFriends = view.findViewById(R.id.rvFriends);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCancel = view.findViewById(R.id.btnCancel);

        friendAdapter = new FriendAdapter(followedFriends,true, new FriendAdapter.OnFriendClickListener() {
            @Override
            public void onFriendClicked(Friend friend) {

            }

            @Override
            public void onFriendSelected(Friend friend, boolean selected) {
                if (selected) {
                    invitedFriendIds.add(friend.getFriendUserId());
                } else {
                    invitedFriendIds.remove(friend.getFriendUserId());
                }
            }
        });

        rvFriends.setAdapter(friendAdapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        btnCancel.setOnClickListener(v -> dismiss());

        btnCreate.setOnClickListener(v -> createAlliance());

        loadFollowedFriends();

        return view;
    }

    private void createAlliance() {
        String name = etAllianceName.getText().toString().trim();
        if(name.isEmpty()) {
            etAllianceName.setError("Unesite naziv saveza");
            return;
        }
        allianceService.createAlliance(name, currentUserId, currentUsername, invitedFriendIds, new ICallback<>() {
            @Override
            public void onSuccess(String result) {
                dismiss();
                Intent intent = new Intent(getContext(), AllianceActivity.class);
                intent.putExtra("allianceId", result);
                startActivity(intent);
                Toast.makeText(getContext(), "Savez je kreiran", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
