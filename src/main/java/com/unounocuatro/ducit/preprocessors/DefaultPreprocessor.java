package com.unounocuatro.ducit.preprocessors;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

public class DefaultPreprocessor implements Preprocessor {
	
	public BufferedImage doPreprocess(BufferedImage image, BufferedImage binary) {
		
		List<Integer> coordinates = new ArrayList<Integer>();

		for(int i=0; i<image.getWidth(); i++){
			for(int j=0; j<image.getHeight(); j++){
				Color c = new Color(image.getRGB(i, j));
				if(isBlue(c)) image.setRGB(i, j, Color.WHITE.getRGB());
			}	
		}
		
		int count = 0;
		
		for(int j=0; j<image.getWidth(); j+=10){
			for(int i=0; i<image.getHeight(); i++){
				Color c = new Color(image.getRGB(j, i));
				if(c.getRGB() == Color.WHITE.getRGB()){
					count++;						
				} else{
					if(count>50){
						coordinates.add(i-1-count-15);
						coordinates.add(i-1+15);
					}
						
					count = 0;
				}
			}	
		}
		
		
		for(int z=0; z<coordinates.size(); z+=2){
			int wfirst = coordinates.get(z);
			int wlast = coordinates.get(z+1);
			for(int j=0; j<image.getWidth(); j++){
				for(int i=0; i<image.getHeight(); i++){
					if(i<wfirst || i>wlast)
						image.setRGB(j, i, Color.BLACK.getRGB());
				}	
			}
		}
		
		
		
		/*int limit = Integer.MAX_VALUE;
		for(int j=first; j<last; j++){
			for(int i=0; i<image.getWidth(); i++){
				if(j<image.getHeight()){
					Color c = new Color(image.getRGB(i, j));
					if(c.getRGB() != Color.WHITE.getRGB() && i<limit)
						image.setRGB(i, j, Color.BLACK.getRGB());
					else if(limit == Integer.MAX_VALUE)
						limit = i;
				}			
			}	
		}*/
		
		/*limit = Integer.MIN_VALUE;
		for(int j=first; j<last; j++){
			for(int i=image.getWidth()-1; i>=0; i--){
				Color c = new Color(image.getRGB(i, j));
				if(c.getRGB() != Color.WHITE.getRGB() && i>limit)
					image.setRGB(i, j, Color.BLACK.getRGB());
				else if(limit == Integer.MIN_VALUE)
					limit = i;
			}	
		}*/
		
		return image;
	}
	
	private boolean isLetter(Color c){
		return (c.getGreen()+c.getRed()+c.getBlue() < 150)? true : false;
	}
	
	private boolean isPaper(Color c){
		return (c.getGreen()+c.getRed()+c.getBlue() >400 && c.getRed()>140)? true : false;
	}
	
	private boolean isBlue(Color c){
		return ((c.getBlue()>110 && c.getBlue()<200 && c.getRed()<130))? true : false;
	}
	
	
	private int getRGB(Color c){
		return 65536 * c.getRed() + 256 * c.getGreen() + c.getBlue();
	}

	public BufferedImage toBinary(BufferedImage image) {
		return null;
	}

	public BufferedImage toRaw(BufferedImage image) {
		return null;
	}

	public BufferedImage toClean(String path) {
		return null;
	}

}
