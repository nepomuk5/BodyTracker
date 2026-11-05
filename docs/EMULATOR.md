# Android testen — Emulator und echtes Smartphone

**Stand 05.11.2026:** Android Studio/Gradle-Build und Emulator mit Tracker- und Affirmations-Widgets sind bereits erfolgreich getestet. Das **echte Smartphone steht noch aus**.

## Voraussetzungen

- Android Studio und Android SDK (Platform Tools `adb`, Build Tools, Android Emulator).
- Lokal ausgechecktes Repository und passende Java-/Gradle-Konfiguration.
- Für Gerätetest: Android-Smartphone mit aktivierten Entwickleroptionen und **USB-Debugging**; vertrauenswürdiges USB-Kabel.

## Debug-APK bauen

Im Repository-Root in PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

Die Debug-APK liegt in `app/build/outputs/apk/debug/app-debug.apk`. Die GitHub-Action erstellt sie ebenfalls als Build-Artefakt.

## Auf dem Emulator testen

1. In Android Studio **Device Manager** öffnen, virtuelles Pixel-Android starten.
2. APK installieren (oder `Run` mit dem App-Modul verwenden).
3. Tracker und Widgets über den Android-Launcher hinzufügen.
4. Zwei unterschiedliche Tracker-Widgets konfigurieren; 4×1 auf eine Zeile verkleinern, 2×2 ebenfalls testen.
5. Mehrfacher Tap: genau so viele Ereignisse, `event_time` weiterhin unbekannt.
6. Datums-/Uhrzeitbearbeitung, Inline-Löschen und Undo ausprobieren.
7. Separates Pep-Talk-Widget hinzufügen und per Tap Spruch wechseln.
8. Emulator und App neu starten; Einträge und Widget-Konfiguration kontrollieren.

## Auf dem Samsung-Gerät testen (noch offen)

1. Auf dem Smartphone **Entwickleroptionen → USB-Debugging** aktivieren. Beim Anschließen der Debug-Freigabe auf dem Gerät zustimmen.
2. Handy per USB verbinden. Terminal im Repository öffnen:
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r ".\app\build\outputs\apk\debug\app-debug.apk"
   ```
3. App starten, sieben Tracker und bestehende Funktionen prüfen. Android-Launcher-Widgets manuell hinzufügen und Größe ausprobieren; die Samsung-One-UI-Größenregeln können vom Pixel-Emulator abweichen.
4. App komplett schließen, Widget-Erfassung testen, App neu öffnen, Einträge prüfen.
5. Smartphone/Widget über einen Tageswechsel testen; vor einer echten Produktivnutzung Backup exportieren.
6. JSON-Backup/Import mit **ausschließlich Testdaten** in sicherer Umgebung prüfen. Import ersetzt vorhandene Events und Tracker; nicht ohne Sicherung auf echten Daten testen.

**Wichtig:** `adb install -r` aktualisiert dieselbe App bei kompatibler Signatur normalerweise unter Beibehaltung der lokalen Daten. Ein Deinstallieren löscht dagegen App-Daten. Die Debug-APK ist keine allgemein veröffentlichungsfertige Release-Version.
