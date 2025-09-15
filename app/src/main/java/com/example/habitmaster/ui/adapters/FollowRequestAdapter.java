package com.example.habitmaster.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.data.mapper.FollowRequestMapper;
import com.example.habitmaster.domain.models.FollowRequest;

import java.util.List;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.RequestViewHolder> {
    private final List<FollowRequestWithUsername> requests;
    private final OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onAccept(FollowRequest request);
        void onDecline(FollowRequest request);
    }

    public FollowRequestAdapter(List<FollowRequestWithUsername> requests, OnRequestClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        FollowRequestWithUsername request = requests.get(position);
        holder.tvUsername.setText(request.getFromUsername());

        FollowRequest domainRequest = FollowRequestMapper.toDomain(request);

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(domainRequest));
        holder.btnDecline.setOnClickListener(v -> listener.onDecline(domainRequest));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        Button btnAccept, btnDecline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}
