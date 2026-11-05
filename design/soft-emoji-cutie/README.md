# Soft Emoji Cutie — Icon-Library v0.3

Product Owner approval: 04.11.2026, GitHub Issue #3.

Exactly seven reworked default-tracker illustrations, defined as **native Android VectorDrawables**, with matching editable SVG sources under `design/soft-emoji-cutie/`.

- `sneeze`: pink tissue box with one tissue (Niesen)
- `poop`: friendly brown poop (Kacka)
- `migraine`: wincing face and yellow lightning bolt (Migräne)
- `vomit`: yellow-green nauseated face (Kotzen)
- `hiccup`: startled face with exclamation bubble (Schluckauf)
- `throat`: face with purple scarf and pain marks (Halsschmerzen)
- `alcohol`: stemmed glass with pink liquid (Alkohol)

The existing stable icon keys are preserved by `PixelIcons.resource()`, so user-configured trackers and saved widget selections remain valid. Previously available pixel-icon keys remain available for custom trackers until a future broader icon-library decision.

**Design intent:** high-contrast berry outlines, smoothly scaled vectors, no isolated random pixels. App UI, legacy cat mascot and widget layout are **not** redesigned in this PR (separate issues #2/#3). In particular, the source SVGs and Android vectors should be manually compared in an emulator at small widget size.

**Acceptance checks:** One-tap tracking unchanged; `event_time` remains NULL on tap; custom tracker icon changes and multi-widget selections survive update; all seven new images visible in Today, History, Tracker and Widget Studio / homescreen widget.
