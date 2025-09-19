package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseAllianceChatRepository;
import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class LoadAllMessagesUseCase {
    private final FirebaseAllianceChatRepository repo;

    public LoadAllMessagesUseCase() {
        this.repo = new FirebaseAllianceChatRepository();
    }

    public void execute(String allianceId, ICallback<List<AllianceMessage>> callback) {
        repo.getAllMessages(allianceId, callback);
    }
}
