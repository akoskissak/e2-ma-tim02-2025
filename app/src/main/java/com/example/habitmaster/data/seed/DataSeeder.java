package com.example.habitmaster.data.seed;

import android.content.Context;
import android.util.Log;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.data.repositories.UserLocalRepository;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.usecases.tasks.CreateTaskUseCase;
import com.example.habitmaster.utils.Prefs;
import com.example.habitmaster.utils.exceptions.ColorNotUniqueException;
import com.example.habitmaster.utils.exceptions.NameNotUniqueException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class DataSeeder {

    private final Context context;
    private final DatabaseHelper dbHelper;
    private final UserLocalRepository userLocalRepo;
    private final CategoryRepository categoryLocalRepo;
    private final CreateTaskUseCase createTaskUseCase;
    private Prefs prefs;

    public DataSeeder(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.userLocalRepo = new UserLocalRepository(context);
        this.categoryLocalRepo = new CategoryRepository(context);
        this.prefs = new Prefs(context);
        this.createTaskUseCase = new CreateTaskUseCase(context);
    }

    public void runSeedIfNeeded() {
        Log.d("DATA SEEDER", "runSeedIfNeeded: ");
        if (!prefs.isSeedDone()) {
            Log.d("DATA SEEDER", "seeding data");
            seedData();
            prefs.setSeedDone(true);
        }
    }

    private String loadJSONFromAsset(String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void seedData() {
        String json = loadJSONFromAsset("seed.json");
        if (json == null) return;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (jsonElement, type, context) -> LocalDate.parse(jsonElement.getAsString()))
                .create();
        SeedData seedData = gson.fromJson(json, SeedData.class);

        seedSQLite(seedData);
    }

    private void seedSQLite(SeedData seedData) {
        // Users
        for (User u : seedData.users) {
            userLocalRepo.insert(u);
        }

        // Categories
        if (seedData.categories != null) {
            for (Category c : seedData.categories) {
                try {
                    categoryLocalRepo.addCategory(c);
                } catch (NameNotUniqueException | ColorNotUniqueException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void runTaskSeedIfNeeded() {
        Log.d("DATA SEEDER TASKS", "runSeedIfNeeded: ");
        if (!prefs.isTaskSeedDone()) {
            Log.d("DATA SEEDER TASKS", "seeding task data");
            seedTaskData();
            prefs.setTaskSeedDone(true);
        }
    }

    private void seedTaskData() {
        String json = loadJSONFromAsset("tasks.json");
        if (json == null) return;

        Gson gson = new GsonBuilder().create();
        TaskSeed[] tasks = gson.fromJson(json, TaskSeed[].class);

        for (TaskSeed t : tasks) {
            try {
                createTaskUseCase.execute(
                        t.name,
                        t.description,
                        t.categoryId,
                        t.frequencyStr,
                        t.repeatInterval,
                        t.startDateStr,
                        t.endDateStr,
                        t.executionTimeStr,
                        t.difficultyStr,
                        t.importanceStr,
                        new CreateTaskUseCase.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("DATA SEEDER TASKS", "success");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.d("DATA SEEDER TASKS", "error" + errorMessage);
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

