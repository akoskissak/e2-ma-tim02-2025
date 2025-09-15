package com.example.habitmaster.data.firebases;

import android.content.Context;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseUserRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserLocalRepository local;

    public FirebaseUserRepository(Context ctx) {
        this.local = new UserLocalRepository(ctx);
    }

    public void createAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void loginAuthUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void sendVerification() {
        if (auth.getCurrentUser() != null) auth.getCurrentUser().sendEmailVerification();
    }

    public String currentUid() { return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null; }

    public void saveUser(User u){
        if(u==null) return;
        db.collection("users").document(u.getId()).set(userToMap(u));
        local.insert(u);
    }

    public void getUserById(String userId, ICallback<User> callback) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setId(documentSnapshot.getId());
                            callback.onSuccess(user);
                        } else {
                            callback.onError("Greska u mapiranju korisnika");
                        }
                    } else {
                        callback.onError("Korisnik ne postoji u Firestore");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Greska: " + e.getMessage()));
    }

    private Map<String,Object> userToMap(User u){
        Map<String,Object> doc = new HashMap<>();
        doc.put("id",u.getId());
        doc.put("email",u.getEmail());
        doc.put("username",u.getUsername());
        doc.put("avatarName",u.getAvatarName());
        doc.put("activated",u.isActivated());
        doc.put("createdAt",u.getCreatedAt());
        doc.put("level",u.getLevel());
        doc.put("title",u.getTitle());
        doc.put("powerPoints",u.getPowerPoints());
        doc.put("xp",u.getXp());
        doc.put("coins",u.getCoins());
        doc.put("badgesCount",u.getBadgesCount());
        doc.put("badges",u.getBadges());
        return doc;
    }

    public void updateActivatedFlag(String uid, boolean activated, OnCompleteListener<Void> listener) {
        db.collection("users").document(uid)
                .update("activated", activated)
                .addOnCompleteListener(listener);
    }

    public void updateUserCoins(String userId, int coins, OnCompleteListener<Void> listener) {
        db.collection("users").document(userId)
                .update("coins", coins)
                .addOnCompleteListener(listener);
    }

    public void changePassword(String oldPassword, String newPassword, OnCompleteListener<Void> listener) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null || currentUser.getEmail() == null) {
            if(listener != null){
                listener.onComplete(Tasks.forException(new Exception("Korisnik nije ulogovan ili nedostaje email")));
            }
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);

        currentUser.reauthenticate(credential).addOnCompleteListener(authTask -> {
            if(authTask.isSuccessful()) {
                currentUser.updatePassword(newPassword).addOnCompleteListener(listener);
            } else {
                listener.onComplete(authTask);
            }
        });
    }

    public void isUsernameTaken(String username, Consumer<Boolean> onSuccess, Consumer<String> onError) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.accept(!task.getResult().isEmpty());
                    } else {
                        onError.accept(task.getException() != null ? task.getException().getMessage() : "Nepoznata greska");
                    }
                });
    }

    public void isEmailTaken(String email, Consumer<Boolean> onSuccess, Consumer<String> onError) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.accept(!task.getResult().isEmpty());
                    } else {
                        onError.accept(task.getException() != null ? task.getException().getMessage() : "Nepoznata greska");
                    }
                });
    }
}
