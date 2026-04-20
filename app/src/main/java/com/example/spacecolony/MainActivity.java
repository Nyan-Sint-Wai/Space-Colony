package com.example.spacecolony;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Added so we can show the Save/Load popup messages
import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolony.models.CrewDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvSummary = findViewById(R.id.tv_colony_summary);

        // 1. Setup Navigation Buttons
        findViewById(R.id.btn_nav_recruit).setOnClickListener(v ->
                startActivity(new Intent(this, RecruitActivity.class)));

        findViewById(R.id.btn_nav_quarters).setOnClickListener(v ->
                startActivity(new Intent(this, QuartersActivity.class)));

        findViewById(R.id.btn_nav_simulator).setOnClickListener(v ->
                startActivity(new Intent(this, SimulatorActivity.class)));

        findViewById(R.id.btn_nav_mission).setOnClickListener(v ->
                startActivity(new Intent(this, MissionControlActivity.class)));

        // 2. Setup Manual Save/Load Buttons
        Button btnSave = findViewById(R.id.btn_save_game);
        Button btnLoad = findViewById(R.id.btn_load_game);

        btnSave.setOnClickListener(v -> {
            CrewDatabase.getInstance().saveToFile(this);
            Toast.makeText(this, "Progress Saved!", Toast.LENGTH_SHORT).show();
        });

        btnLoad.setOnClickListener(v -> {
            CrewDatabase.getInstance().loadFromFile(this);
            updateSummary(); // Refresh UI immediately after loading so the numbers change
            Toast.makeText(this, "Data Loaded!", Toast.LENGTH_SHORT).show();
        });

        updateSummary();
    }

    // Refresh the summary every time we return to the home screen
    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        int qCount = CrewDatabase.getInstance().getCrewByLocation("Quarters").size();
        int sCount = CrewDatabase.getInstance().getCrewByLocation("Simulator").size();
        int mCount = CrewDatabase.getInstance().getCrewByLocation("Mission Control").size();
        int medCount = CrewDatabase.getInstance().getCrewByLocation("Medbay").size();

        int totalWins = CrewDatabase.getInstance().getTotalMissions();
        int totalLosses = CrewDatabase.getInstance().getLostMissions();

        String summary = "--- COLONY STATUS REPORT ---\n" +
                "Quarters: " + qCount + " active\n" +
                "Simulator: " + sCount + " training\n" +
                "Mission Control: " + mCount + " deployed\n" +
                "Medbay: " + medCount + " in time-out\n" +
                "Total Missions Won: " + totalWins + "\n" +
                "Total Missions Lost: " + totalLosses;

        tvSummary.setText(summary);
    }
}