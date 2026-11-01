# Roadmap / priorisiertes Backlog

1. **P0 – v0.2-Quellcode importieren**: Bisheriges Android-Projekt mit Kotlin-Dateien, Ressourcen, sieben Pixel-Icons und CI vollständig in dieses Repository übernehmen. Ohne private Tracking-Backups. Keine binären Assets vergessen.
2. **P0 – Android-Gradle-Build reparieren**: Gradle Wrapper hinzufügen, JDK 17, Android SDK 35, reproduzierbarer `:app:assembleDebug` in GitHub Actions.
3. **P0 – Emulator-Smoke-Tests**: App starten, sieben Start-Tracker, One-Tap, Event-Time NULL, Daten bleiben erhalten.
4. **P0 – Android-Homescreen-Widget-Tests**: hinzufügen, configure, icon-only, zwei Instanzen, resize, Refresh, Neustart.
5. **P1 – Design-/Usability-Review im Emulator**: auf echtem gerenderten Android statt Illustrations-Mockups bewerten; Pixel Cutie überarbeiten.
6. **P1 – Export/Import und Migration absichern**: Datenverlust ausschließen.
7. **P2 – Monats-/Jahresstatistiken und Nies-Notizimport**.

Für Änderungen jeweils kleiner Feature-Branch + Pull Request, CI und Review.
