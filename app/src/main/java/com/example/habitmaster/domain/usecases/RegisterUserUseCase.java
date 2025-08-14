package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.utils.Prefs;
import com.example.habitmaster.utils.AuthValidation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegisterUserUseCase {
    private final UserRepository repo;
    private final Context context;

    public interface Callback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public RegisterUserUseCase(Context ctx){
        this.context = ctx.getApplicationContext();
        this.repo = new UserRepository(ctx);
    }

    public void execute(String email, String password, String confirm, String username, String avatarName, Callback callback) {
        if (!AuthValidation.isValidEmail(email)) {
            callback.onError("Neispravan email");
            return;
        }
        if (!AuthValidation.passwordsMatch(password, confirm)) {
            callback.onError("Lozinke se ne poklapaju");
            return;
        }
        if (!AuthValidation.isStrongPassword(password)) {
            callback.onError("Lozinka mora imati min. 8 karaktera");
            return;
        }
        if (!AuthValidation.isValidUsername(username)) {
            callback.onError("Korisnicko ime (3-20, bez razmaka)");
            return;
        }
        if (!AuthValidation.notEmpty(avatarName)) {
            callback.onError("Izaberite avatar");
            return;
        }
        repo.createAuthUser(email, password, task -> onAuthCreated(task, email, username, avatarName, callback));
    }

    private void onAuthCreated(Task<AuthResult> task, String email, String username, String avatarName, Callback callback) {
        if (!task.isSuccessful()) {
            callback.onError("Greska: " + (task.getException() != null ? task.getException().getMessage() : ""));
            return;
        }

        repo.sendVerification();

        String uid = repo.currentUid();
        long now = System.currentTimeMillis();
        User u = new User(uid, email, username, avatarName, false, now);
        repo.saveUser(u);

        Prefs prefs = new Prefs(context);
        prefs.setUid(uid);
        prefs.lockUsername();
        prefs.lockAvatar();

        callback.onSuccess(u);
    }
}
