package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseAllianceChatRepository;
import com.example.habitmaster.domain.models.AllianceMessage;
import com.example.habitmaster.services.ICallback;
import com.google.firebase.firestore.ListenerRegistration;

public class ObserveMessagesUseCase {
    private final FirebaseAllianceChatRepository repo;

    public ObserveMessagesUseCase() {
        this.repo = new FirebaseAllianceChatRepository();
    }

    public ListenerRegistration execute(String allianceId, ICallback<AllianceMessage> callback) {
        return repo.listenForMessages(allianceId, callback);
    }
}
