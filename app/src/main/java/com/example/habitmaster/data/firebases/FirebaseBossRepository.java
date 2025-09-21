package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.services.ICallback;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseBossRepository {
    private final FirebaseFirestore firestore;

    public FirebaseBossRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void insert(Boss boss) {
        firestore.collection("bosses")
                .document(String.valueOf(boss.getId()))
                .set(boss)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void findByUserId(String userId, ICallback<Boss> callback) {
        firestore.collection("bosses")
                .document(String.valueOf(userId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        Boss boss = documentSnapshot.toObject(Boss.class);
                        callback.onSuccess(boss);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void update(Boss boss) {
        firestore.collection("bosses")
                .document(String.valueOf(boss.getId()))
                .set(boss)
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
