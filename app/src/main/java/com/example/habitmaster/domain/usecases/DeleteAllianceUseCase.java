package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.services.ICallbackVoid;

public class DeleteAllianceUseCase {
    private final AllianceRepository repo;
    private final FirebaseAllianceRepository firebaseRepo;

    public DeleteAllianceUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
        this.firebaseRepo = new FirebaseAllianceRepository();
    }

    public void execute(String leaderId, ICallbackVoid callback) {
        Alliance alliance = repo.getAllianceByLeaderId(leaderId);

        if (alliance == null) {
            callback.onError("Niste vodja saveza.");
            return;
        }

        if (alliance.isMissionStarted()) {
            callback.onError("Ne mozete ukinuti savez dok je misija pokrenuta.");
            return;
        }

        repo.deleteAlliance(alliance.getId());
        firebaseRepo.deleteAlliance(alliance.getId(), task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError(task.getException() != null ? task.getException().getMessage() : "Firebase error");
            }
        });

        callback.onSuccess();
    }
}
