package com.example.spacecolony.models;

public class CrewMember {
    protected int id;
    protected String name;
    protected String specialization;
    protected int skill;
    protected int resilience;
    protected int experience;
    protected int energy;
    protected int maxEnergy;
    protected String location;

    // --- NEW: INDIVIDUAL STATISTICS TRACKING ---
    private int missionsParticipated = 0;
    private int missionsWon = 0;
    private int trainingSessions = 0;

    public transient boolean isSelectedForUI = false;
    private static int idCounter = 1;
    private int recoveryTime = 0;

    public CrewMember() {}

    public CrewMember(String name, String specialization, int skill, int resilience, int maxEnergy) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
        this.location = "Quarters";
    }

    public int act() { return skill; }
    public void defend(int damage) { this.energy -= damage; }
    public void restoreEnergy() { this.energy = this.maxEnergy; }

    // Track how many times they used the simulator
    public void train() {
        this.experience++;
        this.trainingSessions++;
    }

    // New Tracking Methods
    public void addMission() { this.missionsParticipated++; }
    public void addVictory() { this.missionsWon++; }

    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String loc) { this.location = loc; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getSkill() { return skill; }
    public int getExperience() { return experience; }
    public int getResilience() { return resilience; }
    public int getRecoveryTime() { return recoveryTime; }
    public void setRecoveryTime(int recoveryTime) { this.recoveryTime = recoveryTime; }
    public void setExperience(int experience) { this.experience = experience; }

    // Shows their career stats on a new line!
    public String getFormattedStats() {
        String skillDisplay = (experience > 0) ? skill + " (+" + experience + ")" : String.valueOf(skill);

        return String.format("skill: %s; res: %d; exp: %d; energy: %d/%d\nCareer -> Missions: %d | Wins: %d | Trained: %d",
                skillDisplay, getResilience(), getExperience(),
                Math.max(0, getEnergy()), getMaxEnergy(),
                missionsParticipated, missionsWon, trainingSessions);
    }

    public static void updateIdCounter(int highestIdLoaded) {
        if (highestIdLoaded >= idCounter) {
            idCounter = highestIdLoaded + 1;
        }
    }
}