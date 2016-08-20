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
		looper:
		for(int i=0; i<image.getWidth()-10; i++){
			for(int j=0; j<image.getHeight(); j++){
				for(int z=i; z<i+9; z++){
					if(image.getRGB(z, j) != Color.BLACK.getRGB())
						line=false;
				}
				if(line){
					doCleanLines(image, i, j);
					break looper;
				}
				line=true;
			}
		}
		return image;
	}

	private static void doCleanLines(BufferedImage image, int i, int j) {
		image.setRGB(i, j, Color.WHITE.getRGB());
		if(image.getRGB(i-1, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j-1);
		if(image.getRGB(i-1, j)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j);
		if(image.getRGB(i-1, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i-1, j+1);
		if(image.getRGB(i, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i, j-1);
		if(image.getRGB(i, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i, j+1);
		if(image.getRGB(i+1, j-1)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j-1);
		if(image.getRGB(i+1, j)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j);
		if(image.getRGB(i+1, j+1)==Color.BLACK.getRGB())
			doCleanLines(image, i+1, j+1);
	}
}
