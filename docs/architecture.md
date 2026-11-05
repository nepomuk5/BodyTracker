# Technische Übersicht — Random Life Stats

**Stand 05.11.2026.** Bewusst kleine native Android-App mit Kotlin und klassischer Android-View-UI (kein Compose-Framework, kein Backend).

## Komponenten

| Komponente | Verantwortung |
|---|---|
| `MainActivity.kt` | Vier Tabs: Heute, Verlauf, Tracker, Widget Studio; Event- und Backup-Dialoge; Pep Talk |
| `UiKit.kt` | Gemeinsame Views, Typografie, Karten, Buttons, Farben |
| `LifeDatabase.kt` | SQLiteOpenHelper, Tracker-/Event-Tabellen, CRUD, JSON-Backup |
| `LifeWidgetProvider.kt` | Homescreen-Tracker-Buttons, Widget-Resize, Event-Broadcasts |
| `WidgetPrefs` (gleiche Datei) | Pro Widget gespeicherte Auswahl, Reihenfolge, Labels, Zähler und Farbstil |
| `WidgetConfigActivity.kt` | Einstellungsansicht eines Tracker-Widgets |
| `Affirmations.kt` | Offline-Katalog lockerer positiver Sprüche |
| `AffirmationWidgetProvider.kt` | Unabhängiges Homescreen-Pep-Talk-Widget |
| `PixelIcons.kt` | Historischer Name für die Zuordnung **stabiler Icon-Keys** auf Drawable-Ressourcen |
| `app/src/main/res/` | Widget-Layouts, XML-Vektoren und Pixel-/Soft-Emoji-PNGs |

## Datenmodell

SQLite-Datei: `life_bits.db`, Schema-Version 2.

```text
trackers(id PK, name, icon, active, sort_order)
events(id PK, tracker_id FK → trackers.id, event_date, event_time NULL, recorded_at)
```

- Ein Ereignis gehört zu genau einem Tracker.
- `event_date` ist ein lokales ISO-Datum (YYYY-MM-DD).
- `event_time` ist optional; **beim One-Tap nie automatisch aus der Uhrzeit des Taps füllen**.
- `recorded_at` ist ein automatischer Millisekunden-Timestamp.
- Archivierte Tracker werden nicht physisch gelöscht. Historische Events bleiben sichtbar.
- Widget-Konfiguration liegt in lokalen SharedPreferences, getrennt von der Event-Datenbank.
- JSON-Backup umfasst Tracker und Events; **Widget-Konfigurationen gehören aktuell nicht zum JSON-Export**. Import ersetzt lokale Tracker/Events nach Validierung in einer Transaktion. Daher vor Import Sicherung erstellen.

## Android-Widget-Eigenheiten

- Homescreen-Launcher bestimmen tatsächlich verfügbare Zellgrößen und können Widgets nach einem Update verzögert neu layouten.
- Der kompakte 4×1-Tracker-Widget-Typ wird in `life_widget_info.xml` als Standard angefordert, bleibt in der Größe änderbar; der Provider wählt nach Widget-Optionen Layout/Spalten.
- Tracker-Widgets speichern ohne App-Start über Broadcast-Receiver; keine Bestätigung, Event-Zeit bleibt NULL.
- Beim Test von geänderten Widget-Mindestgrößen ggf. bestehende Widgets entfernen und neu platzieren (Eventdaten bleiben in SQLite).
- Affirmations-Widget ist ein anderer Receiver; Sprüche werden lokal ausgewählt und beim Tippen gewechselt.

## Build und Sicherheit

- `app/build.gradle.kts`: compileSdk 35, minSdk 26, targetSdk 34, Java/Kotlin 17.
- Gradle-Wrapper 8.11.1 und GitHub Actions `.github/workflows/android-debug.yml` bauen eine Debug-APK.
- Keine Accounts, Analytics, Server-APIs oder Netzwerkanforderungen für Grundfunktionen.
- APK-Signatur: Standard-Debug-Signatur beim lokalen Build. Für ein späteres Release neue, kontrolliert verwahrte Release-Signatur erforderlich.
- Keine privaten DB-, Export-, Log- oder Gerätedaten ins Repository committen.

## Aktuelle technische Schulden

- Keine reproduzierbare automatisierte App-/Widget-UI-Testsuite; manuelle Emulator-Tests sind bisher entscheidend.
- Noch keine Ende-zu-Ende-Prüfung eines Restore auf echtem Gerät.
- Zusätzliche Custom-Tracker-Icons stammen noch aus einem älteren Pixelset.
- Keine Migration oder Neudesign des Datenmodells ohne expliziten Test von Altbeständen.
