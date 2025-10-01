package com.example.habitmaster.data.firebases;

import android.util.Log;

import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ICallbackVoid;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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


    public void hasUserSentMessageToday(String userId, String allianceId, LocalDateTime missionStartDateTime, ICallback<Boolean> callback) {
        LocalDate today = LocalDate.now();
        LocalTime missionStartTime = missionStartDateTime.toLocalTime();

        LocalDateTime windowStart;
        LocalDateTime windowEnd = today.atTime(missionStartTime);

        if (missionStartDateTime.toLocalDate().isEqual(today)) {
            // Misija je danas – prozor je od startDateTime do danas + startTime
            windowStart = missionStartDateTime;
            windowEnd = today.atTime(LocalTime.now());
            Log.d("CHAT", "today: start: " + windowStart.toString() + ", end: " + windowEnd.toString());
        } else {
            // Misija je pre danas – prozor je od juče u startTime do danas u startTime
            Log.d("CHAT", "not today");
            if (missionStartTime.isAfter(LocalTime.now())) {
                Log.d("CHAT", "after now");
                windowStart = today.minusDays(1).atTime(missionStartTime);
                windowEnd = today.atTime(missionStartTime);
            } else {
                Log.d("CHAT", "before now");
                // Ako je missionStartTime <= now => prozor je od danas(missionStartTime) do sutra(missionStartTime)
                windowStart = today.atTime(missionStartTime);
                windowEnd = today.plusDays(1).atTime(missionStartTime);
            }
        }

        // Konverzija u java.util.Date za Firestore
        Date startDate = Date.from(windowStart.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(windowEnd.atZone(ZoneId.systemDefault()).toInstant());
        Log.d("CHAT", "startDate: " + startDate.toString());
        Log.d("CHAT", "endDate: " + endDate.toString());

        db.collection("alliances")
                .document(allianceId)
                .collection("messages")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("timestamp", new Timestamp(startDate))
                .whereLessThan("timestamp", new Timestamp(endDate))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean sent = !querySnapshot.isEmpty();
                    callback.onSuccess(sent);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

}
