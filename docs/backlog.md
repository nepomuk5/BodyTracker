# Roadmap & GitHub-Issue-Übersicht

**Stand: 05.11.2026.** Diese Liste ist eine **priorisierte Übersicht**; konkrete Implementierungsdiskussionen und Abnahmeschritte stehen in den verlinkten GitHub-Issues. Fachlich verbindlich ist `docs/requirements.md`.

## Jetzt — stabiler Projektstand + echter Gerätetest

1. **Erledigt — `main` konsolidiert:** PR #10 hat den Android-Prototypen, die Designänderungen und Dokumentation als einen gemeinsamen Stand integriert. Die veralteten PRs #1 und #9 sind als abgelöst geschlossen. Aktueller integrierter Entwicklungsstand: **v0.4.1-dev** mit Samsung-Widget, Monats-/Jahresstats und 20 Soft Emoji Cutie-Icons; CI und die genannten Samsung-Sichtprüfungen sind erfolgreich.
2. **P0 — Android-Gerät testen (#12):** Debug-APK auf Samsung installieren, 4×1 und 2×2 Widget inkl. Tageswechsel/One-Tap, Daten nach App-Update, Offline und Pep Talk prüfen. Die Grundfunktionen und beide Widgets laufen inzwischen auf einem echten Samsung; **vollständige** Checkliste bleibt offen; die verkleinerbare Pep-Talk-Variante ist auf Samsung bestätigt.
3. **P0 — Daten schützen:** Backup-Härtung in **v0.4.2-dev / #6**: JSON-Datei vor Wiederherstellung validieren und mit Datensätzen anzeigen, Export nach dem Speichern zurücklesen, Dateiauswahl-Abbruch erklären, begrenzte Dateigröße und isolierte Emulator-Tests. **Restore ausschließlich im Emulator testen**, bevor eine Wiederherstellung mit echten Daten erfolgt.

## Danach — Design und Usability

4. **P1 — Eigene Tracker + einheitliche Soft Emoji Cutie Icons:** **#11**. Funktion beibehalten; **Erledigt:** PR #14 gemergt, 13 freigegebene zusätzliche Icons auf Samsung installiert und Auswahl bestätigt. Issue #11 geschlossen.
5. **P1 — Widget-Fehleingaben rückgängig:** **#5**. Keine implizite Doppeltipp-Logik und keine Long-Press-Geste, die mit dem Launcher kollidiert.
6. **P1 — Funktionale Regression prüfen:** **#2 (kompaktes Widget), #3 (Default-Icon-Design) und #8 (Affirmations)** sind erledigt und geschlossen. **#4 (Einträge/Verlauf) und #7 (Widget Studio)** bleiben zur finalen Geräte-Abnahme offen.

## Später

7. **P2 — Monats- und Jahresstatistiken:** Erste lokale Monats-/Jahresübersicht in v0.4.0-dev umgesetzt und von der Nutzerin auf Samsung positiv bewertet; detaillierte Zahlenprüfung und spätere Heatmaps bleiben offen.
8. **P2 — Import der bisherigen Nies-Textnotizen:** sauberer Migrationsdialog mit Vorschau, ohne Doppelzählung.
9. **P2 — Weitere Kawaii-Spielereien:** optionaler Katzen-Mascot, weitere Pep-Talk-Stile, erst nach Stabilisierung.

## Arbeitsregeln

- **Eine Änderung → ein überschaubarer Branch → ein PR.** Nicht langfristig mehrere Draft-PRs verschachteln.
- PR beschreibt: Was geändert wurde, welche Requirements betroffen sind, CI-Status, ob Emulator/echtes Gerät getestet.
- Issue schließen, wenn umgesetzt **und** nachvollziehbar geprüft; andernfalls offenes Test-/Follow-up-Issue anlegen.
- `main` repräsentiert den stabilen integrierten Entwicklungsstand, **nicht automatisch eine veröffentlichte App**.
