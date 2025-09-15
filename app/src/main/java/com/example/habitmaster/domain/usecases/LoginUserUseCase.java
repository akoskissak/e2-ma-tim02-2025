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

    public void execute(String email, String password, ICallback<User> callback){
        if(email.isEmpty() || password.isEmpty()){
            callback.onError("Popunite sva polja");
            return;
        }

        repo.loginAuthUser(email, password, task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser == null) {
                    callback.onError("Korisnik nije pronadjen u Firebase Auth");
                    return;
                }

                User localUser = localRepo.findByEmail(email);

                if (!firebaseUser.isEmailVerified()) {
                    if (localUser != null) {
                        long currentTime = System.currentTimeMillis();
                        long accountAgeMillis = currentTime - localUser.getCreatedAt();

                        // 2 minute = 2 * 60 * 1000
                        if (accountAgeMillis > 2 * 60 * 1000) {
                            callback.onError("Link je istekao, registrujte se ponovo");
                            localRepo.delete(localUser.getId());
                            firebaseUser.delete();
                        } else {
                            callback.onError("Molim vas aktivirajte nalog preko email-a");
                        }
                    } else {
                        callback.onError("Molim vas aktivirajte nalog preko email-a");
                    }
                    return;
                }

                if (localUser != null) {
                    activateAndSavePrefs(firebaseUser, localUser, callback);
                } else {
                    // Ako nema u SQLite, povuci iz Firestore
                    repo.getUserFromFirestore(firebaseUser.getUid(), new ICallback<>() {
                        @Override
                        public void onSuccess(User userFromFirestore) {
                            if (userFromFirestore == null) {
                                callback.onError("Korisnik nije pronadjen u Firestore bazi");
                                return;
                            }

                            localRepo.insert(userFromFirestore);

                            activateAndSavePrefs(firebaseUser, userFromFirestore, callback);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError("Greska prilikom citanja Firestore korisnika: " + errorMessage);
                        }
                    });
                }

            } else {
                callback.onError("Greska: " +
                        (task.getException() != null ? task.getException().getMessage() : ""));
            }
        });
    }

    private void activateAndSavePrefs(FirebaseUser firebaseUser, User user, ICallback<User> callback) {
        localRepo.updateActivateFlag(firebaseUser.getUid());
        repo.activateUserInFirebase(firebaseUser.getUid());

        Prefs prefs = new Prefs(context);
        prefs.setUid(firebaseUser.getUid());
        prefs.setUsername(user.getUsername());

        callback.onSuccess(user);
    }
}
