package com.example.habitmaster.data.firebases;

import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ICallbackVoid;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAllianceChatRepository {
    private final FirebaseFirestore db;

    public FirebaseAllianceChatRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void sendMessage(String allianceId, AllianceMessage message, ICallbackVoid callback) {
        db.collection("alliances")
                .document(allianceId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(doc -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public ListenerRegistration listenForMessages(String allianceId, long lastTimestamp, ICallback<AllianceMessage> callback) {
        return db.collection("alliances")
                .document(allianceId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .whereGreaterThan("timestamp", lastTimestamp)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        callback.onError(e.getMessage());
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            AllianceMessage msg = dc.getDocument().toObject(AllianceMessage.class);
                            callback.onSuccess(msg);
                        }
                    }
                });
    }
    public void getAllMessages(String allianceId, ICallback<List<AllianceMessage>> callback) {
        db.collection("alliances")
                .document(allianceId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<AllianceMessage> messages = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        messages.add(doc.toObject(AllianceMessage.class));
                    }
                    callback.onSuccess(messages);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


}
