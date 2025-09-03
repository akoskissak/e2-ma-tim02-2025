package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.DisplayEquipment;
import com.example.habitmaster.domain.models.Equipment;
import com.example.habitmaster.domain.models.Shop;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.ShopService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.ShopAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private TextView coinsText;
    private ShopService shopService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShop);
        coinsText = findViewById(R.id.textCoins);

        UserService userService = new UserService(this);

        userService.getCurrentUser(new ICallback<>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ShopActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        coinsText.setText(String.valueOf(currentUser.getCoins()));

        Shop shop = new Shop();

        List<Equipment> equipmentList = shop.getItemsForSale();
        shopService = new ShopService(currentUser, this);
        List<DisplayEquipment> calculatedEquipmentList = shopService.getDisplayEquipmentList(equipmentList);

        ShopAdapter adapter = new ShopAdapter(calculatedEquipmentList, displayEq -> {
            Equipment original = displayEq.getOriginal();
            shopService.buyItem(original, new ICallback<>() {
                @Override
                public void onSuccess(UserEquipment ue) {
                    coinsText.setText(String.valueOf(currentUser.getCoins()));
                    Toast.makeText(ShopActivity.this, "Bought: " + ue.getName(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ShopActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
