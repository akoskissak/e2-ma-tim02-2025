package com.example.habitmaster.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Friend;
import com.example.habitmaster.utils.AvatarUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<Friend> friends;
    private OnFriendClickListener listener;
    private final boolean isSelectionMode;

    public interface OnFriendClickListener {
        void onFriendClicked(Friend friend);


        void onFriendSelected(Friend friend, boolean selected);
    }

    public FriendAdapter(List<Friend> friends, boolean isSelectionMode, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
        this.isSelectionMode = isSelectionMode;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.txtUsername.setText(friend.getFriendUsername());
        holder.imgAvatar.setImageResource(AvatarUtils.getAvatarResId(friend.getFriendAvatarName()));

        if(isSelectionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.checkBox.setOnCheckedChangeListener(null);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onFriendSelected(friend, isChecked);
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if(!isSelectionMode) {
                listener.onFriendClicked(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateList(List<Friend> newList) {
        friends = newList;
        notifyDataSetChanged();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgAddedIndicator;
        TextView txtUsername;
        CheckBox checkBox;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            checkBox = itemView.findViewById(R.id.checkBoxSelectFriend);
        }
    }
}
