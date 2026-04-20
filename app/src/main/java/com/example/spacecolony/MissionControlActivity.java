package com.example.spacecolony;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spacecolony.models.*;
import java.util.ArrayList;
import java.util.List;

public class MissionControlActivity extends AppCompatActivity {

    private BattleManager battleManager;
    private List<CrewMember> availableCrew;
    private CrewMember c1, c2;
    private Threat threat;
    private CrewAdapter adapter;

    private LinearLayout layoutCombat, layoutStats, layoutLobby;
    private RecyclerView rvLobby;
    private TextView tvLog, tvC1Stats, tvC2Stats, tvThreatStats;
    private Button btnAttack, btnDefend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        // Bind Views
        layoutCombat = findViewById(R.id.layout_combat_actions);
        layoutStats = findViewById(R.id.layout_stats_dashboard);
        layoutLobby = findViewById(R.id.layout_lobby_buttons);
        rvLobby = findViewById(R.id.rv_mission_lobby);
        tvLog = findViewById(R.id.tv_battle_log);
        tvC1Stats = findViewById(R.id.tv_crew1_stats);
        tvC2Stats = findViewById(R.id.tv_crew2_stats);
        tvThreatStats = findViewById(R.id.tv_threat_stats);
        btnAttack = findViewById(R.id.btn_attack);
        btnDefend = findViewById(R.id.btn_defend);

        // Setup RecyclerView
        availableCrew = CrewDatabase.getInstance().getCrewByLocation("Mission Control");
        for (CrewMember cm : availableCrew) cm.isSelectedForUI = false;

        rvLobby.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewAdapter(availableCrew);
        rvLobby.setAdapter(adapter);

        findViewById(R.id.btn_launch_mission).setOnClickListener(v -> launch());
        findViewById(R.id.btn_move_to_quarters).setOnClickListener(v -> moveToQuarters());
        btnAttack.setOnClickListener(v -> turn(CrewBattleAction.ATTACK));
        btnDefend.setOnClickListener(v -> turn(CrewBattleAction.DEFEND));
    }

    private void launch() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember cm : availableCrew) if (cm.isSelectedForUI) selected.add(cm);

        if (selected.size() != 2) {
            Toast.makeText(this, "Please select exactly 2 crew members!", Toast.LENGTH_SHORT).show();
            return;
        }

        c1 = selected.get(0);
        c2 = selected.get(1);
        threat = new Threat("Alien Incursion", CrewDatabase.getInstance().getTotalMissions());
        battleManager = new BattleManager(c1, c2, threat);

        rvLobby.setVisibility(View.GONE);
        layoutLobby.setVisibility(View.GONE);
        layoutCombat.setVisibility(View.VISIBLE);
        layoutStats.setVisibility(View.VISIBLE);
        updateUI();
    }

    private void moveToQuarters() {
        int count = 0;
        for (CrewMember cm : availableCrew) {
            if (cm.isSelectedForUI) {
                cm.setLocation("Quarters");
                cm.restoreEnergy(); // Fully regenerate energy
                count++;
            }
        }
        if (count > 0) {
            finish(); // Refresh room
        } else {
            Toast.makeText(this, "Select crew to send home!", Toast.LENGTH_SHORT).show();
        }
    }

    private void turn(CrewBattleAction action) {
        if (battleManager.isMissionOver()) return;

        // 1. Add the new text to the log
        tvLog.setText(battleManager.executeTurn(action));
        updateUI();

        // 2. THE SCROLL FIX: Add a 100ms delay so the text actually renders before scrolling!
        final ScrollView scrollView = (ScrollView) tvLog.getParent();
        scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);

        if (battleManager.isMissionOver()) {
            endMission();
        }
    }

    private void endMission() {
        // 2. Transform the UI into the "Return to Base" state
        btnDefend.setVisibility(View.GONE);

        // We reuse the Attack button as the Exit button
        btnAttack.setText("MISSION OVER - RETURN TO BASE");
        btnAttack.setBackgroundColor(android.graphics.Color.GRAY);

        // 3. Update the click listener so it closes the screen instead of attacking
        btnAttack.setOnClickListener(v -> finish());

        // Optional: Scroll to the absolute bottom of the log to see "MISSION COMPLETE"
        final ScrollView scrollView = (ScrollView) tvLog.getParent();
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void updateUI() {
        // 1. Update Threat
        String statsThreat = String.format("Threat: %s\nskill: %d; res: %d; energy: %d/%d",
                threat.getName(), threat.getSkill(), threat.getResilience(),
                Math.max(0, threat.getEnergy()), threat.getMaxEnergy());
        tvThreatStats.setText(statsThreat);

        // 2. Figure out whose turn it is
        boolean isC1Turn = false;
        boolean isC2Turn = false;

        if (battleManager != null && !battleManager.isMissionOver()) {
            // Account for the engine auto-skipping dead members
            if (battleManager.isCrewATurn()) {
                if (c1.getEnergy() > 0) isC1Turn = true;
                else isC2Turn = true;
            } else {
                if (c2.getEnergy() > 0) isC2Turn = true;
                else isC1Turn = true;
            }
        }

        // 3. Format Crew A
        if (c1.getEnergy() <= 0) {
            tvC1Stats.setText(c1.getName() + " [KIA]");
            tvC1Stats.setTextColor(android.graphics.Color.RED);
        } else {
            String marker1 = isC1Turn ? "▶ [ACTIVE] " : "";
            tvC1Stats.setText(marker1 + "Crew A: " + c1.getSpecialization() + "(" + c1.getName() + ")\n" + c1.getFormattedStats());
            tvC1Stats.setTextColor(isC1Turn ? android.graphics.Color.GREEN : android.graphics.Color.WHITE);
        }

        // 4. Format Crew B
        if (c2.getEnergy() <= 0) {
            tvC2Stats.setText(c2.getName() + " [KIA]");
            tvC2Stats.setTextColor(android.graphics.Color.RED);
        } else {
            String marker2 = isC2Turn ? "▶ [ACTIVE] " : "";
            tvC2Stats.setText(marker2 + "Crew B: " + c2.getSpecialization() + "(" + c2.getName() + ")\n" + c2.getFormattedStats());
            tvC2Stats.setTextColor(isC2Turn ? android.graphics.Color.GREEN : android.graphics.Color.WHITE);
        }
    }
}