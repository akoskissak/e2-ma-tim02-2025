package com.example.habitmaster.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.habitmaster.R;
import com.example.habitmaster.ui.adapters.AvatarSpinnerAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AvatarPickerFragment extends Fragment {
    public interface OnAvatarSelectedListener {
        void onAvatarSelected(String avatarName);
    }
    private OnAvatarSelectedListener listener;
    private final String[] avatarNames = {
            "avatar1",
            "avatar2",
            "avatar3",
            "avatar4",
            "avatar5"
    };

    @Override
    public void onAttach(@NotNull Context ctx){
        super.onAttach(ctx);
        if (ctx instanceof OnAvatarSelectedListener){
            listener = (OnAvatarSelectedListener) ctx;
        } else {
            throw new RuntimeException(ctx.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar_picker, container, false);
        Spinner avatarSpinner = view.findViewById(R.id.avatarSpinner);

        AvatarSpinnerAdapter adapter = new AvatarSpinnerAdapter(requireContext(), avatarNames);
        avatarSpinner.setAdapter(adapter);

        avatarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onAvatarSelected(avatarNames[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return view;
    }
}
