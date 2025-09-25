package com.example.habitmaster.data.firebases;

import android.content.Context;

import com.example.habitmaster.domain.models.UserLevelProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public Task<UserLevelProgress> getUserLevelProgress(String userId) {
        return db.collection("userLevelProgress")
                .document(userId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        return documentSnapshot.toObject(UserLevelProgress.class);
                    } else {
                        return null;
                    }
                });
    }

}
