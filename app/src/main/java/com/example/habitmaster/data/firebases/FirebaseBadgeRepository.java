package com.example.habitmaster.data.firebases;

import androidx.annotation.NonNull;

import com.example.habitmaster.domain.models.Badge;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseBadgeRepository {
    private final FirebaseFirestore firestore;

    public FirebaseBadgeRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void insert(@NonNull Badge badge) {
        firestore.collection("badges")
                .document(badge.getId())
                .set(badge)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Badge dodat u Firestore: " + badge.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Greška pri dodavanju badge-a: " + e.getMessage());
                });
    }
}
