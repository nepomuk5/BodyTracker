"""Generate seven original 32×32 pixel-art icons for the v0.2 starter tracker set.
Uses only Pillow. Pixels intentionally remain sharp after 6× nearest-neighbor scaling.
"""
from PIL import Image, ImageDraw
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'app/src/main/res/drawable-nodpi'
OUT.mkdir(parents=True, exist_ok=True)
INK='#62264E'; DEEP='#472044'; GLOSS='#FFF9FA'; PINK='#FE9FCA'; SHADOW='#D75E9D'
PALE='#FFE7EF'; BLUSH='#EF82A7'; BROWN='#A9634A'; BROWN_LT='#D88C64'; BROWN_DK='#85462D'
CREAM='#FFE09A'; YELLOW='#FFCB63'; LAV='#BE93E4'; LAV_DK='#996ACD'; GREEN='#5EC892'
SPRITES={}

def make(name, f):
    img=Image.new('RGBA',(32,32),(0,0,0,0)); d=ImageDraw.Draw(img)
    f(d)
    SPRITES[name]=img
    img.resize((192,192),Image.Resampling.NEAREST).save(OUT/f'pixel_{name}.png')

def sneeze(d):
    # White folded tissue with clearly defined central crease.
    d.polygon([(9,18),(7,13),(12,10),(12,5),(17,7),(20,3),(24,7),(23,12),(27,16),(22,20)], fill=INK)
    d.polygon([(11,17),(9,13),(14,12),(14,7),(17,10),(21,6),(22,12),(25,16),(21,18)],fill=GLOSS)
    d.polygon([(15,13),(18,10),(20,15),(20,20),(16,20)],fill='#D7EBFA')
    d.rectangle((5,18,27,27),fill=INK)
    d.rectangle((7,19,25,24),fill='#F5A0CF')
    d.rectangle((7,25,25,26),fill=SHADOW)
    d.rectangle((10,21,22,22),fill='#FFCAE4')
    d.rectangle((10,19,14,19),fill=GLOSS)
    d.rectangle((16,23,19,24),fill=INK)
    d.rectangle((17,23,19,23),fill=GLOSS)
    d.point((28,8),fill=SHADOW);d.point((30,11),fill=SHADOW)

def poop(d):
    # Structured soft swirl: left-facing highlights, thick dark border, readable expression.
    outline=[(4,22),(5,17),(9,15),(9,11),(13,10),(14,7),(16,4),(19,6),(20,9),(23,10),(23,14),(27,17),(28,22),(26,27),(7,27)]
    d.polygon(outline,fill=INK)
    d.polygon([(7,21),(8,18),(12,17),(12,13),(16,12),(17,9),(18,12),(21,13),(21,16),(24,18),(25,22),(24,24),(9,24)], fill=BROWN)
    d.polygon([(10,17),(12,15),(17,14),(21,15),(19,17),(11,19)],fill=BROWN_LT)
    d.rectangle((11,20,13,22),fill=DEEP);d.rectangle((20,20,22,22),fill=DEEP)
    d.rectangle((9,23,10,24),fill=BLUSH);d.rectangle((23,23,24,24),fill=BLUSH)
    d.rectangle((15,23,19,23),fill=INK);d.rectangle((16,24,18,24),fill=INK)
    d.rectangle((13,13,16,13),fill='#F2B38C')

def migraine(d):
    # Head + lightning bolt as strongly recognizable migraine symbol.
    d.ellipse((5,7,25,28),fill=INK)
    d.ellipse((7,9,23,26),fill='#FFD8C4')
    d.polygon([(8,14),(10,9),(14,8),(20,10),(22,14),(21,16),(18,13),(14,15),(10,13)],fill=PINK)
    d.line((10,19,13,20),fill=DEEP,width=2);d.line((18,20,21,19),fill=DEEP,width=2)
    d.line((14,23,18,23),fill=DEEP,width=1)
    d.point((9,21),fill=BLUSH);d.point((22,21),fill=BLUSH)
    d.polygon([(24,2),(17,14),(22,14),(20,22),(30,10),(25,10),(29,2)],fill=INK)
    d.polygon([(24,5),(20,12),(24,12),(23,17),(28,11),(23,11),(26,5)],fill=YELLOW)
    d.point((29,24),fill=SHADOW)

