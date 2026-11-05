# Additional Soft Emoji Cutie Icons (v0.4.1-dev)

**Design approval:** product owner accepted the exact 13-icon set on 05.11.2026.
**Status:** integrated on feature branch; CI build / Samsung test pending.

The `cutie_*.png` resources are **optimized transparent 80×80 PNGs** from the approved concept image, not hand-redrawn alternatives. The seven existing default icons remain unchanged.

| Stable key | New resource |
| --- | --- |
| `book` | `cutie_book.png` |
| `brain` | `cutie_brain.png` |
| `cat` | `cutie_cat.png` |
| `cloud` | `cutie_cloud.png` |
| `clover` | `cutie_clover.png` |
| `coffee` | `cutie_coffee.png` |
| `heart` | `cutie_heart.png` |
| `moon` | `cutie_moon.png` |
| `music` | `cutie_music.png` |
| `pill` | `cutie_pill.png` |
| `star` | `cutie_star.png` |
| `tear` | `cutie_tear.png` |
| `yawn` | `cutie_yawn.png` |

The picker uses `PixelIcons.available`. Its 20 user-facing choices now use a single consistent style; `mascot` remains a branding resource accessible through `resource("mascot")` but is not offered as a tracker icon.

No database schema changes and no stored icon-key migrations; existing custom trackers keep the same stable string key and automatically resolve to the new artwork. Old `pixel_*.png` resources remain in the repository for backwards compatibility.

**Test before merging:** Tracker → create/edit custom tracker, select all new icon choices; verify small widget rendering and saved entries after app restart/update. Check 4-column icon grid on narrower Samsung screens; CI must pass.
