package com.example.habitmaster.ui.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.UserStatistics;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.UserService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserStatisticsActivity extends AppCompatActivity {
    private TextView tvActiveDays, tvStreak, tvXpLast7Days, tvSpecialStarted, tvSpecialCompleted;
    private BarChart chartByCategory;
    private PieChart chartTasksStatus;
    private LineChart chartXpLast7Days, chartPercentageByDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        tvActiveDays = findViewById(R.id.tvActiveDays);
        tvStreak = findViewById(R.id.tvStreak);
        tvXpLast7Days = findViewById(R.id.tvXpLast7Days);
        tvSpecialStarted = findViewById(R.id.tvSpecialStarted);
        tvSpecialCompleted = findViewById(R.id.tvSpecialCompleted);

        chartByCategory = findViewById(R.id.chartByCategory);
        chartTasksStatus = findViewById(R.id.chartTasksStatus);
        chartXpLast7Days = findViewById(R.id.chartXpLast7Days);
        chartPercentageByDifficulty = findViewById(R.id.chartPercentageByDifficulty);

        UserService userService = new UserService(this);

        userService.getUserStatistics(new ICallback<>() {
            @Override
            public void onSuccess(UserStatistics result) {
                showStatistics(result);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(UserStatisticsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showStatistics(UserStatistics stats) {
        tvActiveDays.setText(String.valueOf(stats.getActiveDays()));
        tvStreak.setText(String.valueOf(stats.getLongestStreak()));
        tvSpecialStarted.setText(String.valueOf(stats.getSpecialMissionsStarted()));
        tvSpecialCompleted.setText(String.valueOf(stats.getSpecialMissionsCompleted()));

        int totalXp = 0;
        for (int xp : stats.getXpLast7Days()){
            totalXp += xp;
        }
        tvXpLast7Days.setText(String.valueOf(totalXp));

        showPieChart(stats);
        showBarChart(stats);
        showLineCharts(stats);
    }

    private void showPieChart(UserStatistics stats) {
        List<PieEntry> entries = new ArrayList<>();
        if (stats.getTotalCreated() > 0)
            entries.add(new PieEntry(stats.getTotalCreated(), "Created"));
        if (stats.getTotalCompleted() > 0)
            entries.add(new PieEntry(stats.getTotalCompleted(), "Completed"));
        if (stats.getTotalMissed() > 0)
            entries.add(new PieEntry(stats.getTotalMissed(), "Missed"));
        if (stats.getTotalCancelled() > 0)
            entries.add(new PieEntry(stats.getTotalCancelled(), "Cancelled"));

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No tasks"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.BLUE, Color.GREEN, Color.RED, Color.GRAY});
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Za celobrojni prikaz
            }
        });
        chartTasksStatus.setData(data);
        chartTasksStatus.getDescription().setEnabled(false);
        chartTasksStatus.setDrawHoleEnabled(false);
        chartTasksStatus.animateY(500);
        chartTasksStatus.invalidate();
    }

    private void showBarChart(UserStatistics stats) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : stats.getCompletedTasksByCategory().entrySet()){
            entries.add(new BarEntry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(Color.CYAN);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        chartByCategory.setData(data);
        XAxis xAxis = chartByCategory.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chartByCategory.getAxisLeft().setGranularity(1f);
        chartByCategory.getAxisRight().setEnabled(false);
        chartByCategory.getDescription().setEnabled(false);
        chartByCategory.animateY(500);
        chartByCategory.invalidate();
    }

    private void showLineCharts(UserStatistics stats) {
        List<Entry> xpEntries = new ArrayList<>();
        chartXpLast7Days.setNoDataText("No XP data to show");
        chartXpLast7Days.setNoDataTextColor(Color.GRAY);
        chartXpLast7Days.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);

        for(int i = 0; i < stats.getXpLast7Days().size(); i++) {
            xpEntries.add(new Entry(i, stats.getXpLast7Days().get(i)));
            int value = stats.getXpLast7Days().get(i);
            Log.d("LineChart", "Index " + i + ", XP: " + value);  // log svakog elementa
        }

        if (xpEntries.isEmpty()) {
            chartXpLast7Days.clear();
            chartXpLast7Days.invalidate();
        } else {
            LineDataSet xpDataSet = new LineDataSet(xpEntries, "XP last 7 days");
            xpDataSet.setColor(Color.MAGENTA);
            xpDataSet.setCircleColor(Color.MAGENTA);
            xpDataSet.setLineWidth(2f);
            xpDataSet.setCircleRadius(4f);
            xpDataSet.setValueTextSize(10f);

            LineData xpData = new LineData(xpDataSet);
            chartXpLast7Days.setData(xpData);
            chartXpLast7Days.getDescription().setEnabled(false);
            chartXpLast7Days.animateX(500);
            chartXpLast7Days.invalidate();
        }


        Map<TaskDifficulty, Float> percentByDifficulty = stats.getDifficultyPercent();
        List<Entry> diffEntries = new ArrayList<>();
        TaskDifficulty[] difficulties = TaskDifficulty.values();

        for(int i = 0; i < difficulties.length; i++){
            TaskDifficulty diff = difficulties[i];
            float percent = percentByDifficulty.getOrDefault(diff, 0f);
            diffEntries.add(new Entry(i, percent));
        }

        LineDataSet diffDataSet = new LineDataSet(diffEntries, " percent by Difficulty");
        diffDataSet.setColor(Color.BLUE);
        diffDataSet.setCircleColor(Color.BLUE);
        diffDataSet.setLineWidth(2f);
        diffDataSet.setCircleRadius(4f);
        diffDataSet.setValueTextSize(12f);

        LineData diffData = new LineData(diffDataSet);
        chartPercentageByDifficulty.setData(diffData);

        String[] labels = new String[difficulties.length];
        for (int i = 0; i < difficulties.length; i++) {
            labels[i] = difficulties[i].name();
        }

        XAxis xAxis = chartPercentageByDifficulty.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-25f);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxisLeft = chartPercentageByDifficulty.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(100f);
        yAxisLeft.setGranularity(10f);
        chartPercentageByDifficulty.getAxisRight().setEnabled(false);

        chartPercentageByDifficulty.getDescription().setEnabled(false);
        chartPercentageByDifficulty.animateX(500);
        chartPercentageByDifficulty.invalidate();
    }
}
