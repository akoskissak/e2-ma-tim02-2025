package com.example.habitmaster.domain.usecases.alliances;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceChatRepository;
import com.example.habitmaster.services.ICallback;

import java.time.LocalDateTime;

public class HasUserSentMessageTodayUseCase {

    private final FirebaseAllianceChatRepository remoteRepo;
    public HasUserSentMessageTodayUseCase(Context context) {
        this.remoteRepo = new FirebaseAllianceChatRepository();
    }

    public void execute(String userId, String allianceId, LocalDateTime missionStartDateTime, ICallback<Boolean> callback) {
        remoteRepo.hasUserSentMessageToday(userId, allianceId, missionStartDateTime, new ICallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
