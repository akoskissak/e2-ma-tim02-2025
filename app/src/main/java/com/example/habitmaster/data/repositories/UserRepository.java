package com.example.habitmaster.data.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.domain.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

public class UserRepository {
    private final FirebaseUserRepository firebaseRepo;
    public UserRepository(Context ctx){
        this.firebaseRepo = new FirebaseUserRepository(ctx);
    }

    public void createAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseRepo.createAuthUser(email, password, listener);
    }

    public void loginAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseRepo.loginAuthUser(email, password, listener);
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
}
