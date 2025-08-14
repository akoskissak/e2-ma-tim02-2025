package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.ui.activities.LoginActivity;
import com.example.habitmaster.utils.Prefs;
import com.google.firebase.auth.FirebaseUser;

public class LoginUserUseCase {
    private final UserRepository repo;
    private final UserLocalRepository localRepo;
    private final Context context;

    public interface Callback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }
    public LoginUserUseCase(Context ctx){
        this.context = ctx.getApplicationContext();
        this.repo = new UserRepository(ctx);
        this.localRepo = new UserLocalRepository(ctx);
    }

    public void execute(String email, String password, Callback callback){
        if(email.isEmpty() || password.isEmpty()){
            callback.onError("Popunite sva polja");
            return;
        }

        repo.loginAuthUser(email, password, task -> {
            if(task.isSuccessful()){
                FirebaseUser user = task.getResult().getUser();
                if(user != null && user.isEmailVerified()){
                    User localUser = localRepo.findByEmail(email);
                    if(localUser == null) {
                        callback.onError("Korisnik nije pronadjen u lokalnoj bazi");
                        return;
                    }

                    long currentTime = System.currentTimeMillis();
                    long accountAgeMillis = currentTime - localUser.getCreatedAt();
                    // ovo su 2 minuta 2 * 60 * 1000
                    if(accountAgeMillis > 24 * 60 * 60 * 1000) {
                        callback.onError("Link je istekao, registrujte se ponovo");

                        localRepo.delete(localUser.getId());

                        if(user != null){
                            user.delete();
                        }
                        return;
                    }

                    localRepo.updateActivateFlag(user.getUid());
                    Prefs prefs = new Prefs(context);
                    prefs.setUid(user.getUid());

                    callback.onSuccess(localUser);
                } else {
                    callback.onError("Molim vas aktivirajte nalog preko email-a");
                }
            } else {
                callback.onError("Greska: " + (task.getException() != null ? task.getException().getMessage() : ""));
            }
        });
    }


}
