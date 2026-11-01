# 🎀 Random Life Stats / BodyTracker

Native Android-App für **One-Tap-Tracking** persönlicher, seltener Alltagsereignisse. Ziel: diskrete, individuelle Android-Homescreen-Widgets und eine rosa/pastellige **Modern Pixel Cutie**-Oberfläche.

**Status:** Projekt- und Emulator-Setup ist in diesem Branch vorbereitet; die bestehenden v0.2-Android-Quelldateien/Pixel-Assets müssen noch vollständig importiert werden. **Noch kein bestätigter Android-Build.**

## Einstieg am Windows-PC

1. Android Studio von https://developer.android.com/studio installieren.
2. Den GitHub-Branch `chore/android-project-emulator-setup` lokal klonen/auschecken.
3. Vorhandenes vollständiges v0.2-Projekt aus dem vorbereiteten Quellcodearchiv in die Repository-Wurzel übernehmen (ohne `.git/` zu löschen). Die Quelldateien sollten `app/`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties` und `app/src/main/res/` enthalten.
4. Repository-Hauptordner mit Android Studio öffnen, Gradle Sync ausführen und `app` starten.
5. Virtuelles Pixel-Smartphone im Device Manager anlegen; Emulator starten, App mit ▶ ausführen und Widget auf den virtuellen Homescreen ziehen.

Eine detaillierte bebilderungsunabhängige Anleitung findest du unter **[docs/EMULATOR.md](docs/EMULATOR.md)**. Die Produktanforderungen sind in **[docs/requirements.md](docs/requirements.md)**, Codex-Regeln in **[AGENTS.md](AGENTS.md)** und die nächsten Schritte in **[docs/backlog.md](docs/backlog.md)**.

## Erste Testfälle

- Sieben Start-Tracker: Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol.
- Jeder Tap erzeugt genau einen Eintrag für den Ereignistag (standardmäßig heute).
- **`event_time` bleibt NULL**, während `recorded_at` separat gesetzt wird.
- Zwei konfigurierbare Widgets dürfen sich unterscheiden; diskreter Icon-only-Modus.
- Nach Emulator-Neustart bleiben Einträge erhalten.

GitHub Actions (`.github/workflows/android-debug.yml`) erstellt nach vollständigem Import und erfolgreichem Android-Compile eine Debug-APK. Bis dahin kann der Workflow beim fehlenden App-Code bewusst fehlschlagen.

**Privatsphäre:** Keine echten Gesundheits-/Trackingdaten, Datenbankdateien oder privaten Backups ins GitHub-Repository committen.
