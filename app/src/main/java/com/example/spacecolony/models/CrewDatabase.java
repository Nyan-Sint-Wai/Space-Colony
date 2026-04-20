package com.example.spacecolony.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrewDatabase {
    private static CrewDatabase instance;
    private HashMap<Integer, CrewMember> roster;
    private int totalMissionsCompleted = 0;
    private int totalMissionsLost = 0;

    private CrewDatabase() {
        roster = new HashMap<>();
    }

    public static CrewDatabase getInstance() {
        if (instance == null) {
            instance = new CrewDatabase();
        }
        return instance;
    }

    // Tracker for Failed Missions
    public void addLostMission() { totalMissionsLost++; }
    public int getLostMissions() { return totalMissionsLost; }

    // Basic CRUD Operations
    public void hireCrew(CrewMember cm) {
        roster.put(cm.getId(), cm);
    }

    // Handles the strict Permadeath rule
    public void dismissCrew(int id) {
        roster.remove(id);
    }

    public List<CrewMember> getCrewList() {
        return new ArrayList<>(roster.values());
    }

    // Replaces the need for 3 separate HashMaps!
    public List<CrewMember> getCrewByLocation(String loc) {
        List<CrewMember> filtered = new ArrayList<>();
        for (CrewMember cm : roster.values()) {
            if (cm.getLocation().equals(loc)) {
                filtered.add(cm);
            }
        }
        return filtered;
    }

    // Threat Scaling Trackers
    public void addCompletedMission() { totalMissionsCompleted++; }
    public int getTotalMissions() { return totalMissionsCompleted; }

    // AUTOMATIC MEDBAY RECOVERY LOGIC
    public void processMedbayRecovery() {
        for (CrewMember cm : roster.values()) {
            if (cm.getLocation().equals("Medbay")) {
                cm.setRecoveryTime(cm.getRecoveryTime() - 1); // Tick down the clock

                if (cm.getRecoveryTime() <= 0) {
                    cm.setLocation("Quarters");
                    cm.restoreEnergy();
                }
            }
        }
    }

    // DATA STORAGE LOGIC
    public void saveToFile(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SpaceColonyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String jsonRoster = gson.toJson(roster);

        editor.putString("RosterJSON", jsonRoster);
        editor.putInt("MissionsWon", totalMissionsCompleted);
        editor.putInt("MissionsLost", totalMissionsLost); // Save lost missions!
        editor.apply();
    }

    public void loadFromFile(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SpaceColonyData", Context.MODE_PRIVATE);
        String jsonRoster = prefs.getString("RosterJSON", null);

        if (jsonRoster != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<Integer, CrewMember>>(){}.getType();

            // 1. Load data into a temporary map
            HashMap<Integer, CrewMember> loadedRoster = gson.fromJson(jsonRoster, type);

            // 2. Forcefully clear the current memory and paste the exact saved data in
            if (loadedRoster != null) {
                roster.clear();
                roster.putAll(loadedRoster);

                // 3. Update the ID Counter to safely protect your veterans
                int maxId = 0;
                for (CrewMember cm : roster.values()) {
                    if (cm.getId() > maxId) {
                        maxId = cm.getId();
                    }
                }
                CrewMember.updateIdCounter(maxId);
            }
        }

        totalMissionsCompleted = prefs.getInt("MissionsWon", 0);
        totalMissionsLost = prefs.getInt("MissionsLost", 0); // Load lost missions!
    }
}