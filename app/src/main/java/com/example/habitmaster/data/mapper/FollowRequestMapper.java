package com.example.habitmaster.data.mapper;

import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.domain.models.FollowRequest;

public class FollowRequestMapper {
    public static FollowRequest toDomain(FollowRequestWithUsername viewModel) {
        return new FollowRequest(
                viewModel.getId(),
                viewModel.getFromUserId(),
                viewModel.getToUserId(),
                viewModel.getStatus()
        );
    }
}