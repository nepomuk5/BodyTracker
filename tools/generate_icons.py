"""Generate original 20x20 pixel sprites with no third-party art assets."""
from PIL import Image,ImageDraw,ImageFont
from pathlib import Path
P=Path(__file__).resolve().parents[1]/'app/src/main/res/drawable-nodpi'
P.mkdir(parents=True,exist_ok=True)
INK='#642B58'; WHITE='#FFF6F9'; PINK='#F4A4CB'; ROSE='#E46AA7'; BLUSH='#FFB2C8'; BROWN='#A46656'; CREAM='#FFE7A8'; PURPLE='#AA81DD'; BLUE='#78B9F5'; MINT='#77D9B9'
S=20

def make(name,drawfn):
 im=Image.new('RGBA',(S,S),(0,0,0,0)); d=ImageDraw.Draw(im)
 drawfn(d)
 im.resize((160,160),resample=Image.Resampling.NEAREST).save(P/f'pixel_{name}.png')
 return im

def happy(d,x1=4,y1=5,x2=15,y2=16,base=WHITE):
 d.rounded_rectangle((x1,y1,x2,y2),radius=4,fill=base,outline=INK,width=1)
 d.point((7,10),fill=INK); d.point((13,10),fill=INK)
 d.point((6,12),fill=BLUSH); d.point((14,12),fill=BLUSH)
 d.line((9,13,11,13),fill=INK)

def sneeze(d):
 happy(d,3,3,15,16)
 d.rectangle((6,9,7,10),fill=INK);d.rectangle((12,9,13,10),fill=INK)
 d.rectangle((8,13,12,16),fill='#CDEEFF',outline=INK,width=1)
 d.rectangle((10,15,15,18),fill=WHITE,outline=INK,width=1)
 d.point((17,5),fill=ROSE);d.point((18,4),fill=ROSE)

def poop(d):
 d.polygon([(4,14),(4,10),(7,10),(7,7),(9,7),(10,4),(12,6),(12,8),(15,9),(15,11),(17,12),(17,16),(15,18),(5,18),(3,16)],fill=BROWN)
 d.line([(4,14),(4,10),(7,10),(7,7),(9,7),(10,4),(12,6),(12,8),(15,9),(15,11),(17,12),(17,16),(15,18),(5,18),(3,16),(4,14)],fill=INK,width=1)
 d.rectangle((7,13,8,14),fill=INK);d.rectangle((13,13,14,14),fill=INK)
 d.point((6,15),fill=BLUSH);d.point((15,15),fill=BLUSH)
 d.line((9,16,12,16),fill=INK)

def yawn(d):
 d.ellipse((3,4,16,17),fill=CREAM,outline=INK,width=1)
 d.line((6,10,8,11),fill=INK); d.line((12,11,14,10),fill=INK)
 d.ellipse((9,12,11,15),fill=INK);d.rectangle((15,3,17,4),fill=PURPLE);d.rectangle((17,1,19,2),fill=PURPLE)

def cloud(d):
 d.ellipse((2,8,10,16),fill='#E9D2FF',outline=INK)
 d.ellipse((7,4,15,14),fill='#E9D2FF',outline=INK)
 d.ellipse((11,9,18,16),fill='#E9D2FF',outline=INK)
 d.rectangle((5,11,16,16),fill='#E9D2FF')
 d.line((5,16,16,16),fill=INK)
 d.point((8,12),fill=INK);d.point((13,12),fill=INK)

def tear(d):
 d.polygon([(10,2),(4,11),(4,15),(7,18),(13,18),(16,15),(16,11)],fill=BLUE)
 d.line([(10,2),(4,11),(4,15),(7,18),(13,18),(16,15),(16,11),(10,2)],fill=INK,width=1)
 d.line((7,12,9,9),fill=WHITE,width=2)

def heart(d):
 d.polygon([(10,18),(2,10),(2,6),(5,3),(8,3),(10,6),(12,3),(15,3),(18,6),(18,10)],fill=ROSE)
 d.line([(10,18),(2,10),(2,6),(5,3),(8,3),(10,6),(12,3),(15,3),(18,6),(18,10),(10,18)],fill=INK,width=1)
 d.line((4,6,6,5),fill='#FFD4E6',width=2)

def brain(d):
 d.ellipse((2,5,17,17),fill='#F9A4D7',outline=INK,width=1)
 d.line((9,5,9,17),fill=INK)
 d.line((5,8,7,10,5,12,7,14),fill=ROSE,width=1)
 d.line((13,8,11,10,14,12,12,14),fill=ROSE,width=1)

def moon(d):
 d.ellipse((3,3,17,17),fill=CREAM,outline=INK)
 d.ellipse((9,1,19,13),fill=(0,0,0,0))
 # carve transparent crescent
 d.pieslice((7,1,19,14),180,360,fill=(0,0,0,0))
 d.arc((3,3,17,17),60,275,fill=INK,width=1)
 d.point((16,3),fill=ROSE);d.point((18,6),fill=ROSE)

