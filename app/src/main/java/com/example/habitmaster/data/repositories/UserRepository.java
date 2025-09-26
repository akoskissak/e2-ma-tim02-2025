package com.example.habitmaster.data.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import java.util.List;

public class UserRepository {
    private final FirebaseUserRepository firebaseRepo;
    private final UserLocalRepository localRepo;
    public UserRepository(Context ctx){

        this.firebaseRepo = new FirebaseUserRepository(ctx);
        this.localRepo = new UserLocalRepository(ctx);
    }

    public void createAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseRepo.createAuthUser(email, password, listener);
    }

    public void loginAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseRepo.loginAuthUser(email, password, listener);
    }

    public void getUserFromFirestore(String userId, ICallback<User> callback) {
        firebaseRepo.getUserById(userId, callback);
    }

    public void sendVerification(){
        firebaseRepo.sendVerification();
    }

    public String currentUid(){
        return firebaseRepo.currentUid();
    }

    public void saveUser(User user){
        firebaseRepo.saveUser(user);
    }

    public void activateUserInFirebase(String uid) {
        firebaseRepo.updateActivatedFlag(uid, true, task -> {
            if (!task.isSuccessful()) {
                Log.e("UserRepository", "Greska pri update-u activated flag-a", task.getException());
            }
        });
    }

    public void changePassword(String oldPassword, String newPassword, OnCompleteListener<Void> listener) {
        firebaseRepo.changePassword(oldPassword, newPassword, listener);
    }

    public void updateUserCoins(String userId, int coins){
        firebaseRepo.updateUserCoins(userId, coins, task -> {
            if(!task.isSuccessful()) {
                Log.e("UserRepository", "Greska pri update-u coins-a", task.getException());
            }
        });
    }

    public void checkUserExists(String email, String username, ICallback<Boolean> callback) {
        if (localRepo.exists(email, username)) {
            callback.onSuccess(true);
            return;
        }

        firebaseRepo.isUsernameTaken(username, usernameExists -> {
            if (usernameExists) {
                callback.onSuccess(true);
            } else {
                firebaseRepo.isEmailTaken(email, emailExists -> {
                    callback.onSuccess(emailExists);
                }, error -> callback.onError(error));
            }
        }, error -> callback.onError(error));
    }

    public void setLastLogout(String userId) {
        localRepo.setLastLogout(userId);
        firebaseRepo.setLastLogout(userId);
    }
}
