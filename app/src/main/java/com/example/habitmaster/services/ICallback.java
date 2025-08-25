package com.example.habitmaster.services;

import com.example.habitmaster.domain.models.User;

public interface ICallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}
