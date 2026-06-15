import struct
import zlib

def make_png(w, h):
    return b'\x89PNG\r\n\x1a\n' + b''.join(struct.pack('>I4s', len(chunk), name) + chunk + struct.pack('>I', zlib.crc32(name + chunk)) for name, chunk in [(b'IHDR', struct.pack('>IIBBBBB', w, h, 8, 2, 0, 0, 0)), (b'IDAT', zlib.compress(b''.join(b'\x00' + b'\xff\x00\x00' * w for _ in range(h)))), (b'IEND', b'')])

with open('wear/src/main/res/drawable/preview_digital.png', 'wb') as f:
    f.write(make_png(320, 320))
