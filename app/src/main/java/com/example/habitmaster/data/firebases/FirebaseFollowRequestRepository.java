package com.example.habitmaster.data.firebases;

import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.domain.models.FollowRequestStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseFollowRequestRepository {
    private final FirebaseFirestore db;

    public FirebaseFollowRequestRepository() {
        this.db = FirebaseFirestore.getInstance();
    }
    public void getPendingRequests(String toUserId, OnSuccessListener<List<FollowRequestWithUsername>> onSuccess) {
        db.collection("followRequests")
                .whereEqualTo("toUserId", toUserId)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<FollowRequestWithUsername> requests = new ArrayList<>();

                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String fromUserId = doc.getString("fromUserId");
                        String requestId = doc.getId();
                        String statusStr = doc.getString("status");

                        Task<DocumentSnapshot> userTask = db.collection("users")
                                .document(fromUserId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String username = userDoc.getString("username");
                                    FollowRequestStatus status = FollowRequestStatus.valueOf(statusStr);

                                    requests.add(new FollowRequestWithUsername(
                                            requestId,
                                            fromUserId,
                                            toUserId,
                                            status,
                                            username
                                    ));
                                });
                        userTasks.add(userTask);
                    }

                    Tasks.whenAll(userTasks).addOnSuccessListener(aVoid -> {
                        onSuccess.onSuccess(requests);
                    });

                });
    }

    public void createFollowRequest(FollowRequest request) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("fromUserId", request.getFromUserId());
        requestData.put("toUserId", request.getToUserId());
        requestData.put("status", request.getStatus().name());

        db.collection("followRequests")
                .document(request.getId())
                .set(requestData);
    }

    public void isPending(String fromUserId, String toUserId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        db.collection("followRequests")
                .whereEqualTo("fromUserId", fromUserId)
                .whereEqualTo("toUserId", toUserId)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean exists = !querySnapshot.isEmpty();
                    onSuccess.onSuccess(exists);
                })
                .addOnFailureListener(onFailure);
    }

    public void updateRequestStatus(String requestId, String newStatus, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference docRef = db.collection("followRequests").document(requestId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

}
