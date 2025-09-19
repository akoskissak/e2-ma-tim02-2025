package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseAllianceChatRepository;
import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.services.ICallbackVoid;

public class SendMessageUseCase {
    private final FirebaseAllianceChatRepository repo;

    public SendMessageUseCase() {
        this.repo = new FirebaseAllianceChatRepository();
    }

    public void execute(String allianceId, String userId, String username, String text, ICallbackVoid callback) {
        AllianceMessage message = new AllianceMessage(userId, username, text, System.currentTimeMillis());
        repo.sendMessage(allianceId, message, callback);
    }
}
