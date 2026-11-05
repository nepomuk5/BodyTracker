# Random Life Stats / BodyTracker — Regeln für Coding-Agents

**Quelle der Wahrheit:** `docs/requirements.md` für Produktverhalten, `docs/DECISIONS.md` für gewählte Lösungen, `docs/architecture.md` für technische Grenzen. Product Ownerin: Julia.

## Nicht verhandelbarer Produktvertrag

- Native Kotlin-Android-App, offline, ohne Account, Analytics oder Cloud.
- Sieben automatische Default-Tracker **und keine weiteren**: Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol.
- Ein Tap = ein Ereignis. Pro Tag beliebig viele Ereignisse, kein Bestätigungsdialog bei App-/Widget-Taps.
- `event_date` = Ereignistag (standardmäßig heute); `event_time` = **NULL/unbekannt** bis explizite Eingabe; `recorded_at` = unveränderlicher automatischer Erfassungszeitpunkt.
- Keine Einträge in zukünftige Tage als bereits geschehene Ereignisse.
- Unabhängige konfigurierbare Widgets (Tracker-Auswahl/-Sortierung, diskrete Icon-only-Ansicht, Zähler, Themes); Änderung von Größe darf keine Events verlieren.
- Bereits gespeicherte Einträge und Custom-Tracker auch bei Designänderung, Archivierung oder App-Update erhalten. Icon-Keys niemals unvorbereitet löschen.
- Pastellrosa **Modern Pixel Cutie**-UI, sieben Standardmotive **Soft Emoji Cutie**. Legacy-Pixelbilder für eigene Tracker sind ein **offener UX-Mangel**, nicht Designvorlage.
- Affirmations als optionaler rein lokaler Spaßfaktor, keine medizinischen Ratschläge.
- Keine privaten Trackingdaten/JSON-Backups/DB-Dateien/Signing Keys committen.

## Workflow

- Auf `main` liegt der integrierte Entwicklungsstand. Änderung per neuem Feature-Branch + PR; keine unaufgeforderten großen Umbauten.
- Vor Änderung: README, Anforderungen, Architektur und Backlog prüfen.
- Fachliche Änderung → Requirements/Decision Log aktualisieren und Tests/PR-Nachweis ergänzen.
- Klar trennen: Mockup, kompilierter Code, Emulator-Test, echter Gerätetest, Release.
- Bestehende JSON-Backups und DB-Migrationen schützen; jede Migration mit Altbeständen prüfen.
