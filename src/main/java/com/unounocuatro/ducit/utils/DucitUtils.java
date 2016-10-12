package com.unounocuatro.ducit.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;

public class DucitUtils {
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static BufferedImage mat2Img(Mat in)
    {
		//Imgproc.cvtColor(in, in, Imgproc.COLOR_RGB2GRAY, 0);

		// Create an empty image in matching format
		BufferedImage gray = new BufferedImage(in.width(), in.height(), BufferedImage.TYPE_BYTE_GRAY);

		// Get the BufferedImage's backing array and copy the pixels directly into it
		byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
		in.get(0, 0, data);
		
		return gray;
    }

	public static BufferedImage cleanLines(BufferedImage image) {
		boolean line=true;
		for(int i=1; i<image.getWidth()-50; i++){
			for(int j=1; j<image.getHeight(); j++){
				for(int z=i; z<i+50; z++){
					if(image.getRGB(z, j) != Color.BLACK.getRGB())
						line=false;
				}
				if(line){
					doCleanLines(image, i, j);
				}
				line=true;
			}
		}
		return image;
	}

	private static void doCleanLines(BufferedImage image, int i, int j) {
		image.setRGB(i, j, Color.WHITE.getRGB());
		if(i>0 && j>0 && image.getRGB(i-1, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j-1);
		if(i>0 && image.getRGB(i-1, j)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j);
		if(i>0 && image.getRGB(i-1, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j+1);
		if(j>0 && image.getRGB(i, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i, j-1);
		if(j<image.getWidth()-1 && image.getRGB(i, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i, j+1);
		if(j>0 && i<image.getWidth()-1 && image.getRGB(i+1, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j-1);
		if(i<image.getWidth()-1 && image.getRGB(i+1, j)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j);
		if(i<image.getWidth()-1 && j<image.getHeight()-1 && image.getRGB(i+1, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j+1);
	}

	public static String[] getStringArray(String result) {
		String[] words = result.split("\\s+");
		for (int i = 0; i < words.length; i++) words[i] = words[i].replaceAll("[^\\w]", "");
		
		return words;
	}

	public static String cleanText(String text) {
		text = text.replaceAll("^\\s+", "");
		text = text.replaceAll("\\s+$", "");
		text = text.replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o").replaceAll("ú", "u");
		
		return text;
	}

	public static String getSQLPattern(String word) {
		String result = new String();
		result+="[a-z]*";
		for(int i=0; i<word.length(); i++){	
			if(word.charAt(i) != 'm')
				result+=word.charAt(i);
			else
				result+="(in|ni|m)";
			result+="[a-z]*";
		}
		return result;
	}
	
	public static int getLevenshteinDistance(String s, String t) {
	      if (s == null || t == null) {
	          throw new IllegalArgumentException("Strings must not be null");
	      }

	      /*
	         The difference between this impl. and the previous is that, rather 
	         than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
	         we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
	         is the 'current working' distance array that maintains the newest distance cost
	         counts as we iterate through the characters of String s.  Each time we increment
	         the index of String t we are comparing, d is copied to p, the second int[].  Doing so
	         allows us to retain the previous cost counts as required by the algorithm (taking 
	         the minimum of the cost count to the left, up one, and diagonally up and to the left
	         of the current cost count being calculated).  (Note that the arrays aren't really 
	         copied anymore, just switched...this is clearly much better than cloning an array 
	         or doing a System.arraycopy() each time  through the outer loop.)

	         Effectively, the difference between the two implementations is this one does not 
	         cause an out of memory condition when calculating the LD over two very large strings.
	       */

	      int n = s.length(); // length of s
	      int m = t.length(); // length of t

	      if (n == 0) {
	          return m;
	      } else if (m == 0) {
	          return n;
	      }

	      if (n > m) {
	          // swap the input strings to consume less memory
	          String tmp = s;
	          s = t;
	          t = tmp;
	          n = m;
	          m = t.length();
	      }

	      int p[] = new int[n+1]; //'previous' cost array, horizontally
	      int d[] = new int[n+1]; // cost array, horizontally
	      int _d[]; //placeholder to assist in swapping p and d

	      // indexes into strings s and t
	      int i; // iterates through s
	      int j; // iterates through t

	      char t_j; // jth character of t

	      int cost; // cost

	      for (i = 0; i<=n; i++) {
	          p[i] = i;
	      }

	      for (j = 1; j<=m; j++) {
	          t_j = t.charAt(j-1);
	          d[0] = j;

	          for (i=1; i<=n; i++) {
	              cost = s.charAt(i-1)==t_j ? 0 : 1;
	              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
	          }

	          // copy current distance counts to 'previous row' distance counts
	          _d = p;
	          p = d;
	          d = _d;
	      }

	      // our last action in the above loop was to switch d and p, so p now 
	      // actually has the most recent cost counts
	      return p[n];
	  }
}
