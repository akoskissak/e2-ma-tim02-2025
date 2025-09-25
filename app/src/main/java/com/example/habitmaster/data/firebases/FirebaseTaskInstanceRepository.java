package com.example.habitmaster.data.firebases;

import androidx.annotation.NonNull;

import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseTaskInstanceRepository {
    private final FirebaseFirestore firestore;

    public FirebaseTaskInstanceRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void insert(@NonNull TaskInstance taskInstance) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", taskInstance.getId());
        data.put("taskId", taskInstance.getTaskId());
        data.put("date", taskInstance.getDate().toString()); // <--- String
        data.put("createdAt", taskInstance.getCreatedAt().toString()); // <--- String
        data.put("status", taskInstance.getStatus().name());

        firestore.collection("taskInstances")
                .document(taskInstance.getId())
                .set(data);
    }


    public void deleteFutureTaskInstances(String taskId) {
        firestore.collection("taskInstances")
                .whereEqualTo("taskId", taskId)
                .whereGreaterThanOrEqualTo("date", LocalDate.now().toString())
                .whereNotEqualTo("status", TaskStatus.COMPLETED.name())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var document : querySnapshot.getDocuments()) {
                        firestore.collection("taskInstances")
                                .document(document.getId())
                                .delete();
                    }
                });
    }

    public void updateAll(@NonNull List<TaskInstance> instances) {
        if (instances == null || instances.isEmpty()) return;

        String taskId = instances.get(0).getTaskId();

        WriteBatch batch = firestore.batch();

        for (TaskInstance instance : instances) {
            DocumentReference docRef = firestore.collection("taskInstances")
                    .document(instance.getId());

            batch.update(docRef, "taskId", taskId);
        }

        batch.commit();
    }

    public void updateStatus(@NonNull String taskInstanceId, @NonNull TaskStatus newStatus) {
        DocumentReference docRef = firestore.collection("taskInstances").document(taskInstanceId);

        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                docRef.update("status", newStatus.name());
            }
        });
    }
}