def music(d):
 d.line((10,4,10,15),fill=INK,width=2)
 d.line((10,4,17,3),fill=INK,width=2)
 d.line((17,3,17,13),fill=INK,width=2)
 d.ellipse((5,13,11,18),fill=PURPLE,outline=INK)
 d.ellipse((12,11,18,16),fill=PURPLE,outline=INK)
 d.point((4,5),fill=ROSE)

def star(d):
 d.polygon([(10,2),(12,7),(18,8),(14,12),(15,18),(10,15),(5,18),(6,12),(2,8),(8,7)],fill=CREAM)
 d.line([(10,2),(12,7),(18,8),(14,12),(15,18),(10,15),(5,18),(6,12),(2,8),(8,7),(10,2)],fill=INK,width=1)
 d.point((8,10),fill=INK);d.point((12,10),fill=INK)

def book(d):
 d.polygon([(2,4),(8,5),(10,7),(12,5),(18,4),(18,16),(12,16),(10,18),(8,16),(2,16)],fill='#F0DDFF')
 d.line([(2,4),(8,5),(10,7),(12,5),(18,4),(18,16),(12,16),(10,18),(8,16),(2,16),(2,4)],fill=INK)
 d.line((10,7,10,17),fill=INK);d.line((4,8,8,9),fill=ROSE);d.line((12,9,16,8),fill=ROSE)

def coffee(d):
 d.rectangle((4,7,14,16),fill=WHITE,outline=INK)
 d.arc((12,8,18,14),270,90,fill=INK,width=2)
 d.rectangle((3,17,16,18),fill=INK)
 d.rectangle((7,10,11,12),fill=ROSE)
 d.line((7,3,7,5),fill=PURPLE);d.line((11,2,11,5),fill=PURPLE)

def clover(d):
 for cx,cy in [(7,6),(13,6),(7,12),(13,12)]:d.ellipse((cx-4,cy-4,cx+2,cy+2),fill=MINT,outline=INK,width=1)
 d.line((10,12,10,18),fill=INK,width=2)

def pill(d):
 d.line((5,16,16,5),fill=INK,width=8)
 d.line((5,16,16,5),fill='#F8AFD0',width=6)
 d.line((10,11,16,5),fill=WHITE,width=6)
 d.line((9,10,13,14),fill=INK)

def cat(d):
 d.polygon([(4,8),(4,3),(8,6),(12,6),(16,3),(16,9),(17,11),(16,16),(13,18),(7,18),(3,15),(3,11)],fill='#FFE9F3')
 d.line([(4,8),(4,3),(8,6),(12,6),(16,3),(16,9),(17,11),(16,16),(13,18),(7,18),(3,15),(3,11),(4,8)],fill=INK)
 d.point((7,11),fill=INK);d.point((13,11),fill=INK);d.point((10,13),fill=ROSE)
 d.line((6,14,9,14),fill=INK);d.line((11,14,14,14),fill=INK)

def run():
 icons={'sneeze':sneeze,'poop':poop,'yawn':yawn,'cloud':cloud,'tear':tear,'heart':heart,'brain':brain,'moon':moon,'music':music,'star':star,'book':book,'coffee':coffee,'clover':clover,'pill':pill,'cat':cat}
 originals={k:make(k,f) for k,f in icons.items()}
 from PIL import ImageDraw
 app=Image.new('RGBA',(256,256),'#FFD4E9');ad=ImageDraw.Draw(app)
 ad.rounded_rectangle((8,8,247,247),radius=62,outline='#A93277',width=12)
 app.alpha_composite(originals['cat'].resize((192,192),Image.Resampling.NEAREST),(32,32))
 app.save(Path(__file__).resolve().parents[1]/'app/src/main/res/drawable/app_icon.png')
 preview=Image.new('RGBA',(320,320),'#FFD4EA');d=ImageDraw.Draw(preview)
 d.rounded_rectangle((5,5,314,314),radius=24,outline='#CB4B91',width=8)
 for idx,k in enumerate(['sneeze','poop','yawn','cloud']):
  x=16+(idx%2)*150;y=16+(idx//2)*150
  d.rounded_rectangle((x,y,x+138,y+138),radius=15,fill='#FFE6F2',outline='#D764A5',width=4)
  preview.alpha_composite(originals[k].resize((100,100),Image.Resampling.NEAREST),(x+19,y+18))
 preview.save(Path(__file__).resolve().parents[1]/'app/src/main/res/drawable/widget_preview.png')
 contact=Image.new('RGB',(5*150,3*175),'#FFF2F8');dd=ImageDraw.Draw(contact)
 for idx,(k,im) in enumerate(originals.items()):
  x=(idx%5)*150;y=(idx//5)*175
  contact.paste(im.resize((130,130),Image.Resampling.NEAREST),(x+10,y+10),im.resize((130,130),Image.Resampling.NEAREST))
  dd.text((x+15,y+145),k,fill=INK)
 contact.save(Path(__file__).resolve().parents[1]/'design/pixel-icon-sheet.png')
run()
