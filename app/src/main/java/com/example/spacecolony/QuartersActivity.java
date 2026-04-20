package com.example.spacecolony;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.models.CrewAdapter;
import com.example.spacecolony.models.CrewDatabase;
import com.example.spacecolony.models.CrewMember;

import java.util.ArrayList;
import java.util.List;

public class QuartersActivity extends AppCompatActivity {

    private RecyclerView rvQuarters;
    private CrewAdapter adapter;
    private List<CrewMember> quartersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        rvQuarters = findViewById(R.id.rv_quarters);
        rvQuarters.setLayoutManager(new LinearLayoutManager(this));

        quartersList = new ArrayList<>();
        adapter = new CrewAdapter(quartersList);
        rvQuarters.setAdapter(adapter);

        // Setup Move Buttons
        findViewById(R.id.btn_move_simulator).setOnClickListener(v -> moveSelectedCrew("Simulator"));
        findViewById(R.id.btn_move_mission).setOnClickListener(v -> moveSelectedCrew("Mission Control"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        quartersList.clear();

        // 1. Load the healthy crew
        quartersList.addAll(CrewDatabase.getInstance().getCrewByLocation("Quarters"));

        // 2. Load the injured crew right below them so you can see their time-out clock!
        quartersList.addAll(CrewDatabase.getInstance().getCrewByLocation("Medbay"));

        adapter.notifyDataSetChanged();
    }

    private void moveSelectedCrew(String destination) {
        List<String> movedNames = new ArrayList<>();

        for (CrewMember cm : quartersList) {
            if (cm.isSelectedForUI && cm.getLocation().equals("Quarters")) {
                cm.setLocation(destination);
                cm.isSelectedForUI = false; // Reset their checkbox for next time
                movedNames.add(cm.getName());
            }
        }

        if (!movedNames.isEmpty()) {

            // Pop up the specific names of the people who moved
            String namesString = TextUtils.join(", ", movedNames);
            Toast.makeText(this, namesString + " moved to " + destination, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Please select at least one active crew member!", Toast.LENGTH_SHORT).show();
        }
    }
}