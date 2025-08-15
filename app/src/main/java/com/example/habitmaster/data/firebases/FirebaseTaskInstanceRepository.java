package com.example.habitmaster.data.firebases;

import androidx.annotation.NonNull;

import com.example.habitmaster.domain.models.TaskInstance;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseTaskInstanceRepository {
    private final FirebaseFirestore firestore;

    public FirebaseTaskInstanceRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void insert(@NonNull TaskInstance taskInstance) {
        firestore.collection("taskInstances")
                .document(taskInstance.getId())
                .set(taskInstance);
    }
}
