# Sichere Backups testen – Random Life Stats v0.4.2-dev

## Grundregeln

- **Auf dem Samsung zuerst NUR Export, Vorschau und Abbruch testen.** Einen echten Import mit „ALLES ERSETZEN“ auf dem Produktivgerät vermeiden, bis die Tests im Emulator bestanden sind und eine extern gesicherte, verifizierte JSON-Datei vorliegt.
- Die exportierten JSON-Dateien enthalten persönliche Ereignisdaten **unverschlüsselt**. Nicht in öffentliche Repositories oder Chats hochladen.
- **Widget-Konfigurationen** (Auswahl, Farben, Beschriftungen, Layout) gehören noch **nicht** zur JSON-Sicherung. Gespeichert werden alle Tracker (auch archivierte) und alle Ereignisse mit Datum, optionaler Ereignisuhrzeit und ursprünglicher Erfassungszeit.
- **`adb install -r`**, nicht deinstallieren: Die signaturkompatible APK-Aktualisierung erhält normalerweise lokale Daten.
- Import ersetzt nach expliziter Bestätigung alle Tracker und Ereignisse. Auch eine gültige, aber veraltete Datei ersetzt neuere Einträge. Deshalb immer das Datum und die Vorschau kontrollieren.

## A – Samsung: Nichtdestruktiv prüfen

1. App öffnen → **Tracker → Backup exportieren**. Einen verständlichen Dateinamen und Speicherort wählen; die App liest die gespeicherte Datei zur Verifikation zurück. Erfolgsmeldung muss die Anzahl Tracker/Einträge zeigen.
2. **Backup importieren** tippen → Vorschalt-Erklärung lesen → Dateiauswahl öffnen → mit dem Android-Zurück-Pfeil abbrechen. Eine klare Abbruchmeldung muss erscheinen; alle Einträge und Tracker bleiben.
3. Erneut importieren → **gerade exportierte JSON-Datei** auswählen. Im Vorschaufenster Tracker-/Ereigniszahlen und (bei Ereignissen) Datumszeitraum vergleichen. **„NICHT ERSETZEN“** tippen. Danach Zähler prüfen.
4. Optional **ungültige JSON-Datei** mit dem Dateipicker auswählen (nur auf Emulator sinnvoll). Es muss eine verständliche Fehlermeldung ohne Datenänderung erscheinen. Keine Wiederherstellung auf dem Samsung auslösen.

## B – Emulator: isolierte automatisierte Tests

Mit gestarteter Android-Emulator-Instanz, Terminal im Projektverzeichnis:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Die Testklasse `BackupRoundTripTest` erstellt **ausschließlich** `life_bits_backup_instrumentation_test.db` und löscht sie danach. Sie benutzt **nie** `life_bits.db`, also nicht die eigentliche Tracker-Datenbank.

Automatisierte Szenarien:

- JSON-Roundtrip: Datensätze, `eventTime = null`, manuell gesetzte Uhrzeit, `eventDate`, originale `recordedAt`-Zeit sowie Custom-Icon-Key.
- Vorschau verändert die Datenbank nicht.
- Ungültige Tracker-Referenz wird abgelehnt; vorhandene Einträge bleiben.
- Doppelte Ereignis-ID oder ungültige Uhrzeit wird abgelehnt; vorhandene Einträge bleiben.
- Falsches Backup-Format/Versionsnummer oder defektes JSON wird abgelehnt.

**Wichtig:** Die GitHub-Action baut die APK und kompiliert den Android-Instrumentierungstest. Sie **führt den Test ohne Emulator nicht aus**. Der echte grüne Lauf muss separat durchgeführt werden.

## C – Emulator: vollständiger System-Filepicker

1. Im Emulator einige Demo-Ereignisse erfassen, mindestens einen Tracker selbst erstellen, Datum/Uhrzeit bearbeiten.
2. Backup exportieren und sichtbare Erfolgsmeldung prüfen.
3. Tracker/Event-Anzahlen notieren; zusätzliche Testeinträge erstellen.
4. Den exportierten Stand **nur im Emulator** über Import → Vorschau → „ALLES ERSETZEN“ wiederherstellen.
5. Originalzahlen, Event-Zeiten und historische Datumswerte vergleichen; neue Demo-Einträge müssen verschwunden sein.
6. Widgets neu prüfen: Daten kommen zurück, aber **Widget-Einstellungen werden nicht mit importiert**; gegebenenfalls neu konfigurieren.
7. App und Emulator neu starten und Daten erneut kontrollieren.

## Technische Grenzen

- Backupformat bleibt `format=life-bits, version=1`, um bestehende Exportdateien weiter lesen zu können.
- Eingehende Datei auf 15 MB begrenzt; valide Referenzen, eindeutige IDs und korrekte Zeitfelder sind Pflicht.
- SQL-Transaktion bei Restore: nicht teilweise importieren. Bei Fehler vor oder während der Transaktion bleiben die vorherigen DB-Datensätze erhalten.
- Die Datenbank wird **nicht** von Androids automatischem Cloud-Backup erfasst (`allowBackup=false`). Das manuelle JSON-Backup ist deshalb wichtig.
