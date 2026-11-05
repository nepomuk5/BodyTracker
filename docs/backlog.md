# Roadmap & GitHub-Issue-Übersicht

**Stand: 05.11.2026.** Diese Liste ist eine **priorisierte Übersicht**; konkrete Implementierungsdiskussionen und Abnahmeschritte stehen in den verlinkten GitHub-Issues. Fachlich verbindlich ist `docs/requirements.md`.

## Jetzt — stabiler Projektstand + echter Gerätetest

1. **P0 — `main` konsolidieren:** Drei aufeinander aufbauende Entwicklungs-PRs (#1 → #9 → #10) geprüft in dieser Reihenfolge integrieren. Danach nur noch `main` und neue kurze Feature-Branches als Arbeitsgrundlage.
2. **P0 — Android-Gerät testen:** Debug-APK auf Samsung installieren, 4×1 und 2×2 Widget inkl. Tageswechsel/One-Tap, Daten nach App-Update, Offline und Pep Talk prüfen. Das Gerät ist noch **nicht** getestet.
3. **P0 — Daten schützen:** Export und Import als echten Roundtrip testen (inkl. Abbruch/defekter Datei), **#6**. Alte persönliche Tracker-Daten niemals für den Test löschen.

## Danach — Design und Usability

4. **P1 — Eigene Tracker + einheitliche Soft Emoji Cutie Icons:** **#11**. Empfohlen: Funktion beibehalten und ein kleines konsistentes Set zusätzlicher Motive entwerfen; die alten Icons bis zur Umstellung für alte Einträge intern weiter unterstützen. Kein unfertiger Pixelkatalog als Auswahl.
5. **P1 — Widget-Fehleingaben rückgängig:** **#5**. Keine implizite Doppeltipp-Logik und keine Long-Press-Geste, die mit dem Launcher kollidiert.
6. **P1 — Funktionale Regression prüfen:** **#2 / #3 / #4 / #7 / #8** sind im v0.3-Stand überwiegend umgesetzt; erst nach Emulator-/Gerätebestätigung einzeln schließen.

## Später

7. **P2 — Monats- und Jahresstatistiken:** Kalender-/Heatmaps, Zähler und Trends mit optionaler Jahresauswahl.
8. **P2 — Import der bisherigen Nies-Textnotizen:** sauberer Migrationsdialog mit Vorschau, ohne Doppelzählung.
9. **P2 — Weitere Kawaii-Spielereien:** optionaler Katzen-Mascot, weitere Pep-Talk-Stile, erst nach Stabilisierung.

## Arbeitsregeln

- **Eine Änderung → ein überschaubarer Branch → ein PR.** Nicht langfristig mehrere Draft-PRs verschachteln.
- PR beschreibt: Was geändert wurde, welche Requirements betroffen sind, CI-Status, ob Emulator/echtes Gerät getestet.
- Issue schließen, wenn umgesetzt **und** nachvollziehbar geprüft; andernfalls offenes Test-/Follow-up-Issue anlegen.
- `main` repräsentiert den stabilen integrierten Entwicklungsstand, **nicht automatisch eine veröffentlichte App**.
