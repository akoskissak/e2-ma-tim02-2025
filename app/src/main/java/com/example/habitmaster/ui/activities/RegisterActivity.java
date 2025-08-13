package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.usecases.RegisterUserUseCase;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.fragments.AvatarPickerFragment;

public class RegisterActivity extends AppCompatActivity implements AvatarPickerFragment.OnAvatarSelectedListener {
    private EditText etEmail, etPassword, etConfirmPassword, etUsername;
    private Button btnRegister;
    private ProgressBar progressBar;
    private String selectedAvatarName;

    int[] avatars = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5
    };

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase(); // Force open

        userService = new UserService(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.avatarFragmentContainer, new AvatarPickerFragment())
                .commit();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);


        btnRegister.setOnClickListener(v -> register());
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
                new RegisterUserUseCase.Callback() {
                    @Override
                    public void onSuccess(User user) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registracija uspesna! Proverite email za aktivaciju.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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
