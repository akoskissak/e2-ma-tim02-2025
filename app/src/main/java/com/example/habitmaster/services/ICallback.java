package com.example.habitmaster.services;

import com.example.habitmaster.domain.models.User;

public interface ICallback {
    void onSuccess(User user);
    void onError(String errorMessage);
}
