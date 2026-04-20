# Space Colony - Android Management Game

## 1. General Project Description
Space Colony is an Android application where the player manages a crew of space specialists. The objective is to recruit crew members, train them in the simulator to gain experience, and deploy them on cooperative turn-based missions against system-generated threats. The application focuses on resource management, tactical combat choices, and managing crew recovery times in the Medbay.

**GitHub Repository:** [INSERT YOUR GITHUB LINK HERE]
**Project Video Demonstration:** [INSERT YOUR YOUTUBE/DRIVE LINK HERE]

---

## 2. Team Composition & Division of Work
* **[YOUR NAME]**: 100% Contribution. Handled all UI design, object-oriented logic, Activity routing, data persistence via SharedPreferences/Gson, and the turn-based combat engine. 
*(Note: If you had partners, list them here and split the tasks up!)*

---

## 3. Application Use-Flow
1. **Launch App:** The user starts at the Main Menu (Colony Overview), displaying current crew distribution and total mission statistics.
2. **Recruitment:** The user navigates to the Recruit screen to hire new crew members by entering a name and selecting a specialization (Pilot, Engineer, Medic, Scientist, Soldier). The UI provides a live preview of the character's icon and base stats.
3. **Quarters:** Recruits start in Quarters. From here, the user can select healthy crew members to move them to either the Simulator or Mission Control.
4. **Simulator (Training):** Users can train crew members. Training awards Experience Points (XP). Each point of XP permanently increases the crew member's Skill power for future missions.
5. **Mission Control (Combat):** The user selects exactly two healthy crew members to face a system-generated threat. 
   * Combat is turn-based. 
   * The user chooses between "Attack" (deals damage based on Skill + XP + Random Dice Roll) or "Tactical Defend" (Heals 2 Energy and boosts Resilience).
   * The threat retaliates after every crew action.
6. **Post-Mission:** * **Victory:** Threat is defeated. Surviving crew gain XP and return to Quarters.
   * **Defeat:** If a crew member's HP hits zero, they are evacuated to the Medbay. They lose their accumulated XP and receive a 2-mission "Time Out" penalty before they can be deployed again.
7. **Saving/Loading:** The user manually saves their roster and mission statistics from the Main Menu.

---

## 4. Implemented Features
### Mandatory Features:
* **Object-Oriented Paradigm:** Utilized Encapsulation, Inheritance, Polymorphism, and Abstraction.
* **Android Compatibility:** Fully developed in Java using Android Studio.
* **Basic Functionality:** Full recruitment, training, mission execution, and quarters recovery loops are functional.

### Bonus Features Implemented:
1. **RecyclerView (+1):** Used a custom `CrewAdapter` to efficiently display crew lists across multiple UI screens.
2. **Crew Images (+1):** Specializations are visualized with unique icons in the RecyclerView and a dynamic live-preview in the Recruitment screen.
3. **Tactical Combat (+2):** Missions are not automatic. The player actively chooses between attacking or using a "Tactical Defend/Heal" action each turn.
4. **No Death / Medbay (+1):** Defeated crew members are not permanently deleted. They are evacuated to the Medbay, stripped of XP, and put on a 2-mission recovery timer.
5. **Randomness in Missions (+1):** Implemented `Math.random()` to add variable dice-roll damage (0-3 bonus damage) to both crew and threat attacks.
6. **Data Storage & Loading (+2):** Implemented a manual save/load system using `Gson` and `SharedPreferences` to persist the complex `HashMap` of the crew roster and mission statistics.

---

## 5. Tools Used
* **IDE:** Android Studio
* **Language:** Java, XML
* **Libraries:** Gson (for JSON serialization of object data)
* **Version Control:** Git & GitHub

---

## 6. Installation Instructions
1. Clone the repository: `git clone [YOUR GITHUB LINK]`
2. Open the project in Android Studio.
3. Sync project with Gradle Files.
4. Build and run the application on an Android Emulator (API 24+) or a physical Android device.

---

## 7. UML Class Diagram
*(Note: UI Classes such as Activities and Adapters have been excluded as per project instructions.)*

```mermaid
classDiagram
    class CrewBattleAction {
        <<enumeration>>
        ATTACK
        DEFEND
    }

    class CrewDatabase {
        -CrewDatabase instance$
        -HashMap~Integer, CrewMember~ roster
        -int totalMissionsCompleted
        -int totalMissionsLost
        -CrewDatabase()
        +getInstance() : CrewDatabase$
        +addLostMission() : void
        +getLostMissions() : int
        +hireCrew(cm: CrewMember) : void
        +dismissCrew(id: int) : void
        +getCrewList() : List~CrewMember~
        +getCrewByLocation(loc: String) : List~CrewMember~
        +addCompletedMission() : void
        +getTotalMissions() : int
        +processMedbayRecovery() : void
        +saveToFile(context: Context) : void
        +loadFromFile(context: Context) : void
    }

    class CrewMember {
        #int id
        #String name
        #String specialization
        #int skill
        #int resilience
        #int experience
        #int energy
        #int maxEnergy
        #String location
        -int missionsParticipated
        -int missionsWon
        -int trainingSessions
        +boolean isSelectedForUI
        -int idCounter$
        -int recoveryTime
        +CrewMember()
        +CrewMember(name: String, specialization: String, skill: int, resilience: int, maxEnergy: int)
        +act() : int
        +defend(damage: int) : void
        +restoreEnergy() : void
        +train() : void
        +addMission() : void
        +addVictory() : void
        +getLocation() : String
        +setLocation(loc: String) : void
        +setEnergy(energy: int) : void
        +getId() : int
        +getName() : String
        +getSpecialization() : String
        +getEnergy() : int
        +getMaxEnergy() : int
        +getSkill() : int
        +getExperience() : int
        +getResilience() : int
        +getRecoveryTime() : int
        +setRecoveryTime(recoveryTime: int) : void
        +setExperience(experience: int) : void
        +getFormattedStats() : String
        +updateIdCounter(highestIdLoaded: int) : void$
    }

    class Threat {
        -String name
        -int skill
        -int resilience
        -int energy
        -int maxEnergy
        +Threat(name: String, completedMissions: int)
        +act() : int
        +defend(incomingDamage: int) : void
        +isDefeated() : boolean
        +getName() : String
        +getEnergy() : int
        +getMaxEnergy() : int
        +getSkill() : int
        +getResilience() : int
        +setEnergy(energy: int) : void
    }

    class BattleManager {
        -CrewMember crewA
        -CrewMember crewB
        -Threat threat
        -boolean isCrewATurn
        -int roundCounter
        -StringBuilder battleLog
        +BattleManager(a: CrewMember, b: CrewMember, t: Threat)
        +executeTurn(action: CrewBattleAction) : String
        -awardVictory() : void
        +isMissionOver() : boolean
        +getLog() : String
        +isCrewATurn() : boolean
    }

    class Pilot {
        +Pilot(name: String)
    }
    class Engineer {
        +Engineer(name: String)
    }
    class Medic {
        +Medic(name: String)
    }
    class Scientist {
        +Scientist(name: String)
    }
    class Soldier {
        +Soldier(name: String)
    }

    CrewDatabase "1" *-- "many" CrewMember : stores
    BattleManager o-- "2" CrewMember : uses
    BattleManager o-- "1" Threat : uses
    BattleManager ..> CrewBattleAction : uses
    CrewMember <|-- Pilot
    CrewMember <|-- Engineer
    CrewMember <|-- Medic
    CrewMember <|-- Scientist
    CrewMember <|-- Soldier
