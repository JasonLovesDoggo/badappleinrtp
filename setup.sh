#!/bin/bash

set -e

yt-dlp https://www.youtube.com/watch?v=FtutLA63Cp8 -o badapple
mkdir img
ffmpeg -i badapple.webm -r 12 img/%04d.png
ffmpeg -i badapple.webm -ar 11025 -ac 1 -acodec pcm_u8 dist/badapple.wav
curl https://archive.org/download/bad-apple-resources/subs/bad_apple_ja-rom.srt -o badapple.srt