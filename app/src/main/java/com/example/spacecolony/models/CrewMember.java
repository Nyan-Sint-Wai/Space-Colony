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
    protected String location; // Tracks: "Quarters", "Simulator", or "Mission Control"

    // UI Helper - Transient means Gson won't save this to the file!
    public transient boolean isSelectedForUI = false;

    private static int idCounter = 1;

    // A blank constructor is required for Gson Data Loading later
    public CrewMember() {}

    public CrewMember(String name, String specialization, int skill, int resilience, int maxEnergy) {
        this.id = idCounter++;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy; // Starts at maximum energy
        this.experience = 0;     // Starts at zero experience
        this.location = "Quarters"; // Newly recruited crew are placed in Quarters
    }

    // PDF Rule: Mission performance (attack power) is Skill + Experience
    public int act() {
        return skill; // The BattleManager will handle the subtraction logic
    }

    // From your PDF: Reduces energy based on incoming damage
    public void defend(int damage) {
        this.energy -= damage;
    }

    // PDF Rule: Fully restores energy (called when returning to Quarters)
    public void restoreEnergy() {
        this.energy = this.maxEnergy;
    }

    // PDF Rule: Training grants experience points
    public void train() {
        this.experience++;
    }

    // Standard Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String loc) { this.location = loc; }
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getSkill() { return skill; }
    public int getExperience() { return experience; }
    public int getResilience() { return resilience; }
    // 1. Add this variable at the top with your other private variables
    private int recoveryTime = 0;

    // 2. Add these methods near the bottom
    public int getRecoveryTime() { return recoveryTime; }
    public void setRecoveryTime(int recoveryTime) { this.recoveryTime = recoveryTime; }
    public void setExperience(int experience) { this.experience = experience; }

    public String getFormattedStats() {
        // If they have experience, show it as a bonus! e.g., "skill: 5 (+2)"
        String skillDisplay = (experience > 0) ? skill + " (+" + experience + ")" : String.valueOf(skill);

        return String.format("skill: %s; res: %d; exp: %d; energy: %d/%d",
                skillDisplay, getResilience(), getExperience(),
                Math.max(0, getEnergy()), getMaxEnergy());
    }

    // Add this to prevent new recruits from stealing loaded IDs!
    public static void updateIdCounter(int highestIdLoaded) {
        if (highestIdLoaded >= idCounter) {
            idCounter = highestIdLoaded + 1;
        }
    }
}