# Soft Emoji Cutie — approved reference illustrations

The seven `cutie_*.png` images in `app/src/main/res/drawable-nodpi/` were prepared as transparent UI assets **directly from the product owner's approved Soft Emoji Cutie concept board**.

They intentionally replace the hand-redrawn VectorDrawable tracker visuals from PR #9, which deviated from the approved reference. The original vector XML remains in the repo for comparison and future development.

| Key | Motif |
|---|---|
| sneeze | heart-decorated tissue box |
| poop | rounded smiling poop |
| migraine | wincing face with large yellow lightning bolt |
| vomit | nauseated green face |
| hiccup | surprised face with “Hic!” bubble |
| throat | scarf and throat pain expression |
| alcohol | pink glass |

Original logical tracker icon keys and event data remain unchanged. Android uses these 112×112 transparent PNGs as small icon resources. Compare real app and widget rendering at native launcher density. Future hand-crafted scalable replacements should be judged against the approved board; do not redesign without review.
