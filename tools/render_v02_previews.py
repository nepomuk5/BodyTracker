"""Reference previews using the exact v0.2 pixel icon assets, NOT device screenshots.
The mock data are illustrative and aren't stored in the app.
"""
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont,ImageFilter
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'design'; OUT.mkdir(exist_ok=True)
ICONS=ROOT/'app/src/main/res/drawable-nodpi'
W,H=864,1810
BG='#FFF0F8'; INK='#61234E'; BERRY='#BB2D80'; SOFT='#A06987'; LINE='#D95099'
PINK='#FFD6E9'; PINK2='#FFE4F0'; PURE='#FFF9FC'; ACTIVE='#FFADD4'; LAV='#EAD2FA'
INTER='/usr/share/fonts/opentype/inter/InterDisplay-Regular.otf'
INTERB='/usr/share/fonts/opentype/inter/InterDisplay-SemiBold.otf'
MONO='/usr/share/fonts/truetype/dejavu/DejaVuSansMono-Bold.ttf'
font=lambda size,bold=False,pixel=False: ImageFont.truetype(MONO if pixel else (INTERB if bold else INTER),size)
entries=[('sneeze','Niesen',2),('poop','Kacka',1),('migraine','Migräne',0),('vomit','Kotzen',0),('hiccup','Schluckauf',1),('throat','Halsschmerzen',0),('alcohol','Alkohol',2)]

def txt(d,x,y,s,size=32,fill=INK,bold=False,pixel=False,anchor=None):
    d.text((x,y),s,font=font(size,bold,pixel),fill=fill,anchor=anchor)

def icon(dst,key,x,y,size):
    im=Image.open(ICONS/f'pixel_{key}.png').convert('RGBA').resize((size,size),Image.Resampling.NEAREST)
    dst.paste(im,(int(x),int(y)),im)

def roundrect(d,xy,fill,outline=LINE,w=3,r=22):
    d.rounded_rectangle(xy,radius=r,fill=fill,outline=outline,width=w)

def center(d,x,y,s,size=28,fill=INK,bold=False,pixel=False):
    txt(d,x,y,s,size,fill,bold,pixel,anchor='mm')

def details(d):
    d.line((0,77,W,77),fill='#F8D9E8',width=2)
    txt(d,48,23,'09:42',30,INK,True)
    for i,h in enumerate([11,17,23,29]):
        d.rectangle((705+i*12,56-h,713+i*12,56),fill=INK)
    d.arc((768,29,793,54),190,345,fill=INK,width=4)
    d.arc((775,36,787,49),190,345,fill=INK,width=4)
    d.rounded_rectangle((807,29,841,51),radius=5,outline=INK,width=3)
    d.rectangle((841,36,845,43),fill=INK)
    d.rectangle((811,33,833,47),fill=INK)

def add_sparkles(d):
    for x,y,r in [(800,118,5),(664,201,4),(35,220,3),(820,406,5),(30,390,4)]:
        d.line((x-r,y,x+r,y),fill='#E98AB7',width=2)
        d.line((x,y-r,x,y+r),fill='#E98AB7',width=2)

def navicon(d,typ,x,y):
    x=int(x);y=int(y)
    if typ==0:  # home
        d.line([(x-22,y),(x,y-19),(x+22,y)],fill=INK,width=5,joint='curve')
        d.rectangle((x-17,y,x+17,y+25),outline=INK,width=5)
        d.rectangle((x-5,y+9,x+5,y+25),fill=INK)
    elif typ==1:  # calendar
        d.rounded_rectangle((x-22,y-17,x+22,y+25),radius=5,outline=INK,width=5)
        d.line((x-22,y-3,x+22,y-3),fill=INK,width=4)
        for xx in (x-12,x,x+12):
            for yy in (y+6,y+16):d.rectangle((xx-2,yy-2,xx+2,yy+2),fill=INK)
    elif typ==2: # heart
        d.line([(x-21,y+4),(x-21,y-10),(x-10,y-17),(x,y-7),(x+10,y-17),(x+21,y-10),(x+21,y+4),(x,y+25),(x-21,y+4)],fill=INK,width=5,joint='curve')
    elif typ==3: # widget grid
        for dx in (-18,8):
            for dy in (-18,8):
                d.rounded_rectangle((x+dx,y+dy,x+dx+15,y+dy+15),radius=3,outline=INK,width=4)

