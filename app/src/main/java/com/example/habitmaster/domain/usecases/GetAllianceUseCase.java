package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.services.ICallback;

public class GetAllianceUseCase {
    private final AllianceRepository repo;

    public GetAllianceUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
    }

    public void execute(String allianceId, ICallback<Alliance> callback) {

        Alliance alliance = repo.getAllianceById(allianceId);
        if(alliance != null) {
            callback.onSuccess(alliance);
            return;
        }

        callback.onError("Savez nije pronadjen");
    }
}
