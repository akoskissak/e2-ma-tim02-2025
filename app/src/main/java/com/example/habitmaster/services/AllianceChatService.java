package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.domain.models.AllianceMissionProgressType;
import com.example.habitmaster.domain.usecases.LoadAllMessagesUseCase;
import com.example.habitmaster.domain.usecases.ObserveMessagesUseCase;
import com.example.habitmaster.domain.usecases.SendMessageUseCase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class AllianceChatService {
    private final SendMessageUseCase sendMessageUC;
    private final ObserveMessagesUseCase observeMessagesUC;
    private final LoadAllMessagesUseCase loadAllMessagesUC;
    private final AllianceService allianceService;

    public AllianceChatService(Context context) {
        this.sendMessageUC = new SendMessageUseCase();
        this.observeMessagesUC = new ObserveMessagesUseCase();
        this.loadAllMessagesUC = new LoadAllMessagesUseCase();
        this.allianceService = new AllianceService(context);
    }

    public void sendMessage(String allianceId, String userId, String username, String text, ICallbackVoid callback) {
        sendMessageUC.execute(allianceId, userId, username, text, new ICallbackVoid() {
            @Override
            public void onSuccess() {
                allianceService.tryUpdateAllianceProgress(userId, AllianceMissionProgressType.MESSAGE_SENT);
                callback.onSuccess();
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public ListenerRegistration subscribeToMessages(String allianceId, ICallback<AllianceMessage> callback) {
        return observeMessagesUC.execute(allianceId, callback);
    }

    public void loadAllMessages(String allianceId, ICallback<List<AllianceMessage>> callback) {
        loadAllMessagesUC.execute(allianceId, callback);
    }
}
