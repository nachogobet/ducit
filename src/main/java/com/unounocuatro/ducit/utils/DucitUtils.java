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

	public static BufferedImage mat2Img(Mat in){
		BufferedImage gray = new BufferedImage(in.width(), in.height(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
		in.get(0, 0, data);

		return gray;
	}

	public static BufferedImage cleanTrash(BufferedImage image){
		for(int i=0; i<image.getWidth(); i++){
			int whites = 0;
			boolean allowed = true;
			boolean cleaned = false;
			for(int j=0; j<image.getHeight(); j++){
				if(image.getRGB(i, j) != Color.WHITE.getRGB() && allowed){
					cleaned = true;
					image.setRGB(i, j, Color.WHITE.getRGB());
				} else if(cleaned){
					whites++;
					allowed = false;
				}
				if(whites>135){
					cleaned = false;
					whites = 0;
					allowed = true;
				}
			}
		}

		for(int i=0; i<image.getWidth(); i++){
			int whites = 0;
			boolean allowed = true;
			boolean cleaned = false;
			for(int j=image.getHeight()-1; j>=0; j--){
				if(image.getRGB(i, j) != Color.WHITE.getRGB() && allowed){
					cleaned = true;
					image.setRGB(i, j, Color.WHITE.getRGB());
				} else if(cleaned){
					whites++;
					allowed = false;
				}
				if(whites>135){
					cleaned = false;
					whites = 0;
					allowed = true;
				}
			}
		}

		return image;
	}

	public static BufferedImage cleanLines(BufferedImage image) {
		boolean line=true;
		for(int i=1; i<image.getWidth()-50; i++){
			for(int j=1; j<image.getHeight(); j++){
				for(int z=i; z<i+50; z++){
					if(image.getRGB(z, j) != Color.BLACK.getRGB())
						line=false;
				}
				if(line)
					doCleanLines(image, i, j);
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
		if(i>0 && j<image.getHeight()-1 && image.getRGB(i-1, j+1)==Color.BLACK.getRGB())
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


		if(text.length()>2){
			if(Character.isDigit(text.charAt(0)))
				text = text.substring(1, text.length());
			if(Character.isDigit(text.charAt(text.length()-1)))
				text = text.substring(0, text.length()-1);
			for(int i=1; i<text.length()-1; i++){
				if(Character.isLetter(text.charAt(i-1)) && Character.isLetter(text.charAt(i+1)) && text.charAt(i) == '¡')
					text = text.substring(0, i) + 'i' + text.substring(i+1,text.length());
				/*if(Character.isDigit(text.charAt(i-1)))
						text = text.substring(0, i-1) + text.substring(i,text.length());
				if(Character.isDigit(text.charAt(i+1)))
						text = text.substring(0, i) + text.substring(i+1,text.length());*/
			}
		}
		text = text.replaceAll("_","");
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

	public static String cleanPlainText(String result) {
		if(result == null)
			return "";
		String textArray[] = result.split("\\r\\n|\\n|\\r");

		for(int i=0; i<textArray.length; i++){
			int errors = 0;
			int size = textArray[i].length();
			for(int j=1; j<size -1; j++){
				if(textArray[i].charAt(j) == '|' && Character.isLetter(textArray[i].charAt(j-1)) && Character.isLetter(textArray[i].charAt(j+1)))
					textArray[i] = textArray[i].substring(0, j) + 'l' + textArray[i].substring(j+1,textArray[i].length());
				if(isBadChar(textArray[i].charAt(j), textArray[i].charAt(j-1), textArray[i].charAt(j+1)))
					errors++;
			}

			if((float)errors/size > 0.1)
				textArray[i] = "El segmento en esta posición fue detectado defectuosamente.";	
		}

		return getTextFromString(textArray);
	}

	private static String getTextFromString(String[] textArray) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < textArray.length; i++) {
			strBuilder.append(textArray[i]);
			strBuilder.append(System.getProperty("line.separator"));
		}
		return strBuilder.toString();
	}

	private static boolean isBadChar(char actual, char previous, char next) {
		if (Character.isDigit(actual)){
			if(Character.isLetter(previous)||Character.isLetter(next))
				return true;
		}

		switch(actual){
		case '°':
			return true;
		case '"':
		{
			// Ejemplos que arrojarían error:  PEPE "!STA	 |  TAMBI"N
			if((previous == ' ' && !Character.isLetter(next)) || Character.isLetter(previous) && !Character.isLetter(next))
				return true;
			if(next == '.')
				return true;
		}
		case '¡':
		{
			if(!Character.isLetter(next))
				return true;
		}
		case '!':
		{
			if(!Character.isLetter(previous))
				return true;
		}
		case '-':
		{
			if(previous == '.' || next == '.')
				return true;
			if(Character.isLetterOrDigit(previous) && next =='\\' )
				return true;
			if(Character.isLetter(previous) && next == ' ')
				return true;
		}
		case '\'':
			// Ejemplos que arrojarían error:  PEPE '!STA	 |  TAMBI'N
			if((previous == ' ' && !Character.isLetter(next)) || (Character.isLetter(previous) && !Character.isLetter(next)))
				return true;
		case '.':
			if (previous == '.' && Character.isLetter(next))
				return true;
			else if (previous == ' ' && next == '.')
				return true;
			else if (previous == '¡' || next == '¡')
				return true;
			else if (previous == '-' || next == '-')
				return true;
			else if (previous == '.' && next == ' ')
				return true;
		case '#':
			return true;
		case '@':
			return true;
		case '|':
			return true;
		case '¿':
			if (Character.isLetter(previous) || !Character.isLetter(next))
				return true;

		case ';':
			if(next == ';' || previous == ';')
				return true;
			else if(Character.isLetter(previous) && Character.isLetter(next))
				return true;
		case '&':
			if(Character.isLetter(previous) && Character.isLetter(next))
				return true;
		case '—':
			return true;
		case '$':
			if(Character.isLetter(previous) && Character.isLetter(next))
				return true;
		}
		return false;
	}

	public static String cleanWikiText(String text) {
		if(text.isEmpty())
			return text;
		int i;
		text = text.replaceAll("</span>", "");
		i = text.indexOf("<span ");
		while(i != -1 && i < text.length()-26){
			if(i<text.length()-26)
				text = text.substring(0, i) + text.substring(i+26, text.length()-1);
			i = text.indexOf("<span ");
		}		

		return text;
	}	
}
