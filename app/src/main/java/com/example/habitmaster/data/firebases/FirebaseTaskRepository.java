package com.example.habitmaster.data.firebases;

import androidx.annotation.NonNull;

import com.example.habitmaster.domain.models.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseTaskRepository {
    private final FirebaseFirestore firestore;

    public FirebaseTaskRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void insert(@NonNull Task task) {
        firestore.collection("tasks")
                .document(task.getId())
                .set(task)
                .addOnFailureListener(Throwable::printStackTrace);;
    }

    public void update(@NonNull Task task) {
        firestore.collection("tasks")
                .document(task.getId())
                .set(task)
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
