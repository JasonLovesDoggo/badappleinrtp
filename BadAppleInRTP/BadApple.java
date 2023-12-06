// I mean top comments aren't strictly needed but this is hsa so why not

// Name: Colin Cai
// Date: 2023-12-05
// Teacher: Ms. Krasteva
// Description: This program plays the "Bad apple!!" music video on the console, along with some subtitles.

import hsa.*;
import java.io.*;
import java.awt.*;
import sun.audio.*;

class BadApple
{
    Console c;
    byte[] fileData;
    AudioStream as;
    BufferedReader br;

    private static final double SCALE_FACTOR = 4.0 / 3;

    public BadApple ()
    {
	c = new Console (29, 80, "Bad Apple!! played in HSA Console");
	try
	{
	    RandomAccessFile file = new RandomAccessFile ("badapple.bin", "r");
	    fileData = new byte [(int) file.length ()];
	    file.readFully (fileData);

	    as = new AudioStream (new FileInputStream ("badapple.wav"));

	    br = new BufferedReader (new FileReader ("badapple.txt"));
	}
	catch (Exception e)
	{
	}
    }


    public int[] [] [] [] generateFrameData ()
    {
	int[] [] [] [] frames = new int [2630] [] [] [];
	int curFrame = 0;
	int[] [] [] contours = new int [1000] [] [];
	int curContour = 0;
	int[] contour = new int [1000];
	int curPoint = 0;

	for (int index = 0 ; index < fileData.length ;)
	{
	    int b = fileData [index++] & 0xff;
	    if (b == 0xff)
	    {
		int contoursData[] [] [] = new int [curContour] [] [];
		System.arraycopy (contours, 0, contoursData, 0, curContour);
		frames [curFrame++] = contoursData;
		curContour = 0;
	    }
	    else if ((b & 0xfc) == 0xfc)
	    {
		int x[] = new int [curPoint];
		int y[] = new int [curPoint];
		for (int i = 0 ; i < curPoint ; i++)
		{
		    int point = contour [i];
		    x [i] = (int) (point % 240 * 2 * SCALE_FACTOR);
		    y [i] = (int) (point / 240 * 2 * SCALE_FACTOR);
		}
		contours [curContour++] = new int[] []
		{
		    x, y,
		    {
			b == 0xfd ? 1:
			0
		    }
		}
		;
		curPoint = 0;
	    }
	    else
	    {
		contour [curPoint++] = (b << 8) | (fileData [index++] & 0xff);
	    }
	}

	return frames;
    }


    public void badApple () throws IOException
    {
	int[] [] [] [] frames = generateFrameData ();

	AudioPlayer.player.start (as);

	c.setTextColor (new Color (128, 128, 128));
	c.setCursor (29, 63);
	c.print ("Made by Colin Cai");

	c.setTextColor (new Color (47, 0, 94));
	c.setCursor (27, 6);
	c.print ("Lyrics:");

	double targetFrameTime = System.currentTimeMillis ();
	int nextLyricsFrame = -1;
	for (int frameIndex = 0 ; frameIndex < frames.length ; frameIndex++)
	{
	    c.setColor (Color.black);
	    c.fillRect (0, 0, (int) (480 * SCALE_FACTOR), (int) (360 * SCALE_FACTOR));
	    int[] [] [] frame = frames [frameIndex];
	    for (int contourIndex = 0 ; contourIndex < frame.length ; contourIndex++)
	    {
		int[] [] contour = frame [contourIndex];
		c.setColor (contour [2] [0] == 1 ? Color.white:
		Color.black);
		c.fillPolygon (contour [0], contour [1], contour [0].length);
	    }

	    targetFrameTime += 1000d / 12;

	    if (frameIndex == nextLyricsFrame)
	    {
		c.setTextColor (new Color (47, 0, 94));
		c.setCursor (27, 17);
		c.print ("", 80);

		String subtitle = br.readLine ().trim ();
		c.setCursor (27, 40 - subtitle.length () / 2);
		c.print (subtitle);

		nextLyricsFrame = -1;
	    }

	    if (nextLyricsFrame == -1)
	    {
		String frameStr = br.readLine ();
		if (frameStr == null || frameStr.trim ().length () == 0)
		{
		    nextLyricsFrame = -2;
		}
		else
		{
		    nextLyricsFrame = Integer.parseInt (frameStr.trim ());
		}
	    }
	    
	    try
	    {
		Thread.sleep ((long) targetFrameTime - System.currentTimeMillis ());
	    }
	    catch (Exception e)
	    {
	    }
	}

	c.setColor (Color.white);
	c.setFont (new Font ("Arial", Font.PLAIN, 100));
	c.drawString ("Bad Apple!!", 30, 130);
	c.setFont (new Font ("Arial", Font.PLAIN, 25));
	c.drawString ("on Ready to Program's HSA Console", 35, 200);
	c.setFont (new Font ("Arial", Font.PLAIN, 15));
	c.setTextColor (new Color (128, 128, 128));
	c.drawString ("Made by Colin", 35, 260);
    }


    public static void main (String[] args) throws IOException
    {
	BadApple badApple = new BadApple ();
	badApple.badApple ();
    }
}


