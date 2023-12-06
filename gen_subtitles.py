#!/bin/env python3

import pysrt as srt

subs = srt.open("badapple.srt")

with open("dist/badapple.txt", "w") as outfile:
    for i, sub in enumerate(subs):
        start_frame = round(sub.start.ordinal / 1000 * 12)
        outfile.write(str(start_frame) + "\n")
        outfile.write(sub.text + "\n")
        if i == len(subs) - 1 or sub.end.ordinal != subs[i + 1].start.ordinal:
            end_frame = round(sub.end.ordinal / 1000 * 12)
            outfile.write(str(end_frame) + "\n\n")