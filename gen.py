#!/bin/env python3

import numpy as np
import cv2 as cv

def get_contours(img_id):
    im = cv.imread(f"img/{img_id:04}.png")
    im = cv.cvtColor(im, cv.COLOR_BGR2GRAY)
    ret, contoured = cv.threshold(im, 127, 255, cv.THRESH_BINARY)
    contours, hierarchy = cv.findContours(contoured, cv.RETR_LIST, cv.CHAIN_APPROX_SIMPLE)
    
    contours = [cv.approxPolyDP(contour, 2, True) for contour in contours]
    contours = [contour for contour in contours if len(contour) >= 3]

    inside = []
    for i, c in enumerate(contours):
        inside.append([0, c])

    for i, c1 in enumerate(contours):
        for c2 in contours:
            if c1 is c2:
                continue
            if cv.pointPolygonTest(c2, list(int(i) for i in c1[0][0]), False) > 0:
                inside[i][0] += 1
    
    ans = []
    for i, c in sorted(inside, key=lambda x: x[0]):
        ans.append((i % 2 == 0, c[:,0] // 2))
    return ans

def write_contours(file, contours):
    file.write(b''.join(b''.join(int(point[1] * 240 + point[0]).to_bytes(2, 'big') for point in contour[1]) + (b'\xfd' if contour[0] else b'\xfc') for contour in contours) + b'\xff')

with open("dist/badapple.bin", "wb") as outfile:
# with open("/dev/null", "wb") as outfile:
    print("Processing...")
    for i in range(1, 2631):
    # for i in [276]:
        if i % 17 == 0:
            print(f" {i:4}/2630", end="\r")
        write_contours(outfile, get_contours(i))
    print(" 2630/2630\nDone")