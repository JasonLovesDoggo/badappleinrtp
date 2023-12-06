#!/bin/env python3


import asyncio
import concurrent.futures
import numpy as np
import cv2 as cv

async def get_contours(img_id):
    im = cv.imread(f"img/{img_id:04}.png", cv.IMREAD_GRAYSCALE)
    ret, contoured = cv.threshold(im, 127, 255, cv.THRESH_BINARY)
    contours, _ = cv.findContours(contoured, cv.RETR_LIST, cv.CHAIN_APPROX_SIMPLE)

    contours = [cv.approxPolyDP(contour, 2, True) for contour in contours]
    contours = [contour for contour in contours if len(contour) >= 3]

    inside = [0] * len(contours)
    for i, c1 in enumerate(contours):
        for j, c2 in enumerate(contours):
            if i != j and cv.pointPolygonTest(c2, tuple(c1[0][0]), False) > 0:
                inside[i] += 1

    sorted_contours = sorted(zip(inside, contours), key=lambda x: x[0])
    ans = [(i % 2 == 0, np.array(c)[:, 0] // 2) for _, c in sorted_contours]
    return ans

async def write_frame(outfile, img_id):
    contours = await get_contours(img_id)
    outfile.write(b''.join(
        b''.join((point[1] * 240 + point[0]).to_bytes(2, 'big') for point in contour[1]) + (b'\xfd' if contour[0] else b'\xfc') 
        for contour in contours
    ) + b'\xff')

async def process_frames():
    with open("dist/badapple.bin", "wb") as outfile:
        print("Processing...")
        tasks = [write_frame(outfile, i) for i in range(1, 2631)]
        await asyncio.gather(*tasks)

    print(" 2630/2630\nDone")

if __name__ == "__main__":
    asyncio.run(process_frames())
