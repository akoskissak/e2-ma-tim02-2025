package com.example.habitmaster.domain.usecases;

import com.example.habitmaster.data.repositories.UserLocalRepository;

public class AddUserXpUseCase {
    private final UserLocalRepository userRepository;

    public interface Callback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public AddUserXpUseCase(UserLocalRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String userId, int xp) {
        userRepository.addXp(userId, xp);
    }
}
