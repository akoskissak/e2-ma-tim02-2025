package com.example.habitmaster.data.repositories;

import android.content.Context;

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

    public void sendVerification(){
        firebaseRepo.sendVerification();
    }

    public String currentUid(){
        return firebaseRepo.currentUid();
    }

    public void saveUser(User user){
        firebaseRepo.saveUser(user);
    }
}
