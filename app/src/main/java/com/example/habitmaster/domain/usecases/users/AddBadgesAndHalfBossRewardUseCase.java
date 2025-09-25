package com.example.habitmaster.domain.usecases.users;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.firebases.FirebaseUserRepository;
import com.example.habitmaster.domain.models.Boss;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.services.ICallback;

import java.util.List;

public class AddBadgesAndHalfBossRewardUseCase {

    private final FirebaseUserRepository repository;

    public AddBadgesAndHalfBossRewardUseCase(Context context) {
        this.repository = new FirebaseUserRepository(context);
    }

    public void execute(List<String> members) {
        for (String id: members) {
            repository.getUserById(id, new ICallback<User>() {
                @Override
                public void onSuccess(User user) {
                    user.setBadgesCount(user.getBadgesCount() + 1);
                    user.setCoins(user.getCoins() + Boss.calculateHalfNextReward(user.getLevel()));

                    repository.update(user);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.d("Add badges and half boss reward", "onError: " + errorMessage);
                }
            });

        }
    }
}
