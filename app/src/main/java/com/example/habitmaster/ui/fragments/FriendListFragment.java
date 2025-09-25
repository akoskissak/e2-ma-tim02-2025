package com.example.habitmaster.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.AllianceService;
import com.example.habitmaster.services.FriendService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.activities.AllianceActivity;
import com.example.habitmaster.ui.activities.ProfileActivity;
import com.example.habitmaster.ui.adapters.FriendAdapter;
import com.example.habitmaster.utils.Prefs;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FriendListFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private RecyclerView recyclerViewFriends;
    private EditText editSearch;
    private ImageButton btnReset;
    private ImageButton btnSearch;
    private Button scanQrButton;
    private Button createAllianceButton;
    private FriendAdapter friendAdapter;
    private TextView tvFriendsHeader;
    private List<Friend> followedFriends = new ArrayList<>();

    private FriendService friendService;
    private UserService userService;
    private String currentUserId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        editSearch = view.findViewById(R.id.editTextSearch);
        btnReset = view.findViewById(R.id.btnReset);
        btnSearch = view.findViewById(R.id.btnSearchIcon);
        tvFriendsHeader = view.findViewById(R.id.tvFriendsHeader);
        scanQrButton = view.findViewById(R.id.scanQrButton);
        createAllianceButton = view.findViewById(R.id.createAllianceButton);

        friendService = new FriendService(requireContext());
        userService = new UserService(requireContext());

        Prefs prefs = new Prefs(requireContext());
        currentUserId = prefs.getUid();


        friendAdapter = new FriendAdapter(followedFriends, false, new FriendAdapter.OnFriendClickListener() {
            @Override
            public void onFriendClicked(Friend friend) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("username", friend.getFriendUsername());
                startActivity(intent);
            }

            @Override
            public void onFriendSelected(Friend friend, boolean selected) {

            }
        });
        
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFriends.setAdapter(friendAdapter);

        tvFriendsHeader.setVisibility(View.GONE);

        loadFollowedFriends();
        setupSearch();

        setupReset();

        AllianceService allianceService = new AllianceService(getContext());
        setupCreateAllianceButton(allianceService);

        return view;
    }

    private void loadFollowedFriends() {
        // Dohvati sve korisnike koje trenutni korisnik prati
        friendService.getFriends(currentUserId, new ICallback<>() {
            @Override
            public void onSuccess(List<Friend> result) {
                followedFriends.clear();
                followedFriends.addAll(result);
                friendAdapter.updateList(followedFriends);


                if (!result.isEmpty()) {
                    tvFriendsHeader.setVisibility(View.VISIBLE);
                } else {
                    tvFriendsHeader.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(String errorMessage) {
                followedFriends.clear();
                friendAdapter.updateList(followedFriends);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupSearch() {
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = editSearch.getText().toString().trim();
            performSearch(query);
            return true;
        });

        btnSearch.setOnClickListener(v -> {
            String query = editSearch.getText().toString().trim();
            performSearch(query);
        });

        scanQrButton.setOnClickListener(v -> startQrScan());

    }

    private void startQrScan() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan a user's QR code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            qrLauncher.launch(options);
        }
    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();

                    try {
                        Friend scannedFriend = getFriend(scannedData);

                        addFriendByQr(scannedFriend);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @NonNull
    private static Friend getFriend(String scannedData) throws JSONException {
        JSONObject qrObject = new JSONObject(scannedData);
        String scannedUserId = qrObject.getString("userId");
        String scannedUsername = qrObject.getString("username");
        String scannedAvatarName = qrObject.getString("avatarName");

        Friend scannedFriend = new Friend();
        scannedFriend.setFriendUserId(scannedUserId);
        scannedFriend.setFriendUsername(scannedUsername);
        scannedFriend.setFriendAvatarName(scannedAvatarName);
        return scannedFriend;
    }

    private void addFriendByQr(Friend scannedFriend) {
        if(scannedFriend.getFriendUserId().equals(currentUserId)) {
            Toast.makeText(getContext(), "You cannot add yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        friendService.addFriend(scannedFriend, currentUserId, new ICallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), scannedFriend.getFriendUsername() + " added as friend", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            tvFriendsHeader.setVisibility(View.GONE);
            userService.findUserByUsername(query, new ICallback<>() {
                @Override
                public void onSuccess(User result) {
                    Friend tempFriend = new Friend(null, result.getId(), result.getUsername(), result.getAvatarName());
                    friendAdapter.updateList(Collections.singletonList(tempFriend));
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    friendAdapter.updateList(Collections.emptyList());
                }
            });
        }
    }

    private void setupCreateAllianceButton(AllianceService allianceService) {
        allianceService.getAllianceByUserId(currentUserId, new ICallback<>() {
            @Override
            public void onSuccess(Alliance alliance) {
                if(alliance != null) {
                    createAllianceButton.setText("Show alliance");
                    createAllianceButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), AllianceActivity.class);
                        intent.putExtra("allianceId", alliance.getId());
                        startActivity(intent);
                    });
                } else {
                    createAllianceButton.setText("Create alliance");
                    createAllianceButton.setOnClickListener(v -> {
                        CreateAllianceDialogFragment dialog = new CreateAllianceDialogFragment();
                        dialog.show(getParentFragmentManager(), "create_alliance");
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                createAllianceButton.setText("Create alliance");
                createAllianceButton.setOnClickListener(v -> {
                    CreateAllianceDialogFragment dialog = new CreateAllianceDialogFragment();
                    dialog.show(getParentFragmentManager(), "create_alliance");
                });
            }
        });
    }

    private void setupReset() {
        btnReset.setOnClickListener(v -> {
            editSearch.setText("");
            loadFollowedFriends();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFollowedFriends();
        setupCreateAllianceButton(new AllianceService(getContext()));
    }
}
