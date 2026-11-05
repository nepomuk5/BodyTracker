# UX-Test 01 — Random Life Stats v0.2

**Datum:** 04.11.2026  
**Methode:** Think-Aloud-Selbsttest im Android-Emulator; Bildschirmaufnahme (~7 Min.) + vom Product Owner bereitgestelltes Transkript.  
**Status:** Auswertung; **keine** Codeänderung oder technisch bestätigte Fehlerbehebung.  
**Datenschutz:** Dies ist eine abstrahierte Zusammenfassung. Keine Rohaufnahme, kein vollständiges persönliches Transkript und keine echten Trackingdaten ins Repository.

## Positiv bestätigt
- One-Tap-Tracking und Liste der heutigen Einträge.
- Einträge öffnen, Tag ändern, tatsächliche Uhrzeit setzen/„unbekannt“ lassen, automatisch erfassten Zeitpunkt sehen, löschen.
- Kalendernavigation und leere Tage; Tracker bearbeiten und archivieren.
- Widget Studio: Auswahl, Reihenfolge, Namen/Zähler/Farbe; Homescreen-Widget grundsätzlich funktionsfähig.
- Dezente Statusmeldungen und angenehme, dumpfe Klick-Rückmeldung.

## Probleme / Wünsche
| Bereich | Beobachtung | Issue | Status |
|---|---|---|---|
| Widget | Weit gedehnte Kacheln, sehr kleine Icons/Zähler | #2 | Bestätigtes visuelles Problem |
| Visuelle Identität | Icons teils unkenntlich, störende Einzelpixel, Katze doppelt, Launcher-Icon missfällt | #3 | Designentscheidung offen |
| Heute/Verlauf | Erklärungstext unnötig; schnelles Löschen; redundanter Nachtrag; Zukunftsdatum unklar | #4 | UX-Optimierung |
| Widget | Falsche One-Tap-Eingabe diskret korrigieren, ohne permanenten Minusbutton | #5 | Interaktionsentwurf offen |
| Backup | Abbrechen im Android-System-Dateipicker unklar; tatsächliche Wiederherstellung noch nicht vollständig geprüft | #6 | Usability / Reproduktion erforderlich |
| Widget Studio | Anfangs Verwirrung: Größenwahl in der App vs. tatsächliches Launcher-Resize | #7 | UX-Erklärung/Vorschau |

## Offene Produktentscheidungen
- Icons: hochqualitative klarere Pixel-Sticker vs. andere konsistente Emoji-/Vektorstile; **nicht** bereits beschlossen, den Retro-Look zu verwerfen.
- Cat-Mascot ggf. neu gestalten und sparsam/optional animieren.
- „Life Bits“ als Begriff gefällt; offizielle Umbenennung des Produktes **nicht beschlossen**.
- Zukünftige Ereignisdatum-Eingaben: eher sperren als irreführend „nachtragen“ anbieten; mit Product Owner abklären.
- Undo: Doppel-Tap oder Long-Press im Android-Widget sind aus UX/Technik-Sicht riskant; Alternative entwerfen.

## Nächste Arbeitsschritte
1. Erst vollständigen lokal erfolgreichen v0.2-Android-Quellcode sicher auf den Branch bringen; im Repository liegt bisher nur das Bootstrap/Setup.
2. UX-Fixes mit kleinen PRs und Vorher/Nachher-Screenshots.
3. Für #3 mindestens zwei Iconsysteme mit allen sieben Tracker-Motiven in Widgetgröße zeigen und vergleichen.
4. Nach Implementierung erneut denselben Emulator-Flow prüfen, speziell Widget-Resize/Undo, Backup-Cancel, Navigation.

**Produktinvarianten:** sieben Default-Tracker; Offline; ein Tap = ein Event; tatsächliche Ereignisuhrzeit bleibt anfänglich NULL; Erfassungszeit getrennt; mehrere individuell konfigurierbare Widgets; bestehende Daten bei Updates erhalten.
