package com.example.habitmaster.ui.adapters;


import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.AllianceMessage;

import java.util.ArrayList;
import java.util.List;

public class AllianceChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    private final List<AllianceMessage> messages = new ArrayList<>();
    private final String currentUserId;

    public AllianceChatAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void addMessage(AllianceMessage msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<AllianceMessage> msgs) {
        messages.clear();
        messages.addAll(msgs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(currentUserId) ? TYPE_ME : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == TYPE_ME)
                ? R.layout.item_message_me
                : R.layout.item_message_other;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessageViewHolder) holder).bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView text, username, timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.txtMessage);
            username = itemView.findViewById(R.id.txtUsername);
            timestamp = itemView.findViewById(R.id.txtTimestamp);
        }

        public void bind(AllianceMessage msg) {
            text.setText(msg.getContent());
            username.setText(msg.getSenderUsername());
            timestamp.setText(DateFormat.format("HH:mm", msg.getTimestamp()));
        }
    }
}