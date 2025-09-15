package com.example.habitmaster.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.habitmaster.R;
import com.example.habitmaster.ui.fragments.FriendListFragment;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        if (savedInstanceState == null) {
            FriendListFragment friendListFragment = new FriendListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.friendFragmentContainer, friendListFragment);
            transaction.commit();
        }
    }
}
