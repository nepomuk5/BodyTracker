# Random Life Stats — MVP-Anforderungen

| ID | Anforderung | Abnahme |
|---|---|---|
| RLS-001 | Ein Tippen in App/Widget erzeugt ein Ereignis ohne Öffnen eines Dialogs. | Genau ein Datensatz, auch bei geschlossenem App-Fenster |
| RLS-002 | Mehrere Ereignisse pro Tag bleiben getrennt. | Drei Taps = drei Einträge, Zähler 3 |
| RLS-003 | `event_date` als Ereignistag; editierbar. | Standard lokales Heute, Nachtrag und Datumsänderung möglich |
| RLS-004 | `event_time` anfänglich NULL, erst durch Nutzer gesetzt. | Klickzeit nie als tatsächliche Ereignisuhrzeit ausgegeben |
| RLS-005 | `recorded_at` automatisch/immutable. | Bearbeitung lässt Erfassungszeit unverändert |
| RLS-006 | Starter-Tracker exakt sieben. | Niesen, Kacka, Migräne, Kotzen, Schluckauf, Halsschmerzen, Alkohol |
| RLS-007 | Tracker individuell anlegen/archivieren; alte Daten behalten. | Auch nach Archivierung noch im Verlauf |
| RLS-008 | Android-Homescreen-Widgets, mehrfach und unabhängig. | Unterschiedliche Tracker-Konfiguration je Instanz |
| RLS-009 | Diskretes Widget ohne Labels. | Icon-only, Zähler und Farbe je Widget separat |
| RLS-010 | Lokale Persistenz offline. | Einträge bleiben nach App-/Emulator-Neustart |
| RLS-011 | Verlauf: korrigieren/löschen/nachtragen. | Nutzer kann Datum/Uhrzeit nachträglich pflegen |
| RLS-012 | Visuelle Richtung Modern Pixel Cutie. | Eigene klare Pixel-Icons, rosa/lila/creme |

Später: Jahres- und Monatsübersichten, Statistik-Heatmap, Import der Nies-Textnotizen, Mehrfacheintrag per +N. Keine künstlichen Erinnerungen/Habit-Scoring.

**Release-Gate:** Android-Build in CI erfolgreich UND manuelle One-Tap/Widget-Tests im Android Emulator (siehe `docs/EMULATOR.md`) erfolgreich. Vorher kein Anspruch auf funktionsfähige APK.
