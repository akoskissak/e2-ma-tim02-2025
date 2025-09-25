package com.example.habitmaster.domain.usecases;


import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class GetAllianceMembersUseCase {
    private final FirebaseAllianceRepository repo;

    public GetAllianceMembersUseCase() {
        this.repo = new FirebaseAllianceRepository();
    }

    public void execute(String allianceId, ICallback<List<String>> callback) {
        repo.getMembersByAllianceId(allianceId, membersUsernames -> {
            if (membersUsernames != null && !membersUsernames.isEmpty()) {
                callback.onSuccess(membersUsernames);
            } else {
                callback.onError("Savez nema članova");
            }
        });
    }
}
