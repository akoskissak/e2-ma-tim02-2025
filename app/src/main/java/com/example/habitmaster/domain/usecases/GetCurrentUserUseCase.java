package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.Prefs;

public class GetCurrentUserUseCase {
    private final UserLocalRepository userRepository;
    private final Context context;

    public GetCurrentUserUseCase(Context ctx) {
        this.userRepository = new UserLocalRepository(ctx);
        this.context = ctx.getApplicationContext();
    }

    public void execute(ICallback<User> callback) {
        Prefs prefs = new Prefs(context);
        String email = prefs.getEmail();
        if(email == null){
            callback.onError("No user logged in");
            return;
        }
        User user = userRepository.findByEmail(email);

        callback.onSuccess(user);
    }
}
