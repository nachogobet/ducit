package com.unounocuatro.ducit.preprocessors;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DefaultPreprocessor implements Preprocessor {
	
	public BufferedImage doPreprocess(BufferedImage image) {

		for(int i=0; i<image.getWidth(); i++){
			for(int j=0; j<image.getHeight(); j++){
				Color c = new Color(image.getRGB(i, j));
				if(isBlue(c))
					image.setRGB(i, j, Color.WHITE.getRGB());
			}	
		}
		int count = 0;
		int first = 0;
		int last = 0;
		
		for(int j=0; j<image.getWidth(); j+=10){
			for(int i=0; i<image.getHeight(); i++){
				Color c = new Color(image.getRGB(j, i));
				if(c.getRGB() == Color.WHITE.getRGB()){
					count++;						
				} else{
					if(count>50){
						last = i-1;
						first = last - count;
					}
						
					count = 0;
				}
			}	
		}
		
		int wfirst = first-15;
		int wlast = last +15;
		
		
		
		for(int j=0; j<image.getWidth(); j++){
			for(int i=0; i<image.getHeight(); i++){
				if(i<wfirst || i>wlast)
					image.setRGB(j, i, Color.BLACK.getRGB());
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
		if(c.getGreen()+c.getRed()+c.getBlue() < 150)
			return true;
		return false;
	}
	
	private boolean isPaper(Color c){
		if(c.getGreen()+c.getRed()+c.getBlue() >400 && c.getRed()>140)
			return true;
		return false;
	}
	
	private boolean isBlue(Color c){
		if((c.getBlue()>110 && c.getBlue()<200 && c.getRed()<130))
			return true;
		return false;
	}
	
	
	private int getRGB(Color c){
		return 65536 * c.getRed() + 256 * c.getGreen() + c.getBlue();
	}

}
