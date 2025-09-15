package com.example.habitmaster.domain.usecases;

import android.content.Context;

import com.example.habitmaster.data.repositories.AllianceRepository;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetAllianceMembersUseCase {
    private final AllianceRepository repo;

    public GetAllianceMembersUseCase(Context ctx) {
        this.repo = new AllianceRepository(ctx);
    }

    public void execute(String allianceId, ICallback<List<String>> callback) {
        try {
            List<String> membersUsernames = repo.getMembersByAllianceId(allianceId);
            callback.onSuccess(membersUsernames);
        } catch (Exception e) {
            callback.onError("Greska pri dohvatanju clanova saveza: " + e.getMessage());
        }
    }
}
