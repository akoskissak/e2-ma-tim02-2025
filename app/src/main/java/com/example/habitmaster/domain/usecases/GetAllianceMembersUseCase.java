package com.example.habitmaster.domain.usecases;


import com.example.habitmaster.data.firebases.FirebaseAllianceRepository;
import com.example.habitmaster.services.ICallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class GetAllianceMembersUseCase {
    private final FirebaseAllianceRepository repo;

    public GetAllianceMembersUseCase() {
        this.repo = new FirebaseAllianceRepository();
    }

    public void getMemberUsernamesByAllianceId(String allianceId, ICallback<List<String>> callback) {
        repo.getMemberUsernamesByAllianceId(allianceId, membersUsernames -> {
            if (membersUsernames != null && !membersUsernames.isEmpty()) {
                callback.onSuccess(membersUsernames);
            } else {
                callback.onError("Savez nema članova");
            }
        });
    }

    public void getMemberIdsByAllianceId(String allianceId, ICallback<List<String>> callback) {
        repo.getMemberIdsByAllianceId(allianceId, new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> ids) {
                if (ids != null && !ids.isEmpty()) {
                    callback.onSuccess(ids);
                } else {
                    callback.onError("No members in alliance");
                }
            }
        });
    }
}
