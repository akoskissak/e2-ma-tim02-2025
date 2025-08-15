package com.example.habitmaster.domain.usecases;


import android.content.Context;

import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.services.ICallbackVoid;

public class ChangePasswordUseCase {
    private final UserRepository repo;
    private final Context context;

    public ChangePasswordUseCase(Context ctx){
        this.context = ctx.getApplicationContext();
        this.repo = new UserRepository(ctx);
    }

    public void execute(String oldPassword, String newPassword, ICallbackVoid callback){
        repo.changePassword(oldPassword, newPassword, task -> {
            if(task.isSuccessful()){
                callback.onSuccess();
            } else {
                Exception e = task.getException();
                String message = (e != null) ? e.getMessage() : "Unknown error";
                callback.onError(message);
            }
        });
    }
}
