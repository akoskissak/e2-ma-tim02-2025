package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.services.ICallback;

public class GetAllianceByUserIdUseCase {
    private final AllianceRepository repo;

    public GetAllianceByUserIdUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
    }

    public void execute(String userId, ICallback<Alliance> callback) {
        try {
            Alliance alliance = repo.getAllianceByLeaderId(userId);
            if(alliance == null) {
                alliance = repo.getAllianceByUserId(userId);
            }

            if(alliance != null) {
                callback.onSuccess(alliance);
            } else {
                callback.onError("Korisnik nije u savezu");
            }
        } catch (Exception e) {
            callback.onError("Greska prilikom dohvatanja saveza:" + e.getMessage());
        }
    }
}
