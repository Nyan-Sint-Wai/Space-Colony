package com.example.spacecolony.models;

public class Threat {
    private String name;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;

    // The PDF Scaling Formula: threat skill = 4 + number of completed missions
    public Threat(String name, int completedMissions) {
        this.name = name;
        this.skill = 4 + completedMissions;

        // We scale resilience and energy slightly so the game stays challenging
        this.resilience = 2 + (completedMissions / 2);
        this.maxEnergy = 25 + (completedMissions * 5);
        this.energy = this.maxEnergy;
    }

    public int act() {
        return this.skill;
    }

    public void defend(int incomingDamage) {
        // Damage is reduced by resilience, minimum of 0
        int actualDamage = Math.max(0, incomingDamage - this.resilience);
        this.energy -= actualDamage;
        if (this.energy < 0) {
            this.energy = 0;
        }
    }

    public boolean isDefeated() {
        return this.energy <= 0;
    }

    // Standard Getters
    public String getName() { return name; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public void setEnergy(int energy) {
        this.energy = energy;
    }
}