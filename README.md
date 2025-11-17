# Habit Master: RPG Habit & Goal Tracker

Habit Master is an Android application that gamifies productivity, turning everyday goals and habits into an engaging RPG (Role-Playing Game) experience. Users create and complete tasks to earn XP (Experience Points), unlock powerful Equipment, advance through Levels, and battle challenging Bosses. The core objective is to boost user motivation and consistency by integrating progress tracking with satisfying gaming and social elements.

# Technical Architecture

The application is built exclusively for the **Android** platform using the Java programming language. We utilize a strict Three-Layer Architecture to ensure clean separation of the Presentation (UI), Business Logic, and Data Management components.

For data persistence, we employ a hybrid approach: SQLite serves as the robust local database, while Firebase handles all online features, including user authentication, real-time chatting, and Guild data. General application settings are managed using SharedPreferences.

We integrate the built-in Android system for Notifications to handle reminders and social alerts. Key third-party libraries include ZXing QR Code Scanner for easy friend management, and MPAndroidChart for visualizing progress on the statistics screen.

## Application View & Core Features

Habit Quest presents itself as a digital character sheet and quest log, guiding the user through a continuous loop of task completion and progression:

### Character Sheet View (Profile)
The Profile Screen acts as the user's character sheet, prominently displaying their progress: **Level**, **Title**, **XP** progression bar, accumulated **Coins**, current **PP** (Power Points, which determine attack strength), and a visual representation of their equipped **Gear**. This centralized view grounds the user in the RPG experience.

### Quest Log View (Task Management)
The core loop starts with the **Task Calendar** and **List View**, where users create and manage daily **Habits** and one-time **Quests**. When creating a task, users assign it **Difficulty** and **Importance**, these values directly determine the **XP** reward upon completion. This system encourages tackling more challenging or impactful goals. Completing tasks rewards **XP**, which directly fuels the level progression.

### Leveling and Boss Fights
Once enough **XP** is gained, the user **Levels Up**, immediately leading to a challenging **Boss Fight**. The Boss's HP scales with the user's progression. The user has 5 attempts to defeat it, the damage per hit is based on their **PP**, and the chance of a successful hit is dynamically calculated based on their real-world **Task Completion Success Rate**. Defeating the Boss awards **Coins** and the chance to find powerful new **Equipment**.

### Inventory and Gear
The Equipment Screen allows users to manage their inventory of **Weapons**, **Clothing**, and **Potions**. These items grant temporary or permanent bonuses to stats like **PP** or attack success chance, requiring strategic management before each Boss Fight.

### Social and Guild Missions
The Guild Screen enables social interaction. Users can add friends via search or **QR Code** and form a **Guild**. Guilds can undertake a **Special Mission** to defeat a powerful joint Boss over a two-week period. Damage is inflicted collaboratively by completing specific Guild objectives. Successful missions award all members with unique rewards and **Badges**.
