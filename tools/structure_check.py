from pathlib import Path
import re
import xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
res=ROOT/'app/src/main/res'
xmlfiles=list(res.glob('**/*.xml'))+[ROOT/'app/src/main/AndroidManifest.xml']
for p in xmlfiles:
    ET.parse(p)
for p in list((ROOT/'app/src/main/java').rglob('*.kt')):
    s=p.read_text()
    for name in re.findall(r'R\.(?:layout|drawable|id|string|xml)\.([a-z][a-z_0-9]*)',s):
        kind=next((x for x in ('layout','drawable','id','string','xml') if f'R.{x}.{name}' in s),None)
        if kind=='layout': assert (res/'layout'/f'{name}.xml').exists(),(p,name)
        if kind=='drawable': assert any((res/r/f'{name}{e}').exists() for r in ('drawable','drawable-nodpi') for e in ('.png','.xml')),(p,name)
        if kind=='xml': assert (res/'xml'/f'{name}.xml').exists(),(p,name)
print(f'OK: parsed {len(xmlfiles)} XML resources; checked referenced Kotlin resources.')
print(f'PNG icon count: {len(list((res/"drawable-nodpi").glob("pixel_*.png")))}')
print('Note: not equivalent to Android compilation or device testing.')
