package com.example.habitmaster.services;

import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.domain.usecases.LoadAllMessagesUseCase;
import com.example.habitmaster.domain.usecases.ObserveMessagesUseCase;
import com.example.habitmaster.domain.usecases.SendMessageUseCase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class AllianceChatService {
    private final SendMessageUseCase sendMessageUC;
    private final ObserveMessagesUseCase observeMessagesUC;
    private final LoadAllMessagesUseCase loadAllMessagesUC;

    public AllianceChatService() {
        this.sendMessageUC = new SendMessageUseCase();
        this.observeMessagesUC = new ObserveMessagesUseCase();
        this.loadAllMessagesUC = new LoadAllMessagesUseCase();
    }

    public void sendMessage(String allianceId, String userId, String username, String text, ICallbackVoid callback) {
        sendMessageUC.execute(allianceId, userId, username, text, callback);
    }

    public ListenerRegistration subscribeToMessages(String allianceId, ICallback<AllianceMessage> callback) {
        return observeMessagesUC.execute(allianceId, callback);
    }

    public void loadAllMessages(String allianceId, ICallback<List<AllianceMessage>> callback) {
        loadAllMessagesUC.execute(allianceId, callback);
    }
}
