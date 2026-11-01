# Random Life Stats / BodyTracker — Regeln für Coding-Agents

**Quelle der Wahrheit:** Dieses Repository. Product Ownerin: Julia. Änderungen per Branch + Pull Request; kein stilles Überschreiben von main.

## Produktvertrag
- Native Kotlin-Android-App, offline und ohne Konto; primärer Anwendungsfall: One-Tap-Ereignistracking auf dem Android-Homescreen.
- **Genau sieben** Default-Tracker: Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol. Keine weiteren automatisch anlegen.
- Jeder Tap = ein eigener Datensatz, auch mehrfach pro Tag. App- und Widget-Taps benötigen keinen Bestätigungsdialog.
- Getrennte Zeitfelder: `event_date` = Ereignistag, anfänglich lokal heute; `event_time` = **NULL/unbekannt** bis expliziter Nachtrag; `recorded_at` = unveränderlicher automatischer Erfassungszeitpunkt.
- Mehrere unabhängige Widgets mit frei wählbaren Trackern, Sortierung, Themes und optional nur Icons (diskret ohne Namen); Tageszahlen optional.
- Niemals bestehende Datensätze beim Archivieren/Update heimlich löschen.
- Design: **Modern Pixel Cutie** — pastelrosa, lila, creme, klare kleine Pixel-Sticker-Icons; zugänglich/lesbar.
- Monats-/Jahresstatistiken, Import alter Textnotizen und Bulk-Nachtrag später.
- Keine privaten Trackingdaten ins Repository, keine Analytics, Cloud, Berechtigungen oder medizinischen Aussagen ohne Zustimmung.

## Entwicklung
- Vor Eingriffen `docs/requirements.md`, `docs/EMULATOR.md` und `README.md` lesen.
- Erst vorhandenen v0.2-Quellcode importieren, Gradle-Sync/Compile und Emulator-Smoke-Test herstellen; keine unbeauftragte Architektur-Neuschreibung.
- Fachliche Änderung -> Anforderungen aktualisieren, Tests ergänzen, PR mit Teststatus.
- Unterscheide Mockups, statische Prüfungen, erfolgreichen Build, Emulator-Test und echten Gerätetest sauber.
