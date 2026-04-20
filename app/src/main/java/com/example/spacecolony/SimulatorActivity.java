package com.example.spacecolony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.models.CrewAdapter;
import com.example.spacecolony.models.CrewDatabase;
import com.example.spacecolony.models.CrewMember;

import java.util.List;

public class SimulatorActivity extends AppCompatActivity {

    private List<CrewMember> simulatorList;
    private CrewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        // Load ONLY the crew members currently in the Simulator
        simulatorList = CrewDatabase.getInstance().getCrewByLocation("Simulator");

        // Reset checkboxes when opening the screen
        for (CrewMember cm : simulatorList) {
            cm.isSelectedForUI = false;
        }

        RecyclerView rvSimulator = findViewById(R.id.rv_simulator);
        rvSimulator.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewAdapter(simulatorList);
        rvSimulator.setAdapter(adapter);

        Button btnTrain = findViewById(R.id.btn_train_crew);
        Button btnToQuarters = findViewById(R.id.btn_sim_to_quarters);

        btnTrain.setOnClickListener(v -> trainSelectedCrew());
        btnToQuarters.setOnClickListener(v -> moveSelectedToQuarters());
    }

    private void trainSelectedCrew() {
        java.util.List<String> trainedNames = new java.util.ArrayList<>();

        for (CrewMember cm : simulatorList) {
            if (cm.isSelectedForUI) {
                cm.train();
                cm.isSelectedForUI = false;
                trainedNames.add(cm.getName());
            }
        }

        if (!trainedNames.isEmpty()) {
            adapter.notifyDataSetChanged();
            String namesString = android.text.TextUtils.join(", ", trainedNames);
            Toast.makeText(this, namesString + " gained +1 XP!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Select crew members to train first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveSelectedToQuarters() {
        java.util.List<String> movedNames = new java.util.ArrayList<>();

        for (CrewMember cm : simulatorList) {
            if (cm.isSelectedForUI) {
                cm.setLocation("Quarters");
                cm.restoreEnergy();
                cm.isSelectedForUI = false;
                movedNames.add(cm.getName());
            }
        }

        if (!movedNames.isEmpty()) {
            String namesString = android.text.TextUtils.join(", ", movedNames);
            Toast.makeText(this, namesString + " returned to Quarters.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Select crew members to move first!", Toast.LENGTH_SHORT).show();
        }
    }
}