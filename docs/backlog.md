# Roadmap & GitHub-Issue-Übersicht

**Stand: 05.11.2026.** Diese Liste ist eine **priorisierte Übersicht**; konkrete Implementierungsdiskussionen und Abnahmeschritte stehen in den verlinkten GitHub-Issues. Fachlich verbindlich ist `docs/requirements.md`.

## Jetzt — stabiler Projektstand + echter Gerätetest

1. **Erledigt — `main` konsolidiert:** PR #10 hat den Android-Prototypen, die Designänderungen und Dokumentation als einen gemeinsamen Stand integriert. Die veralteten PRs #1 und #9 sind als abgelöst geschlossen. Aktueller Entwicklungsstand: **v0.3.1-dev**; der `main`-CI-Build ist erfolgreich.
2. **P0 — Android-Gerät testen (#12):** Debug-APK auf Samsung installieren, 4×1 und 2×2 Widget inkl. Tageswechsel/One-Tap, Daten nach App-Update, Offline und Pep Talk prüfen. Das Gerät ist noch **nicht** getestet.
3. **P0 — Daten schützen:** Export und Import als echten Roundtrip testen (inkl. Abbruch/defekter Datei), **#6**. Alte persönliche Tracker-Daten niemals für den Test löschen.

## Danach — Design und Usability

4. **P1 — Eigene Tracker + einheitliche Soft Emoji Cutie Icons:** **#11**. Funktion beibehalten; 13 zusätzliche Motive für die vorhandenen stabilen Icon-Keys in genau demselben Stil erstellen. Bis dahin alte Icons nur als Übergang; historische Einträge/Keys schützen.
5. **P1 — Widget-Fehleingaben rückgängig:** **#5**. Keine implizite Doppeltipp-Logik und keine Long-Press-Geste, die mit dem Launcher kollidiert.
6. **P1 — Funktionale Regression prüfen:** **#2 (kompaktes Widget), #3 (Default-Icon-Design) und #8 (Affirmations)** sind erledigt und geschlossen. **#4 (Einträge/Verlauf) und #7 (Widget Studio)** bleiben zur finalen Geräte-Abnahme offen.

## Später

7. **P2 — Monats- und Jahresstatistiken:** Kalender-/Heatmaps, Zähler und Trends mit optionaler Jahresauswahl.
8. **P2 — Import der bisherigen Nies-Textnotizen:** sauberer Migrationsdialog mit Vorschau, ohne Doppelzählung.
9. **P2 — Weitere Kawaii-Spielereien:** optionaler Katzen-Mascot, weitere Pep-Talk-Stile, erst nach Stabilisierung.

## Arbeitsregeln

- **Eine Änderung → ein überschaubarer Branch → ein PR.** Nicht langfristig mehrere Draft-PRs verschachteln.
- PR beschreibt: Was geändert wurde, welche Requirements betroffen sind, CI-Status, ob Emulator/echtes Gerät getestet.
- Issue schließen, wenn umgesetzt **und** nachvollziehbar geprüft; andernfalls offenes Test-/Follow-up-Issue anlegen.
- `main` repräsentiert den stabilen integrierten Entwicklungsstand, **nicht automatisch eine veröffentlichte App**.
