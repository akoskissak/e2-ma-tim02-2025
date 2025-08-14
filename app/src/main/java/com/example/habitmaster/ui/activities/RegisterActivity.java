package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.fragments.AvatarPickerFragment;

public class RegisterActivity extends AppCompatActivity implements AvatarPickerFragment.OnAvatarSelectedListener {
    private EditText etEmail, etPassword, etConfirmPassword, etUsername;
    private Button registerButton;
    private ProgressBar progressBar;
    private String selectedAvatarName;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userService = new UserService(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.avatarFragmentContainer, new AvatarPickerFragment())
                .commit();

        etEmail = findViewById(R.id.registerEmail);
        etPassword = findViewById(R.id.registerPassword);
        etConfirmPassword = findViewById(R.id.registerConfirmPassword);
        etUsername = findViewById(R.id.registerUsername);
        registerButton = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


        registerButton.setOnClickListener(v -> register());
    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String username = etUsername.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        userService.register(
                email,
                password,
                confirmPassword,
                username,
                selectedAvatarName,
                new ICallback() {
                    @Override
                    public void onSuccess(User user) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registracija uspesna! Proverite email za aktivaciju.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    @Override
    public void onAvatarSelected(String avatarName) {
        selectedAvatarName = avatarName;
    }
}