def bottomnav(im,active):
    d=ImageDraw.Draw(im)
    y=1669
    d.rectangle((0,y,W,H),fill='#FFE0EE')
    d.line((0,y,W,y),fill='#EEABD0',width=3)
    items=['Heute','Verlauf','Tracker','Widget']
    spacing=W//len(items)
    for j,name in enumerate(items):
        x=j*spacing+10
        if j==active:roundrect(d,(x,y+12,x+spacing-20,H-13),ACTIVE,'#DF66A4',2,24)
        navicon(d,j,x+spacing//2-10,y+53)
        center(d,x+spacing//2-10,y+112,name,25,INK,True)

def header(im,title,sub,second=False):
    d=ImageDraw.Draw(im)
    if second:
        txt(d,50,116,'♡ TRACKER ♡',52,BERRY,True,pixel=True)
    else:
        txt(d,45,112,'RANDOM',50,BERRY,True,pixel=True)
        txt(d,45,171,'LIFE STATS',50,BERRY,True,pixel=True)
        icon(im,'cat',695,115,125)
    if second:
        txt(d,50,188,sub,27,SOFT)
    else:
        txt(d,49,248,sub,27,SOFT)

def preview_today():
    im=Image.new('RGB',(W,H),BG); d=ImageDraw.Draw(im)
    details(d);header(im,'','Little moments, big statistics ♡');add_sparkles(d)
    roundrect(d,(37,302,827,432),PURE,'#E988B7',3,27)
    txt(d,66,326,'✦ HEUTE',39,INK,True,pixel=True)
    txt(d,66,380,'Sonntag, 1. November 2026',27,SOFT)
    icon(im,'heart',738,326,82)
    tile_y=[456,714]
    for i,(key,name,count) in enumerate(entries):
        col=i%4;row=i//4;x=37+col*202;y=tile_y[row]
        roundrect(d,(x,y,x+190,y+243),PINK if i%3==0 else PINK2,LINE,3,26)
        icon(im,key,x+43,y+14,108)
        s=name
        if name=='Halsschmerzen':
            center(d,x+95,y+156,'Hals-',23,INK,True)
            center(d,x+95,y+183,'schmerzen',23,INK,True)
        else:
            center(d,x+95,y+174,s,25 if len(s)<10 else 22,INK,True)
        roundrect(d,(x+67,y+199,x+123,y+232),PURE,'#EE95BF',2,16)
        center(d,x+95,y+216,str(count),24,INK,True,pixel=True)
    txt(d,40,992,'✦ HEUTIGE EINTRÄGE',35,INK,True,pixel=True)
    listitems=[('alcohol','Alkohol','20:14'),('sneeze','Niesen','15:37'),('poop','Kacka','13:02'),('hiccup','Schluckauf','10:21')]
    for i,(key,name,record) in enumerate(listitems):
        y=1046+i*137
        roundrect(d,(37,y,826,y+120),PURE,'#EAA4C8',3,22)
        icon(im,key,57,y+17,86)
        txt(d,162,y+15,name,29,INK,True)
        txt(d,162,y+55,'Ereigniszeit: unbekannt',23,SOFT)
        txt(d,600,y+57,'Erfasst: '+record,20,SOFT)
        for dx in (0,10,20):
            d.ellipse((773+dx,y+57,778+dx,y+62),fill=SOFT)
    bottomnav(im,0)
    im.save(OUT/'RandomLifeStats_v02_Heute_Vorschau.png')

def preview_trackers():
    im=Image.new('RGB',(W,H),BG);d=ImageDraw.Draw(im)
    details(d);header(im,'','Deine kleine Pixel-Sammlung',True);add_sparkles(d)
    options=[('Alle',True),('Aktiv',False),('Archiviert',False)]
    for i,(name,active) in enumerate(options):
        x=38+i*267
        roundrect(d,(x,266,x+256,330),ACTIVE if active else PURE,'#DE7BAF',3,23)
        center(d,x+128,297,name,28,INK,True)
    row_counts=[12,8,3,1,4,2,7]
    for i,(key,name,_) in enumerate(entries):
        y=358+i*166
        roundrect(d,(37,y,826,y+151),PURE,'#EAA2C7',3,24)
        roundrect(d,(53,y+18,162,y+132),PINK,'#E88AB8',2,17)
        icon(im,key,62,y+23,92)
        txt(d,184,y+24,name,34,INK,True)
        txt(d,184,y+80,f'{row_counts[i]} Einträge gesamt',24,SOFT)
        roundrect(d,(715,y+53,801,y+97),'#EE77B3','#D45398',2,22)
        d.ellipse((763,y+56,798,y+94),fill='#FFFAFD')
        # six tiny pixels for reordering affordance
        for xx in (678,691):
            for yy in (y+66,y+80,y+94): d.ellipse((xx,yy,xx+5,yy+5),fill='#D2A6C3')
    y=1541
    roundrect(d,(70,y,793,y+88),ACTIVE,BERRY,4,25)
    center(d,432,y+43,'+ Neuen Tracker erstellen',29,INK,True)
    bottomnav(im,2)
    im.save(OUT/'RandomLifeStats_v02_Tracker_Vorschau.png')

preview_today();preview_trackers()
