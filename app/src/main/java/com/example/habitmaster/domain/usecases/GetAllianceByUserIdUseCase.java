package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.services.ICallback;

public class GetAllianceByUserIdUseCase {
    private final FirebaseAllianceRepository repo;

    public GetAllianceByUserIdUseCase() {
        this.repo = new FirebaseAllianceRepository();
    }

    public void execute(String userId, ICallback<Alliance> callback) {
        repo.getAllianceByUserId(
                userId,
                alliance -> {
                    if (alliance != null) {
                        callback.onSuccess(alliance);
                    } else {
                        callback.onError("Korisnik nije u savezu");
                    }
                }
        );
    }
}
