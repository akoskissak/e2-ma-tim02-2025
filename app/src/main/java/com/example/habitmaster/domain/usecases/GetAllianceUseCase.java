package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.services.ICallback;

public class GetAllianceUseCase {
    private final FirebaseAllianceRepository repo;

    public GetAllianceUseCase() {

        this.repo = new FirebaseAllianceRepository();
    }

    public void execute(String allianceId, ICallback<Alliance> callback) {
        repo.getAllianceById(allianceId,
                alliance -> {
                    if (alliance != null) {
                        callback.onSuccess(alliance);
                    } else {
                        callback.onError("Savez nije pronađen");
                    }
                },
                e -> callback.onError("Greška pri dohvatu saveza: " + e.getMessage())
        );
    }
}
