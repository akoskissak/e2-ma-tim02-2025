package com.example.habitmaster.data.firebases;

import androidx.annotation.NonNull;

import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.util.List;

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
        String threeDaysAgo = LocalDate.now().minusDays(3).toString();

        DocumentReference docRef = firestore.collection("taskInstances").document(taskInstanceId);

        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String date = snapshot.getString("date");
                if (date != null && date.compareTo(threeDaysAgo) >= 0) {
                    docRef.update("status", newStatus.name());
                }
            }
        });
    }
}
