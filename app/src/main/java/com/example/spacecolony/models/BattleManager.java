package com.example.spacecolony.models;

public class BattleManager {
    private CrewMember crewA;
    private CrewMember crewB;
    private Threat threat;
    private boolean isCrewATurn;
    private int roundCounter = 1;
    private StringBuilder battleLog;

    public BattleManager(CrewMember a, CrewMember b, Threat t) {
        this.crewA = a;
        this.crewB = b;
        this.threat = t;
        this.isCrewATurn = true;
        this.battleLog = new StringBuilder();

        this.crewA.addMission();
        this.crewB.addMission();

        battleLog.append("=== MISSION: Deep Space Operation ===\n");
        battleLog.append("--- Round ").append(roundCounter).append(" ---\n");
    }

    public String executeTurn(CrewBattleAction action) {
        if (isMissionOver()) return battleLog.toString();

        CrewMember activeCrew = isCrewATurn ? crewA : crewB;

        // Skip KIA members
        if (activeCrew.getEnergy() <= 0) {
            isCrewATurn = !isCrewATurn;
            return executeTurn(action);
        }

        // 1. CREW ACTION PHASE
        if (action == CrewBattleAction.ATTACK) {
            battleLog.append(activeCrew.getSpecialization()).append("(").append(activeCrew.getName()).append(") attacks ").append(threat.getName()).append("\n");

            int baseSkill = activeCrew.getSkill() + activeCrew.getExperience();
            int diceRoll = (int)(Math.random() * 4); // Rolls a 0, 1, 2, or 3
            int totalAttack = baseSkill + diceRoll;
            int damageToThreat = Math.max(0, totalAttack - threat.getResilience());

            // Apply damage directly
            threat.setEnergy(Math.max(0, threat.getEnergy() - damageToThreat));

            if (diceRoll >= 2) {
                battleLog.append(">>> CRITICAL HIT! (+").append(diceRoll).append(" Damage Roll) <<<\n");
            }

            battleLog.append("Damage dealt: (Skill:").append(baseSkill).append(" + Roll:").append(diceRoll)
                    .append(") - Res:").append(threat.getResilience()).append(" = ").append(damageToThreat).append("\n");

        } else {
            // TACTICAL DEFEND: Heals up to 2 HP without exceeding max energy!
            int healAmount = 2;
            activeCrew.setEnergy(Math.min(activeCrew.getMaxEnergy(), activeCrew.getEnergy() + healAmount));
            battleLog.append("> ").append(activeCrew.getName()).append(" takes evasive maneuvers!\n");
            battleLog.append(">>> TACTICAL HEAL (+3 Resilience, Restored ").append(healAmount).append(" Energy) <<<\n");
        }

        battleLog.append(threat.getName()).append(" energy: ").append(threat.getEnergy()).append("/").append(threat.getMaxEnergy()).append("\n\n");

        // 2. Check Victory Early
        if (threat.isDefeated()) {
            battleLog.append("=== MISSION COMPLETE ===\n");
            battleLog.append("The ").append(threat.getName()).append(" has been neutralized!\n");
            awardVictory();

            // Advance Medbay timers for recovering crew!
            CrewDatabase.getInstance().processMedbayRecovery();

            return battleLog.toString();
        }

        // 3. THREAT RETALIATION PHASE
        int defendBonus = (action == CrewBattleAction.DEFEND) ? 3 : 0;
        int threatDice = (int)(Math.random() * 4); // Threat also rolls a 0-3
        int threatTotalSkill = threat.getSkill() + threatDice;
        int crewRes = activeCrew.getResilience() + defendBonus;

        int damageToCrew = Math.max(0, threatTotalSkill - crewRes);

        battleLog.append(threat.getName()).append(" retaliates against ").append(activeCrew.getName()).append("\n");

        if (threatDice >= 2) {
            battleLog.append(">>> DANGER: Threat surged! (+").append(threatDice).append(" Damage Roll) <<<\n");
        }

        activeCrew.setEnergy(activeCrew.getEnergy() - damageToCrew);

        battleLog.append("Damage taken: (Skill:").append(threat.getSkill()).append(" + Roll:").append(threatDice)
                .append(") - Res:").append(crewRes).append(" = ").append(damageToCrew).append("\n");

        battleLog.append(activeCrew.getSpecialization()).append("(").append(activeCrew.getName()).append(") energy: ")
                .append(Math.max(0, activeCrew.getEnergy())).append("/").append(activeCrew.getMaxEnergy()).append("\n\n");


        // 4. CLEANUP & ROUND MANAGEMENT
        if (activeCrew.getEnergy() <= 0) {
            // THE MERCY RULE: Evacuate instead of Delete!
            battleLog.append("!!! CRITICAL: ").append(activeCrew.getName()).append(" incapacitated! Evacuating to Medbay... !!!\n");

            activeCrew.setLocation("Medbay");
            activeCrew.setExperience(0); // Penalty 1: Wipe experience!
            activeCrew.setRecoveryTime(2); // Penalty 2: Set Time-Out to 2 Missions!
        }

        if (isMissionOver() && !threat.isDefeated()) {
            battleLog.append("=== MISSION FAILED ===\nAll crew members evacuated. Mission Lost.\n");

            CrewDatabase.getInstance().addLostMission();
            CrewDatabase.getInstance().processMedbayRecovery();

        } else {
            if (!isCrewATurn) {
                roundCounter++;
                battleLog.append("--- Round ").append(roundCounter).append(" ---\n");
            }
            isCrewATurn = !isCrewATurn;
        }

        return battleLog.toString();
    }

    private void awardVictory() {
        if (crewA.getEnergy() > 0) {
            // NEW: Give raw XP and count the victory, without triggering a "training session"
            crewA.setExperience(crewA.getExperience() + 1);
            crewA.addVictory();
            battleLog.append(crewA.getSpecialization()).append("(").append(crewA.getName())
                    .append(") gains 1 experience point. (exp: ").append(crewA.getExperience()).append(")\n");
        }
        if (crewB.getEnergy() > 0) {
            crewB.setExperience(crewB.getExperience() + 1);
            crewB.addVictory();
            battleLog.append(crewB.getSpecialization()).append("(").append(crewB.getName())
                    .append(") gains 1 experience point. (exp: ").append(crewB.getExperience()).append(")\n");
        }
        CrewDatabase.getInstance().addCompletedMission();
    }

    public boolean isMissionOver() {
        return threat.isDefeated() || (crewA.getEnergy() <= 0 && crewB.getEnergy() <= 0);
    }

    public String getLog() { return battleLog.toString(); }

    public boolean isCrewATurn() { return isCrewATurn; }
}