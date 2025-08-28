package com.example.habitmaster.data.firebases;

import android.content.Context;

import com.example.habitmaster.domain.models.UserLevelProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUserLevelProgressRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseUserLevelProgressRepository() { }

    public void saveUserLevelProgress(UserLevelProgress progress, OnCompleteListener<Void> listener) {
        db.collection("userLevelProgress")
                .document(progress.getUserId())
                .set(progress)
                .addOnCompleteListener(listener);
    }

    public void getUserLevelProgress(String userId, OnSuccessListener<UserLevelProgress> onSuccess, OnFailureListener onFailure) {
        db.collection("userLevelProgress")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserLevelProgress progress = documentSnapshot.toObject(UserLevelProgress.class);
                        onSuccess.onSuccess(progress);
                    } else {
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailure);
    }
}
