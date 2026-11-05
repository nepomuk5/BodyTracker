# Produktspezifikation — Random Life Stats

**Stand:** 05.11.2026 · **Prototyp:** 0.4.0-dev (auf main; Widget und Statistik-Grundansicht auf Samsung erprobt) · **Zielplattform:** Android (native Kotlin-App, minSdk 26)

Dieses Dokument ist die **verbindliche fachliche Spezifikation**. Der Status unterscheidet Implementierung von tatsächlicher Abnahme. Ein erfolgreicher CI-Build ersetzt keinen Widget-/Gerätetest.

## Produktziel

Unkompliziert und diskret erfassen, **an welchem Tag und wie häufig** kleine, ungewöhnliche Körper- und Alltagsereignisse passiert sind. Der Primärweg ist ein Homescreen-Widget ohne App-Start. Kein Habit-Score, keine Streak-Pflicht und keine medizinischen Empfehlungen.

## Verbindliche Regeln

- **Ein Tap erzeugt genau einen Datensatz**, auch bei mehreren Taps am selben Tag.
- **`event_date`:** tatsächlicher Ereignistag; bei Soforterfassung lokales Heute, nachträglich editierbar.
- **`event_time`:** tatsächliche Uhrzeit, nach einem Tap **immer NULL**, bis die Nutzerin explizit eine Zeit einträgt.
- **`recorded_at`:** separater automatisch erfasster Zeitstempel; normale Bearbeitung ändert ihn nicht.
- **Keine rückwirkende Ableitung** von `event_time` aus `recorded_at`. Keine Ereigniserfassung in der Zukunft.
- Persönliche Daten bleiben lokal; Tracker-Archivierung und App-Updates dürfen Ereignisse nicht löschen.

## Anforderungen & Abnahme

| ID | Anforderung | Status (05.11.) | Nachweis / offener Test |
|---|---|---|---|
| RLS-001 | One-Tap-Erfassung in App und Widget | Implementiert, Emulator erprobt | Pro Tap genau eine Zeile; auf echtem Gerät offen |
| RLS-002 | Mehrere Ereignisse pro Tag | Implementiert, Emulator erprobt | Drei Taps = drei Einträge |
| RLS-003 | Ereignistag standardmäßig heute, editierbar | Implementiert | Vergangene Tage / Datumsänderung prüfen |
| RLS-004 | Tatsächliche Ereignisuhrzeit zunächst NULL | Implementiert | Auch Widget-/Nachtrags-Flow gezielt prüfen |
| RLS-005 | Automatischer `recorded_at` separat und unverändert | Implementiert | Auch Undo, Backup-Roundtrip prüfen |
| RLS-006 | Exakt sieben Starter | Implementiert | Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol |
| RLS-007 | Tracker erstellen, bearbeiten und archivieren | Funktional implementiert; UX nicht final | Zusätzliche Icons sind noch alte Pixelgrafiken |
| RLS-008 | Mehrere unabhängige Tracker-Widgets | Implementiert, Emulator erprobt | Zwei Instanzen/Neustart auf echtem Gerät prüfen |
| RLS-009 | Diskretes Widget, Label/Zähler/Theme optional | Implementiert | Rosa/Lila/Creme, 4×1 und 2×2 prüfen |
| RLS-010 | Offline & lokale Datenpersistenz | Implementiert | Neustart/Update/Backup im Gerätetest |
| RLS-011 | Verlauf, Bearbeitung, Nachtrag, Löschen | Implementiert | In-App-Löschen mit Undo; Backup/History prüfen |
| RLS-012 | Pastellrosa Modern Pixel Cutie UI | Gestalterisch umgesetzt | Tracker-Motive **Soft Emoji Cutie**; kleine Displays prüfen |
| RLS-013 | Eigenständiger Daily Pep Talk beim App-Start | Implementiert | Zufällige, offline gespeicherte Sprüche; nicht modal |
| RLS-014 | Separates Affirmations-Homescreen-Widget | Implementiert; kompakte Widget-Größe auf Samsung bestätigt | Zusätzliche Größen, Tageswechsel und Neustart noch im vollständigen Regressionstest |
| RLS-015 | Breites kompaktes 4×1-Widget | Implementiert, Emulator bestätigt | 1-Zeilen-Größe durch Nutzerin im Launcher bestätigt |
| RLS-016 | Direkte Löschaktion in Eventliste mit Undo | Implementiert | Ursprungsdaten inklusive `recorded_at` wiederherstellen |
| RLS-017 | Zukünftige Ereignistage verhindern | Implementiert | Navigation darf Zukunft zeigen, Erfassung dort gesperrt |
| RLS-018 | JSON-Backup exportieren/importieren | Implementiert, **noch nicht abgenommen** | Gültiges Backup, Abbruch, defekte Datei, vollständiger Restore |
| RLS-019 | Einheitliche zusätzliche Soft-Emoji-Cutie-Icons für eigene Tracker | **Offen** | Entwurf, Größenvergleich und Icon-Keys/Migration |
| RLS-020 | Monats- und Jahresübersichten / Statistiken | Grundansicht auf Samsung bestätigt | Detailtests zu historischen/archivierten Daten und spätere Heatmap offen |
| RLS-021 | Import historischer Nies-Textnotizen | Geplant | Prüfen ohne bestehende Daten zu überschreiben |
| RLS-022 | Diskretes Undo direkt aus Tracker-Widget | Offene UX-Frage | Keine Konflikte mit Tap/Long-Press des Launchers |
| RLS-023 | Monats-/Jahresstatistiken im Verlauf | Auf Samsung optisch/funktional positiv beurteilt | Grenzfälle, Summenprüfung und Archivdaten noch separat testen |

## Navigation und Bedienung

- **Heute:** Tageszähler, One-Tap-Buttons, Liste des Tages, Bearbeiten und Löschen; Pep Talk ohne erzwungenen Dialog.
- **Verlauf:** Kalender vor/zurück, Tag wählen, historische Einträge ansehen und nachtragen; Zukunftseinträge nicht möglich.
- **Tracker:** Aktivieren, bearbeiten, archivieren und eigene Tracker anlegen. **Custom-Icon-Auswahl ist ein bekanntes Design-Defizit**, nicht freigegeben.
- **Widget Studio:** Instanzen konfigurieren, Reihenfolge, Texte, Zähler, Farbe; tatsächliche Größe im Android-Launcher.
- **Affirmations:** Optionales, separat hinzufügbares Widget; keine Trackingdaten erforderlich.

## Release-Gates

1. `./gradlew :app:assembleDebug` (Windows: `gradlew.bat`) und GitHub Actions erfolgreich.
2. Emulator: One-Tap-Logik, Datum/Uhrzeit, Widget-Resize und -Konfiguration, Undo, Offline-/Neustart-Test.
3. **Echtes Android-Gerät:** Installation, Launcher-Widgets, Tageswechsel, Datenerhalt nach Update, Backup-/Restore-Probe.
4. Vor allgemeiner Weitergabe: Release-Signierung, Datenschutzprüfung, sinnvolle Versionsnummer, ggf. separates Release-APK; **Debug-APK ist kein veröffentlichungsfertiger Release-Build**.
