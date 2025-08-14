package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.data.repositories.UserRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.utils.Prefs;
import com.google.firebase.auth.FirebaseUser;

public class LoginUserUseCase {
    private final UserRepository repo;
    private final UserLocalRepository localRepo;
    private final Context context;

    public LoginUserUseCase(Context ctx){
        this.context = ctx.getApplicationContext();
        this.repo = new UserRepository(ctx);
        this.localRepo = new UserLocalRepository(ctx);
    }

    public void execute(String email, String password, ICallback callback){
        if(email.isEmpty() || password.isEmpty()){
            callback.onError("Popunite sva polja");
            return;
        }

        repo.loginAuthUser(email, password, task -> {
            if(task.isSuccessful()){
                FirebaseUser user = task.getResult().getUser();
                if(user != null){
                    User localUser = localRepo.findByEmail(email);
                    if(localUser == null) {
                        callback.onError("Korisnik nije pronadjen u lokalnoj bazi");
                        return;
                    }

                    if(user.isEmailVerified()){
                        localRepo.updateActivateFlag(user.getUid());
                        repo.activateUserInFirebase(user.getUid());
                        Prefs prefs = new Prefs(context);
                        prefs.setUid(user.getUid());
                        callback.onSuccess(localUser);
                    } else {
                        long currentTime = System.currentTimeMillis();
                        long accountAgeMillis = currentTime - localUser.getCreatedAt();
                        // ovo su 2 minuta 2 * 60 * 1000
                        if(accountAgeMillis > 2 * 60 * 1000) {
                            callback.onError("Link je istekao, registrujte se ponovo");
                            localRepo.delete(localUser.getId());
                            user.delete();
                        } else {
                            callback.onError("Molim vas aktivirajte nalog preko email-a");
                        }
                    }
                } else {
                    callback.onError("Korisnik nije pronadjen u firebase bazi");
                }
            } else {
                callback.onError("Greska: " + (task.getException() != null ? task.getException().getMessage() : ""));
            }
        });
    }


}
