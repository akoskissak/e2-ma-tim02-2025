package com.example.habitmaster.ui.fragments;

import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserEquipment;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserEquipmentService;
import com.example.habitmaster.services.UserService;
import com.example.habitmaster.ui.adapters.ActiveEquipmentAdapter;
import com.example.habitmaster.ui.adapters.InventoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryFragment extends Fragment {
    private RecyclerView recyclerView, activeRecyclerView;
    private TextView tvCoins, tvNoEquipment;
    private InventoryAdapter adapter;
    private ActiveEquipmentAdapter activeAdapter;
    private List<UserEquipment> inventoryList = new ArrayList<>();
    private List<UserEquipment> activeEquipmentList = new ArrayList<>();
    private User currentUser;
    private UserEquipmentService userEquipmentService;

    public InventoryFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        tvCoins = view.findViewById(R.id.coinsText);
        tvNoEquipment = view.findViewById(R.id.tvNoEquipment);
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        activeRecyclerView = view.findViewById(R.id.activeRecyclerView);
        activeRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        userEquipmentService = new UserEquipmentService(requireContext());
        UserService userService = new UserService(requireContext());

        userService.getCurrentUser(new ICallback<>() {
            @Override
            public void onSuccess(User result) {
                currentUser = result;
                tvCoins.setText(String.valueOf(currentUser.getCoins()));
                loadUserEquipment();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadUserEquipment() {
        if (currentUser == null) return;

        userEquipmentService.getAllUserEquipment(currentUser.getId(), new ICallback<>() {
            @Override
            public void onSuccess(List<UserEquipment> result) {

                if (result == null || result.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvNoEquipment.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoEquipment.setVisibility(View.GONE);

                    inventoryList = result;

                    adapter = new InventoryAdapter(inventoryList, new UserEquipmentService(requireContext()), currentUser, item -> onActivateClicked(item), newCoins -> tvCoins.setText(String.valueOf(newCoins)));
                    recyclerView.setAdapter(adapter);
                }
                inventoryList = result;



                // Filtriranje samo aktivnih equipmenta
                activeEquipmentList = inventoryList.stream()
                        .filter(UserEquipment::isActivated)
                        .collect(Collectors.toList());

                activeAdapter = new ActiveEquipmentAdapter(activeEquipmentList, new UserEquipmentService(requireContext()), item -> onDeactivateClicked(item));
                activeRecyclerView.setAdapter(activeAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Greska: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onActivateClicked(UserEquipment item) {
        userEquipmentService.activateItem(item, inventoryList);

        activeEquipmentList = inventoryList.stream()
                .filter(UserEquipment::isActivated)
                .collect(Collectors.toList());

        if (activeAdapter != null) {
            activeAdapter.updateItems(activeEquipmentList);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(getContext(), item.getName() + " activated!", Toast.LENGTH_SHORT).show();
    }

    private void onDeactivateClicked(UserEquipment item) {
        userEquipmentService.deactivateEquipment(item.getId());

        item.setActivated(false);

        activeEquipmentList = inventoryList.stream()
                .filter(UserEquipment::isActivated)
                .collect(Collectors.toList());

        if (activeAdapter != null) {
            activeAdapter.updateItems(activeEquipmentList);
        }

        adapter.notifyDataSetChanged();

        Toast.makeText(getContext(), item.getName() + " deaktiviran!", Toast.LENGTH_SHORT).show();
    }
}