def vomit(d):
    d.ellipse((3,5,28,28),fill=INK)
    d.ellipse((5,7,26,26),fill=YELLOW)
    d.polygon([(9,14),(14,17),(8,18)],fill=DEEP)
    d.polygon([(22,14),(17,17),(23,18)],fill=DEEP)
    d.rectangle((14,19,19,22),fill=DEEP)
    d.rectangle((14,22,21,26),fill=GREEN)
    d.polygon([(17,24),(23,22),(26,26),(25,31),(21,31),(20,28),(16,29)],fill=GREEN)
    d.rectangle((21,26,23,30),fill='#3BAE78')
    d.rectangle((7,22,8,23),fill=BLUSH);d.rectangle((24,22,25,23),fill=BLUSH)

def hiccup(d):
    d.ellipse((4,6,25,28),fill=INK)
    d.ellipse((6,8,23,26),fill='#FFE8BE')
    d.rectangle((10,16,12,18),fill=INK);d.rectangle((18,16,20,18),fill=INK)
    d.rectangle((14,20,17,24),fill=INK);d.rectangle((15,21,16,23),fill='#F9A5C9')
    d.rectangle((7,20,8,22),fill=PINK);d.rectangle((22,20,23,22),fill=PINK)
    d.rectangle((25,5,27,14),fill=SHADOW)
    d.rectangle((25,16,27,18),fill=SHADOW)
    d.point((29,20),fill=PINK)

def throat(d):
    d.ellipse((8,3,25,22),fill=INK)
    d.ellipse((10,5,23,20),fill='#FFDDD0')
    d.rectangle((12,14,13,15),fill=INK);d.rectangle((19,14,20,15),fill=INK)
    d.rectangle((14,18,18,18),fill=SHADOW)
    d.rectangle((14,20,19,24),fill='#FFB9BD')
    d.polygon([(6,22),(24,22),(27,25),(25,29),(8,29),(4,26)],fill=INK)
    d.polygon([(8,23),(23,23),(25,25),(23,27),(9,27),(7,25)],fill=LAV)
    d.polygon([(18,27),(24,26),(24,31),(20,31)],fill=INK)
    d.polygon([(19,28),(23,28),(23,30),(20,30)],fill=LAV_DK)
    d.line((27,13,30,11),fill=SHADOW,width=2)
    d.line((28,17,31,17),fill=SHADOW,width=2)
    d.line((27,21,30,23),fill=SHADOW,width=2)

def alcohol(d):
    # Generic decorative wine glass means an alcoholic drink event, not a dose.
    d.line((7,5,25,5),fill=INK,width=2)
    d.line((8,6,10,18),fill=INK,width=2)
    d.line((24,6,22,18),fill=INK,width=2)
    d.line((10,18,13,22),fill=INK,width=2)
    d.line((22,18,19,22),fill=INK,width=2)
    d.rectangle((15,21,17,27),fill=INK)
    d.rectangle((11,28,22,29),fill=INK)
    d.polygon([(10,13),(22,13),(21,17),(19,20),(13,20),(11,17)],fill=SHADOW)
    d.polygon([(11,14),(21,14),(20,17),(18,19),(14,19),(12,17)],fill='#E86CAA')
    d.rectangle((11,7,11,11),fill=GLOSS)
    d.rectangle((7,3,8,3),fill=SHADOW)
    d.rectangle((25,3,26,3),fill=SHADOW)
    d.point((27,10),fill=PINK)

make('sneeze',sneeze)
make('poop',poop)
make('migraine',migraine)
make('vomit',vomit)
make('hiccup',hiccup)
make('throat',throat)
make('alcohol',alcohol)

# Compact sheet for documentation, not used by Android itself.
from PIL import ImageFont
sheet=Image.new('RGB',(7*210,265),'#FFF0F8')
d=ImageDraw.Draw(sheet)
for idx,(name,im) in enumerate(SPRITES.items()):
    x=idx*210
    d.rounded_rectangle((x+7,10,x+203,205),radius=28,fill='#FFD7E9',outline='#D95A9C',width=4)
    large=im.resize((164,164),Image.Resampling.NEAREST)
    sheet.paste(large,(x+23,22),large)
    d.text((x+24,222),name,fill=INK)
sheet.save(ROOT/'design/starter-icons-v02.png')

preview=Image.new('RGBA',(360,360),'#FFD5E9')
d=ImageDraw.Draw(preview)
d.rounded_rectangle((5,5,355,355),radius=38,outline='#A6327F',width=7)
for idx,im in enumerate(list(SPRITES.values())[:4]):
    x=18+(idx%2)*166;y=18+(idx//2)*166
    d.rounded_rectangle((x,y,x+156,y+156),radius=21,fill='#FFE9F4',outline='#D6589A',width=4)
    small=im.resize((130,130),Image.Resampling.NEAREST)
    preview.alpha_composite(small,(x+13,y+13))
preview.save(ROOT/'app/src/main/res/drawable/widget_preview.png')
