package com.example.habitmaster.data.firebases;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserLevelProgress;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseUserRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserLocalRepository local;
    private final FirebaseUserLevelProgressRepository firebaseUserLevelProgressRepository;

    public FirebaseUserRepository(Context ctx) {

        this.local = new UserLocalRepository(ctx);
        this.firebaseUserLevelProgressRepository = new FirebaseUserLevelProgressRepository();
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
                        String id = documentSnapshot.getId();
                        String email = documentSnapshot.getString("email");
                        String username = documentSnapshot.getString("username");
                        String avatarName = documentSnapshot.getString("avatarName");
                        Boolean activatedBool = documentSnapshot.getBoolean("activated");
                        long createdAt = documentSnapshot.getLong("createdAt") != null ? documentSnapshot.getLong("createdAt") : 0;
                        long lastLogout = documentSnapshot.getLong("lastLogout") != null ? documentSnapshot.getLong("lastLogout") : 0;

                        User user = new User(id, email, username, avatarName, activatedBool != null && activatedBool, createdAt, lastLogout);

                        Long level = documentSnapshot.getLong("level");
                        user.setLevel(level != null ? level.intValue() : 0);

                        Object levelObj = documentSnapshot.get("levelStartDate");
                        LocalDate levelStartDate = LocalDate.now();

                        if (levelObj instanceof Map) {
                            Map levelMap = (Map) levelObj;
                            try {
                                int year = ((Long) levelMap.get("year")).intValue();
                                int month = ((Long) levelMap.get("monthValue")).intValue();
                                int day = ((Long) levelMap.get("dayOfMonth")).intValue();
                                levelStartDate = LocalDate.of(year, month, day);
                            } catch (Exception e) {
                                levelStartDate = LocalDate.now();
                            }
                        }
                        user.setLevelStartDate(levelStartDate);

                        user.setTitle(documentSnapshot.getString("title") != null ? documentSnapshot.getString("title") : "Rookie");
                        user.setPowerPoints(documentSnapshot.getLong("powerPoints") != null ? documentSnapshot.getLong("powerPoints").intValue() : 0);
                        user.setXp(documentSnapshot.getLong("xp") != null ? documentSnapshot.getLong("xp").intValue() : 0);
                        user.setCoins(documentSnapshot.getLong("coins") != null ? documentSnapshot.getLong("coins").intValue() : 0);
                        user.setBadgesCount(documentSnapshot.getLong("badgesCount") != null ? documentSnapshot.getLong("badgesCount").intValue() : 0);
                        user.setBadges(documentSnapshot.getString("badges") != null ? documentSnapshot.getString("badges") : "");

                        callback.onSuccess(user);
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
        doc.put("levelStartDate", u.getLevelStartDate());
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

    public void findUserByUsername(String username, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        User u = new User();
                        u.setId(doc.getId());
                        u.setEmail(doc.getString("email"));
                        u.setUsername(doc.getString("username"));
                        u.setAvatarName(doc.getString("avatarName"));
                        u.setActivated(Boolean.TRUE.equals(doc.getBoolean("activated")));
                        Long createdAt = doc.getLong("createdAt");
                        u.setCreatedAt(createdAt != null ? createdAt : System.currentTimeMillis());
                        u.setLevel(doc.getLong("level") != null ? doc.getLong("level").intValue() : 1);

                        Object levelStartObj = doc.get("levelStartDate");
                        if (levelStartObj instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) levelStartObj;
                            Long year = map.get("year") instanceof Number ? ((Number) map.get("year")).longValue() : null;
                            Long monthValue = map.get("monthValue") instanceof Number ? ((Number) map.get("monthValue")).longValue() : null;
                            Long dayOfMonth = map.get("dayOfMonth") instanceof Number ? ((Number) map.get("dayOfMonth")).longValue() : null;

                            if (year != null && monthValue != null && dayOfMonth != null) {
                                u.setLevelStartDate(LocalDate.of(year.intValue(), monthValue.intValue(), dayOfMonth.intValue()));
                            } else {
                                u.setLevelStartDate(LocalDate.now());
                            }
                        } else {
                            u.setLevelStartDate(LocalDate.now());
                        }

                        u.setTitle(doc.getString("title"));
                        u.setPowerPoints(doc.getLong("powerPoints") != null ? doc.getLong("powerPoints").intValue() : 0);
                        u.setXp(doc.getLong("xp") != null ? doc.getLong("xp").intValue() : 0);
                        u.setCoins(doc.getLong("coins") != null ? doc.getLong("coins").intValue() : 0);
                        u.setBadgesCount(doc.getLong("badgesCount") != null ? doc.getLong("badgesCount").intValue() : 0);
                        u.setBadges(doc.getString("badges"));

                        onSuccess.onSuccess(u);
                    } else {
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void update(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) return;

        Map<String, Object> userMap = userToMap(user);

        db.collection("users")
                .document(user.getId())
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User " + user.getId() + " updated successfully.");
                    local.insert(user);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Failed to update user " + user.getId() + ": " + e.getMessage());
                });
    }

    public void addXp(String userId, int xp) {
        if (userId == null || userId.isEmpty()) {
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        return;
                    }

                    db.runTransaction(transaction -> {
                        DocumentReference userRef = db.collection("users").document(userId);
                        DocumentSnapshot snapshot = transaction.get(userRef);

                        if (!snapshot.exists()) {
                            Log.w("LevelUp", "User document ne postoji za userId=" + userId);
                            return null;
                        }

                        Long currentXp = documentSnapshot.getLong("xp");
                        if (currentXp == null) currentXp = 0L;

                        long newXp = currentXp + xp;

                        checkAndLevelUpFirebase(transaction, userId, newXp);

                        return null;
                    });
                });
    }

    private void checkAndLevelUpFirebase(Transaction transaction, String userId, long currentXp) {
        try {
            DocumentReference userRef = db.collection("users").document(userId);
            DocumentReference progressRef = db.collection("userLevelProgress").document(userId);

            DocumentSnapshot snapshot = transaction.get(userRef);
            DocumentSnapshot progressSnap = transaction.get(progressRef);



            Long level = snapshot.getLong("level");
            String title = snapshot.getString("title");
            Long powerPoints = snapshot.getLong("powerPoints");

            if (level == null) level = 0L;
            if (powerPoints == null) powerPoints = 0L;
            if (title == null) title = "Rookie";

            Log.d("LevelUp", "Pre level-up: level=" + level + ", xp=" + currentXp + ", pp=" + powerPoints);

            UserLevelProgress progress = null;
            if (progressSnap.exists()) {
                progress = progressSnap.toObject(UserLevelProgress.class);
            }

            if (progress == null) {
                Log.w("LevelUp", "Nema progress dokumenta za userId=" + userId);
                return;
            }

            boolean leveledUp = false;

            while (currentXp >= progress.getRequiredXp()) {
                level++;
                currentXp -= progress.getRequiredXp();

                int prevRequired = progress.getRequiredXp();
                progress.updateRequiredXp(prevRequired);

                if (level == 1) {
                    powerPoints = 40L;
                } else {
                    powerPoints = Math.round(powerPoints + (3.0 / 4.0) * powerPoints);
                }

                progress.updateXpValuesOnLevelUp();

                switch (level.intValue()) {
                    case 0:
                        title = "Rookie";
                        break;
                    case 1:
                        title = "Adventurer";
                        break;
                    case 2:
                        title = "Hero";
                        break;
                    default:
                        title = "Hero lvl" + level;
                        break;
                }

                leveledUp = true;
            }

            if (leveledUp) {
                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("level", level);
                userUpdates.put("title", title);
                userUpdates.put("powerPoints", powerPoints);
                userUpdates.put("xp", currentXp);
                userUpdates.put("levelStartDate", LocalDate.now().toString());

                transaction.update(userRef, userUpdates);

                transaction.set(progressRef, progress);
                Log.d("LevelUp", "User i progress uspešno ažurirani u Firestore");
            } else {
                transaction.update(userRef, "xp", currentXp);
                Log.d("LevelUp", "Nema level up-a, ostaje level=" + level + ", xp=" + currentXp);
            }
        } catch (FirebaseFirestoreException e) {
            Log.e("LevelUp", "Greška tokom transakcije: " + e.getMessage(), e);
        }
    }

    public void setLastLogout(String userId) {
        long currentTime = System.currentTimeMillis();

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("lastLogout", currentTime);

        db.collection("users")
                .document(userId)
                .update(updateMap);
    }
}
