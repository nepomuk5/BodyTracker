# Entscheidungen / Decision Log

**Stand: 05.11.2026.** Diese Entscheidungen gelten, bis wir sie ausdrücklich in einem neuen Issue oder PR ändern.

| Thema | Entscheidung | Begründung |
|---|---|---|
| Plattform | Native Android-App in Kotlin | Echte Homescreen-Widgets und Offline-Funktion |
| Zeitlogik | `event_date` lokal heute, `event_time=NULL`, `recorded_at` separat | Nachtragen ohne erfundene genaue Uhrzeit |
| Starter | Genau sieben feste Standardtracker | Fokus und übersichtlicher Start |
| Eigene Tracker | Funktion **behalten**, zusätzliche 13 Icons in Soft Emoji Cutie überarbeiten (Issue #11) | Flexibilität ja, uneinheitliche alte Pixelmotive nein |
| Design | Modern Pixel Cutie als UI; **Soft Emoji Cutie** für Tracker-Illustrationen | Pastell-Ästhetik, aber gut lesbare kleine Icons |
| Maskottchen | Eine sparsam eingesetzte kawaii Katze (Kopfhörer/Laptop) | Keine doppelte Katzen-Deko |
| Widgets | Unabhängige konfigurierbare Tracker-Widgets; breites kompaktes 4×1 | One-Tap auf Homescreen, diskreter Icon-only-Modus |
| Löschen | Direkt in der App mit Undo; Widget-Undo noch offen | Fehler rückgängig machen ohne Doppel-Tap-Fallen |
| Zukunft | Noch nicht eingetretene Ereignisse nicht als Nachtrag speichern | Wahrhaftige Statistik |
| Affirmations | Zufällige lokale positive Sprüche in App und separatem Widget | Spaßiger Zusatz ohne Tracking-Zwang |
| Daten | Offline SQLite + lokaler JSON-Export/-Import | Private Daten ohne Konto/Backend |
| Repo | Integrierter Entwicklungsstand auf `main`; neue Features via PR | Übersicht statt verschachtelter Langzeit-Draft-PRs |

## Nicht entschieden

- Umfang und Auswahl der zusätzlichen **Soft Emoji Cutie** Custom-Tracker-Icons.
- Exakter Widget-Undo-Mechanismus.
- Design und Inhalt späterer Monats-/Jahresstatistiken.
- App-Release-Verteilung: interne Debug-APK vs. später signierte private APK / Play Store.
