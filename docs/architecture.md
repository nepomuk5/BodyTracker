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
- JSON-Backup umfasst Tracker und Events; **Widget-Konfigurationen gehören aktuell nicht zum JSON-Export**. In v0.4.2 validiert die App die gesamte JSON-Datei vor dem Bestätigungsdialog, zeigt Tracker-/Eventanzahlen und führt einen bestätigten Import nach erneuter Prüfung in einer SQL-Transaktion aus. Der Export wird durch erneutes Einlesen geprüft. Ein fehlgeschlagener Import darf vorhandene Daten nicht verändern.

## Statistiken (v0.4.0-dev)

- Der Verlauf besitzt eine umschaltbare Monats-/Jahresansicht.
- `LifeDatabase` aggregiert direkt per SQLite über lokale ISO-Ereignistage (kein Netz, kein Data Warehouse).
- Monats-/Jahresansicht zählt Ereignisse und unterschiedliche Tage, einschließlich archivierter Tracker mit historischen Daten. `event_time` wird nicht verwendet.
- Erst nach Emulator- und Samsung-Test als fachlich abgenommen markieren.

## Android-Widget-Eigenheiten

- Homescreen-Launcher bestimmen tatsächlich verfügbare Zellgrößen und können Widgets nach einem Update verzögert neu layouten.
- Der kompakte 4×1-Tracker-Widget-Typ wird in `life_widget_info.xml` als Standard angefordert, bleibt in der Größe änderbar; der Provider wählt nach Widget-Optionen Layout/Spalten.
- Tracker-Widgets speichern ohne App-Start über Broadcast-Receiver; keine Bestätigung, Event-Zeit bleibt NULL.
- Beim Test von geänderten Widget-Mindestgrößen ggf. bestehende Widgets entfernen und neu platzieren (Eventdaten bleiben in SQLite).
- Affirmations-Widget ist ein anderer Receiver; Sprüche werden lokal ausgewählt und beim Tippen gewechselt.
- Für Samsung One UI ist im v0.4.0-Branch eine 3×1-Standardgröße und ein **eigenes kompaktes Textlayout** für einzeilige Launcher-Zellen vorgesehen. Größere Widgets behalten Titel, Spruch und Hinweis.

## Build und Sicherheit

- `app/build.gradle.kts`: compileSdk 35, minSdk 26, targetSdk 34, Java/Kotlin 17.
- Gradle-Wrapper 8.11.1 und GitHub Actions `.github/workflows/android-debug.yml` bauen eine Debug-APK.
- Keine Accounts, Analytics, Server-APIs oder Netzwerkanforderungen für Grundfunktionen.
- APK-Signatur: Standard-Debug-Signatur beim lokalen Build. Für ein späteres Release neue, kontrolliert verwahrte Release-Signatur erforderlich.
- Keine privaten DB-, Export-, Log- oder Gerätedaten ins Repository committen.

## Aktuelle technische Schulden

- v0.4.2 bringt isolierte instrumentierte Datenbank-/Backup-Tests; vollständige App-/Widget-UI-End-to-End-Tests bleiben offen. GitHub Actions **kompiliert** diese Tests, führt sie aber mangels Emulator nicht aus.
- Noch keine Ende-zu-Ende-Prüfung eines Restore auf echtem Gerät.
- Die 20 Soft Emoji Cutie-Icons sind implementiert und die Auswahl wurde auf Samsung bestätigt.
- Keine Migration oder Neudesign des Datenmodells ohne expliziten Test von Altbeständen.

## Backup-QA (v0.4.2-dev)

- `BackupSummary` validiert Format/Version, IDs, Referenzen, Datum/Uhrzeit und erforderliche Felder ohne Datenbankänderung.
- Maximal 15 MB beim Einlesen über das Android-Dokumenten-API; untrusted Dateien nicht unbegrenzt als `readText()` laden.
- `BackupRoundTripTest` nutzt eine **eigene Test-SQLite-Datei**, nicht die Produktionsdatenbank.
- Android-Backups für die App sind im Manifest ausgeschaltet (`allowBackup=false`). Daher muss ein verifizierter manueller JSON-Export außerhalb der App aufbewahrt werden.
- Detaillierte sichere Samsung- und Emulator-Schritte: `docs/BACKUP_TESTS.md`.
