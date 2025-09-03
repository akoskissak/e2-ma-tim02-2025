package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habitmaster.R;
import com.example.habitmaster.ui.fragments.InventoryFragment;


public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // pri rotaciji ekrana dodavanje fragmenta
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new InventoryFragment())
                    .commit();
        }
    }
}
