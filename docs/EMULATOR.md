# Android-Emulator: Random Life Stats lokal testen

Die echte Android-App inklusive Android-Homescreen-Widgets wird mit **Android Studio** auf einem virtuellen Telefon ausgeführt. Der Emulator ist keine Website und kein ChatGPT-Mockup.

## Windows-Einrichtung

1. Android Studio offiziell installieren: https://developer.android.com/studio
2. Beim Setup **Android SDK**, **Android SDK Platform Tools** und **Android Emulator** installieren.
3. Hardware-Virtualisierung aktivieren. In Windows ggf. unter "Windows-Features aktivieren oder deaktivieren" die **Windows-Hypervisor-Plattform** einschalten und neu starten. Anleitung: https://developer.android.com/studio/run/emulator-acceleration
4. Repository klonen und den **Repository-Hauptordner** mit **Open** in Android Studio öffnen.
5. Den **Gradle Sync** vollständig durchlaufen lassen. Fehlende SDK-Komponenten ggf. über **SDK Manager** installieren.
6. **Device Manager** öffnen: View → Tool Windows → Device Manager → + → Create Virtual Device. Ein Pixel-Smartphone-Profil wählen, danach ein Google-Play-fähiges Android-Systemimage (empfohlen: eine aktuell unterstützte Android-Version) herunterladen.
7. Virtuelles Gerät starten und oben im Geräteauswahl-Menü auswählen. Auf **Run ▶** klicken. Android Studio baut und installiert die App im Emulator.

## Widget auf dem virtuellen Homescreen testen

1. Mit Home zum Launcher des virtuellen Smartphones wechseln.
2. Lange auf freie Stelle klicken (Maus gedrückt halten) → **Widgets**.
3. **Random Life Stats** suchen; Widget hinzufügen.
4. Tracker-Auswahl, Icon-only-Modus, Tageszähler und Größe prüfen.
5. Auf ein Tracker-Icon tippen; kontrollieren, ob die App **ohne vorheriges Öffnen** genau ein Ereignis mit dem heutigen Ereignisdatum speichert. **Ereignisuhrzeit bleibt unbekannt/NULL**; Erfassungszeit separat.
6. In der App Eintrag bearbeiten; nach App-Neustart Datenpersistenz und Widget-Aktualisierung überprüfen.

## Hinweise

- Der Android Emulator läuft **lokal auf deinem PC**, nicht in GitHub und nicht in ChatGPT.
- GitHub Actions erstellt zusätzlich eine **Debug-APK**, bietet aber keinen interaktiven Homescreen. Für visuelle und Widget-Tests brauchst du den Emulator.
- Für gute Performance empfehlen sich 16 GB RAM oder mehr, eine SSD und aktive Hardware-Virtualisierung.
- Android Studio lädt SDK/Emulator beim ersten Mal herunter. Kein physisches Android-Gerät erforderlich.
- Die vorhandene ZIP-v0.2 ist **noch nicht auf Build-Fähigkeit geprüft**. Zunächst alle Quelldateien/Assets ins Repository übernehmen, danach Gradle Sync und Build korrigieren. Keine erfolgreiche APK behaupten, bevor CI oder Android Studio tatsächlich erfolgreich bauen.

Offizielle Referenzen:
- https://developer.android.com/studio/run/emulator
- https://developer.android.com/studio/run/managing-avds
- https://developer.android.com/studio/install
