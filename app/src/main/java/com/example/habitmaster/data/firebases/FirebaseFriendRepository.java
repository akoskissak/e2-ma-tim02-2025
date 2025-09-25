package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.Friend;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseFriendRepository {

    private final FirebaseFirestore db;

    public FirebaseFriendRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addFriend(Friend friend, String currentUserId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", currentUserId);
        data.put("friendUserId", friend.getFriendUserId());
        data.put("friendUsername", friend.getFriendUsername());
        data.put("friendAvatarName", friend.getFriendAvatarName());

        db.collection("friends")
                .add(data)
                .addOnSuccessListener(documentReference -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    public void removeFriend(String friendUserId, String currentUserId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("friends")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("friendUserId", friendUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(aVoid -> onSuccess.onSuccess(null))
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    public void getAllFriends(String userId, OnSuccessListener<List<Friend>> onSuccess, OnFailureListener onFailure) {
        db.collection("friends")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Friend> friends = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Friend f = new Friend();
                        f.setFriendUserId(doc.getString("friendUserId"));
                        f.setFriendUsername(doc.getString("friendUsername"));
                        f.setFriendAvatarName(doc.getString("friendAvatarName"));
                        friends.add(f);
                    }
                    onSuccess.onSuccess(friends);
                })
                .addOnFailureListener(onFailure);
    }

    public void isAlreadyFriend(String currentUserId, String viewedUserId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        db.collection("friends")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("friendUserId", viewedUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> onSuccess.onSuccess(!querySnapshot.isEmpty()))
                .addOnFailureListener(onFailure);
    }
}

