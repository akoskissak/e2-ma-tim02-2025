package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallbackVoid;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.utils.AvatarUtils;
import com.example.habitmaster.utils.Prefs;
import com.example.habitmaster.utils.QRCodeUtils;

public class ProfileActivity extends AppCompatActivity {
    private ImageView avatarImage, qrImageView;
    private TextView usernameText, levelText, titleText, xpText, badgesCountText, badgesText, coinsText, equipmentText;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button changePasswordButton;
    private UserService userService;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userService = new UserService(this);
        prefs =  new Prefs(this);

        avatarImage = findViewById(R.id.avatarImage);
        qrImageView = findViewById(R.id.qrImageView);
        usernameText = findViewById(R.id.usernameText);
        levelText = findViewById(R.id.levelText);
        titleText = findViewById(R.id.titleText);
        xpText = findViewById(R.id.xpText);
        badgesCountText = findViewById(R.id.badgesCountText);
        badgesText = findViewById(R.id.badgesText);
        coinsText = findViewById(R.id.coinsText);
        equipmentText = findViewById(R.id.equipmentText);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        etOldPassword = findViewById(R.id.oldPasswordInput);
        etNewPassword = findViewById(R.id.newPasswordInput);
        etConfirmPassword = findViewById(R.id.confirmPasswordInput);

        loadUserProfile();
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void loadUserProfile() {
        String uid = prefs.getUid();
        if(uid == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        User user = userService.getUser(prefs.getEmail());

        usernameText.setText(user.getUsername());
        levelText.setText(String.valueOf(user.getLevel()));
        titleText.setText(user.getTitle());
        xpText.setText(String.valueOf(user.getXp()));
        badgesCountText.setText(String.valueOf(user.getBadgesCount()));
        badgesText.setText(user.getBadges());
        coinsText.setText(String.valueOf(user.getCoins()));
        equipmentText.setText(user.getEquipment());

        int avatarResId = AvatarUtils.getAvatarResId(user.getAvatarName());
        avatarImage.setImageResource(avatarResId);

        String qrData = user.getId() + ";" + user.getEmail();
        Bitmap qrBitmap= QRCodeUtils.generateQRCode(qrData, 500);
        qrImageView.setImageBitmap(qrBitmap);

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

        if(prefs.getUid() == null || prefs.getEmail() == null ){
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
