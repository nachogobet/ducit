package com.unounocuatro.ducit.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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
		return text;
	}

	public static String getSQLPattern(String word) {
		String result = new String();
		for(int i=0; i<word.length(); i++){
			result+="%";
			result+=word.charAt(i);
		}
		return result;
	}
}
