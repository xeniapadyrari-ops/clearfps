# clearfps (Fabric, Minecraft 26.2)

Menu: `;` (rebind in Controls -> clearfps). Left click = toggle, right click = option.

Features
- HUD: watermark, FPS / ping / XYZ / Nether<->Overworld coords / facing, nearby-player radar, animated module list
- Triggerbot (right click: players only / all)
- Invis Vision (renders invisible players/mobs)
- Freecam (WASD, Space, Shift, Ctrl = fast, right click = speed)
- Fullbright, Auto Sprint, Auto Totem, Auto Respawn, Anti AFK

Build
1. Install JDK 25.
2. Generate a Gradle wrapper (easiest: create a template at https://fabricmc.net/develop/template/ for 26.2,
   copy its `gradlew`, `gradlew.bat` and `gradle/` folder here) or run `gradle wrapper` with Gradle 9.x installed.
3. `./gradlew build`  ->  `build/libs/clearfps-1.0.0+26.2.jar`  (ignore the *-sources.jar)
4. Put the jar into `.minecraft/mods` together with Fabric API 26.2.

If versions in gradle.properties are too old, take the newest loader / Fabric API for 26.2 from
https://fabricmc.net/develop/ and update them.

Build without installing anything (GitHub Actions)
1. Create a new GitHub repository and upload ALL files of this folder (including the hidden `.github` folder).
2. Open the Actions tab -> `build-jar` -> wait until it is green.
3. Open the finished run -> Artifacts -> `clearfps-jar` -> download the zip -> inside is `clearfps-1.0.0+26.2.jar`.
