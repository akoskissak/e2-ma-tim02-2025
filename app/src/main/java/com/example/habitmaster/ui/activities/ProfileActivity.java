package com.example.habitmaster.ui.activities;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.FriendService;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ICallbackVoid;
import com.example.habitmaster.services.UserEquipmentService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.InventoryAdapter;
import com.example.habitmaster.utils.AvatarUtils;
import com.example.habitmaster.utils.Prefs;
import com.example.habitmaster.utils.QRCodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ImageView avatarImage, qrImageView;
    private TextView usernameText, levelText, titleText, xpText, ppText, badgesCountText, badgesText, coinsText, noEquipmentText;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button changePasswordButton, followButton;
    private UserService userService;
    private UserEquipmentService userEquipmentService;
    private RecyclerView recyclerViewInventory;
    private Prefs prefs;
    private String viewedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userService = new UserService(this);
        userEquipmentService = new UserEquipmentService(this);

        prefs =  new Prefs(this);
        String loggedInUserId = prefs.getUid();
        if(loggedInUserId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        String loggedInUsername = prefs.getUsername();

        avatarImage = findViewById(R.id.avatarImage);
        qrImageView = findViewById(R.id.qrImageView);
        usernameText = findViewById(R.id.usernameText);
        levelText = findViewById(R.id.levelText);
        titleText = findViewById(R.id.titleText);
        xpText = findViewById(R.id.xpText);
        ppText = findViewById(R.id.ppText);
        badgesCountText = findViewById(R.id.badgesCountText);
        badgesText = findViewById(R.id.badgesText);
        coinsText = findViewById(R.id.coinsText);
        recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        noEquipmentText = findViewById(R.id.noEquipmentText);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        etOldPassword = findViewById(R.id.oldPasswordInput);
        etNewPassword = findViewById(R.id.newPasswordInput);
        etConfirmPassword = findViewById(R.id.confirmPasswordInput);
        followButton = findViewById(R.id.followButton);

        LinearLayout changePasswordContainer = findViewById(R.id.changePasswordContainer);
        LinearLayout ppContainer = findViewById(R.id.powerPointsContainer);
        LinearLayout xpContainer= findViewById(R.id.xpContainer);
        LinearLayout coinsContainer = findViewById(R.id.coinsContainer);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewInventory.setLayoutManager(layoutManager);

        viewedUsername = getIntent().getStringExtra("username");
        if(viewedUsername == null) {
            viewedUsername = loggedInUsername;
        }

        if(!viewedUsername.equals(loggedInUsername)) {
            changePasswordContainer.setVisibility(View.GONE);
            ppContainer.setVisibility(View.GONE);
            xpContainer.setVisibility(View.GONE);
            coinsContainer.setVisibility(View.GONE);
        } else {
            changePasswordContainer.setVisibility(View.VISIBLE);
            ppContainer.setVisibility(View.VISIBLE);
            xpContainer.setVisibility(View.VISIBLE);
            coinsContainer.setVisibility(View.VISIBLE);
        }

        FriendService friendService = new FriendService(this);

        loadUserProfile(viewedUsername, loggedInUserId, loggedInUsername, friendService);

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void loadUserProfile(String username, String loggedInUserId, String loggedInUsername, FriendService friendService) {
        userService.findUserByUsername(username, new ICallback<>() {
            @Override
            public void onSuccess(User user) {
                usernameText.setText(user.getUsername());
                levelText.setText(String.valueOf(user.getLevel()));
                titleText.setText(user.getTitle());
                xpText.setText(String.valueOf(user.getXp()));
                ppText.setText(String.valueOf(user.getPowerPoints()));
                badgesCountText.setText(String.valueOf(user.getBadgesCount()));
                badgesText.setText(user.getBadges());
                coinsText.setText(String.valueOf(user.getCoins()));

                userEquipmentService.getAllUserEquipment(user.getId(), new ICallback<>() {
                    @Override
                    public void onSuccess(List<UserEquipment> result) {

                        if (result == null || result.isEmpty()) {
                            recyclerViewInventory.setVisibility(View.GONE);
                            noEquipmentText.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewInventory.setVisibility(View.VISIBLE);
                            noEquipmentText.setVisibility(View.GONE);

                            InventoryAdapter adapter = new InventoryAdapter(result, userEquipmentService, user);
                            recyclerViewInventory.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                int avatarResId = AvatarUtils.getAvatarResId(user.getAvatarName());
                avatarImage.setImageResource(avatarResId);

                // QR KOD
                try {
                    JSONObject qrObject = new JSONObject();
                    qrObject.put("userId", user.getId());
                    qrObject.put("username", user.getUsername());
                    qrObject.put("avatarName", user.getAvatarName());

                    String qrData = qrObject.toString();
                    Bitmap qrBitmap = QRCodeUtils.generateQRCode(qrData, 500);
                    qrImageView.setImageBitmap(qrBitmap);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Nije moguce generisati QR kod", Toast.LENGTH_SHORT).show();

                }

                if(user.getUsername().equals(loggedInUsername)) {
                    followButton.setVisibility(View.GONE);
                    return;
                }

                followButton.setVisibility(View.VISIBLE);

                friendService.isAlreadyFriend(loggedInUserId, user.getId(), new ICallback<>() {
                    @Override
                    public void onSuccess(Boolean isFriend) {
                        friendService.isFollowRequestPending(loggedInUserId, user.getId(), new ICallback<>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (isFriend) {
                                    followButton.setText("Unfollow");
                                } else if (result) {
                                    followButton.setText("Requested");
                                    followButton.setEnabled(false);
                                } else {
                                    followButton.setText("Request to follow");
                                }
                                followButton.setOnClickListener(v -> {
                                    if (!isFriend && !result) {
                                        friendService.sendFollowRequest(loggedInUserId, user.getId());

                                        Toast.makeText(ProfileActivity.this, "Friend request sent to " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                        followButton.setText("Requested");
                                        followButton.setEnabled(false);
                                    } else if (isFriend){
                                        friendService.removeFriend(user.getId(), loggedInUserId, new ICallback<>() {
                                            @Override
                                            public void onSuccess(Void result) {
                                                Toast.makeText(ProfileActivity.this, "Unfollowed " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                                followButton.setText("Request to follow");
                                                followButton.setEnabled(true);
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "Popuni sva polja", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPassword.equals(confirmPassword)){
            Toast.makeText(this, "Nove lozinke se ne poklapaju", Toast.LENGTH_SHORT).show();
            return;
        }

        if(prefs.getUid() == null || prefs.getUsername() == null ){
            Toast.makeText(this, "Greska sa nalogom", Toast.LENGTH_SHORT).show();
            return;
        }

        // reautentifikacija korisnika
        userService.changePassword(
                oldPassword,
                newPassword,
                new ICallbackVoid() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ProfileActivity.this, "Lozinka promenjena", Toast.LENGTH_SHORT).show();
                        etOldPassword.setText("");
                        etNewPassword.setText("");
                        etConfirmPassword.setText("");
                    }
                    @Override
                    public void onError (String errorMessage){
                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
