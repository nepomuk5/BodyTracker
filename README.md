# 🎀 Random Life Stats (BodyTracker)

**Native Android-App für One-Tap-Tracking von seltenen kleinen Alltagsereignissen.** Offline, ohne Account und ohne Cloud-Synchronisierung. Eigene Homescreen-Widgets machen das Erfassen mit nur einem Tipp möglich.

**Projektstatus (05.11.2026):** Android-Prototyp **v0.3.1-dev** erfolgreich gebaut und auf einem Android-Emulator ausprobiert. Tracker-Widget und Daily-Pep-Talk-Widget funktionieren im Emulator. **Ein Test auf einem echten Android-Gerät und ein Release sind noch offen.** Die App wird bislang als Debug-APK installiert; noch keine Play-Store-Veröffentlichung.

## Features im aktuellen Prototyp

- Sieben Start-Tracker: **Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol**.
- Ein Tap = ein Ereignis. Mehrere Ereignisse am selben Tag sind erlaubt.
- `event_date` ist standardmäßig der heutige Tag; `event_time` bleibt **NULL/unbekannt**; `recorded_at` speichert den separaten Erfassungszeitpunkt.
- Verlauf mit Kalender, Bearbeitung, Nachtrag, Löschen und schnellem Undo in der App.
- Mehrere unabhängig konfigurierbare Tracker-Widgets (Buttons, Reihenfolge, Beschriftung, Zähler, Farbe); kompakte 4×1- und 2×2-Layouts.
- Lokaler JSON-Export und -Import; Wiederherstellung ist noch vertieft zu testen.
- Zufälliger **Daily Pep Talk** in der App sowie ein eigenes optionales Affirmations-Widget.
- **Soft Emoji Cutie** für die sieben Standardtracker; die **zusätzliche Icon-Auswahl für eigene Tracker ist noch nicht überarbeitet**.

## Einstieg (Windows + Android Studio)

Android Studio mit Android SDK/Emulator verwenden. Repository lokal öffnen und im Root-Verzeichnis:

```powershell
.\gradlew.bat :app:assembleDebug
```

Die Debug-APK liegt anschließend unter `app/build/outputs/apk/debug/app-debug.apk`.

Mit gestartetem Emulator **oder angeschlossenem Android-Gerät mit USB-Debugging** installieren:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r ".\app\build\outputs\apk\debug\app-debug.apk"
```

`install -r` aktualisiert die installierte Debug-App bei gleicher Signatur, ohne die App-Daten absichtlich zu löschen. **Nicht deinstallieren**, wenn die lokalen Testdaten bleiben sollen. Vor Experimenten ein JSON-Backup exportieren.

## Projektübersicht

| Pfad | Inhalt |
|---|---|
| `app/` | Kotlin-Quellcode, SQLite, Views, Android-Widgets, Icons und AndroidManifest |
| `design/` | Designreferenzen und Icon-Quelldateien |
| `docs/requirements.md` | **Verbindliche Produktspezifikation** mit Status |
| `docs/architecture.md` | Architektur, Persistenz und wichtige technische Grenzen |
| `docs/backlog.md` | Prioritäten, offene Entscheidungen und GitHub-Issues |
| `docs/EMULATOR.md` | Emulator- und Geräte-Testablauf |
| `docs/DECISIONS.md` | Getroffene Produkt- und Designentscheidungen |
| `docs/ux-test-01.md` | Historische Think-Aloud-Testauswertung |
| `AGENTS.md` | Verbindliche Entwicklungsregeln für Coding-Agents |
| `.github/workflows/android-debug.yml` | Automatischer Debug-APK-Build auf GitHub |

**Arbeitsweise:** `main` ist der Integrationsstand. Neue Änderungen in separaten Feature-Branches mit Pull Request; dokumentierte Anforderungen und Build-/Teststatus müssen aktualisiert werden.

**Datenschutz:** Keine echten Trackingdaten, Sicherungsdateien, Datenbanken, lokale SDK-Pfade, Schlüssel oder privaten Daten in GitHub committen.
