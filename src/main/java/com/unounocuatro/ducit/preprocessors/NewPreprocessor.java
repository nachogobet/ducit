package com.unounocuatro.ducit.preprocessors;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.unounocuatro.ducit.utils.DucitUtils;

public class NewPreprocessor implements Preprocessor {

	public BufferedImage toBinary(BufferedImage image){
		BufferedImage result = DucitUtils.deepCopy(image);
		for(int i=0; i<result.getWidth(); i++){
			for(int j=0; j<result.getHeight(); j++){
				Color c = new Color(result.getRGB(i, j));
				if(isLetter(c))
					result.setRGB(i, j, Color.BLACK.getRGB());
				else
					result.setRGB(i, j, Color.WHITE.getRGB());
			}	
		}

		return result;	
	}

	private boolean isYellow(Color c) {
		return (c.getGreen()>130 && c.getRed()>130  && c.getBlue()<80)? true : false;
	}

	private boolean isLetter(Color c){
		return (c.getGreen() + c.getRed() + c.getBlue() < 100 && c.getBlue()<20)? true : false;
	}

	private boolean isPaper(Color c){
		return (c.getGreen()+c.getRed()+c.getBlue() >300 && c.getRed()>150)? true : false;
	}

	private boolean isBlue(Color c){
		return ((c.getBlue()>110 && c.getBlue()<200 && c.getRed()<130))? true : false;
	}


	private int getRGB(Color c){
		return 65536 * c.getRed() + 256 * c.getGreen() + c.getBlue();
	}


	public BufferedImage doPreprocess(BufferedImage image, BufferedImage binary) {
		for(int i=0; i<image.getWidth(); i++){
			for(int j=0; j<image.getHeight(); j++){
				Color c = new Color(image.getRGB(i, j));
				if(isYellow(c))
					image.setRGB(i, j, Color.WHITE.getRGB());
				else
					image.setRGB(i, j, Color.BLACK.getRGB());
			}	
		}
		
		for(int i=1; i<image.getWidth()-1; i++){
			for(int j=1; j<image.getHeight()-1; j++){
				Color c1 = new Color(image.getRGB(i, j));
				Color c2 = new Color(image.getRGB(i-1, j));
				Color c3 = new Color(image.getRGB(i+1, j));
				Color c4 = new Color(image.getRGB(i, j-1));
				Color c5 = new Color(image.getRGB(i, j+1));
				if(c1.getRGB() == Color.BLACK.getRGB() && c2.getRGB() == Color.BLACK.getRGB() && c3.getRGB() == Color.BLACK.getRGB() && c4.getRGB() == Color.BLACK.getRGB() && c5.getRGB() == Color.BLACK.getRGB())
					binary.setRGB(i, j, Color.BLACK.getRGB());
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



		/*for(int j=0; j<binary.getWidth(); j++){
			for(int i=0; i<binary.getHeight(); i++){
				if(i<wfirst || i>wlast)
					binary.setRGB(j, i, Color.BLACK.getRGB());
			}	
		}*/


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
		}

		limit = Integer.MIN_VALUE;
		for(int j=first; j<last; j++){
			for(int i=image.getWidth()-1; i>=0; i--){
				Color c = new Color(image.getRGB(i, j));
				if(c.getRGB() != Color.WHITE.getRGB() && i>limit)
					image.setRGB(i, j, Color.BLACK.getRGB());
				else if(limit == Integer.MIN_VALUE)
					limit = i;
			}	
		}*/

		return binary;
	}

	public BufferedImage toRaw(BufferedImage image) {
		BufferedImage result = DucitUtils.deepCopy(image);
		for(int i=0; i<result.getWidth(); i++){
			for(int j=0; j<result.getHeight(); j++){
				Color c = new Color(result.getRGB(i, j));
				if(isRawLetter(c))
					result.setRGB(i, j, Color.BLACK.getRGB());
				else
					result.setRGB(i, j, Color.WHITE.getRGB());
			}	
		}

		return result;	
	}

	private boolean isRawLetter(Color c) {
		return (c.getRed()<60)? true : false;
	}

	public BufferedImage toClean2(String path) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread(path);
		Mat marcado = new Mat();
		Mat hsv = new Mat();
		Mat gauss = new Mat();
		Mat adapt = new Mat();
		Scalar narBajo = new Scalar(0,85,75);
		Scalar narAlto = new Scalar(50,255,255);

		
		Imgproc.cvtColor(source, hsv, Imgproc.COLOR_BGR2HSV);
		//Imgproc.imwrite("./src/main/resources/images/hsv.jpg", hsv);

		
		Core.inRange(hsv, narBajo, narAlto, marcado);
		//Imgproc.imwrite("./src/main/resources/images/hsv.jpg", marcado);
		
		
		//aplico gauss
		
		Imgproc.GaussianBlur(marcado, gauss, new Size(9,9), 0);
		//Imgproc.imwrite("./src/main/resources/images/hsv.jpg", gauss);
		
		//Aplico adaptative threshold
		
		Imgproc.adaptiveThreshold(gauss, adapt, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 41,11);
		
		//Imgproc.imwrite("./src/main/resources/images/hsv.jpg",adapt);
		
		return DucitUtils.cleanLines(DucitUtils.mat2Img(adapt));
	}
	
	public BufferedImage toClean(String path){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread(path);
		Mat marcado = new Mat();
		Mat hsv = new Mat();
		Mat gauss = new Mat();
		Mat adapt = new Mat();	
		Mat inv = new Mat();
		Mat fin = new Mat();
		
		
		
		try
		{
			
			Scalar res_low = null;
			Scalar res_high = null;
			Scalar img_low = null;
			Scalar img_high = null;
			Scalar sin_low = null;
			Scalar sin_high = null;
			
			//leemos el archivo de configuraciÛn
			
			//setConfig(res_low,res_high,img_low,img_high,sin_low,sin_high);
			
					
			
			
			//VALOR FINAL RESALTADOR NARANJA
			Scalar narBajo = new Scalar(5,75,115);
			Scalar narAlto = new Scalar(25,255,255);
			
			
			//VALOR FINAL RESALTADOR AMARILLO
			Scalar amarBajo = new Scalar(26,85,75);
			Scalar amarAlto = new Scalar(50,255,255);
			
			//VALOR FINAL RESALTADOR VERDE
			Scalar verBajo = new Scalar(35,50,50);
			Scalar verAlto = new Scalar(107,255,255);
			
			//VALOR FINAL RESALTADOR AZUL
			Scalar azulBajo = new Scalar(55,60,65);
			Scalar azulAlto = new Scalar(110,255,255);
			
			//VALOR FINAL RESALTADOR VIOLETA
			
			Scalar violBajo = new Scalar(100,20,90);
			Scalar violAlto = new Scalar(155,255,255);
			
			//VALOR FINAL RESALTADOR ROSA
			Scalar rosaBajo = new Scalar(155,75,140);
			Scalar rosaAlto = new Scalar(255,255,255);
			
			Imgproc.cvtColor(source, hsv, Imgproc.COLOR_BGR2HSV);
			Imgcodecs.imwrite("hsv.jpg", hsv);

			
			Core.inRange(hsv,narBajo, narAlto, marcado);
			Imgcodecs.imwrite("marcado.jpg", marcado);
			
			
			//aplico gauss
			
			Imgproc.GaussianBlur(marcado, gauss, new Size(5,5), 0);
			//Imgproc.imwrite("gauss.jpg", gauss);
			
			//Aplico adaptative threshold
			
			Imgproc.adaptiveThreshold(marcado, adapt, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,15, 4);
			Imgcodecs.imwrite("adapt.jpg",adapt);
			
			Imgproc.adaptiveThreshold(marcado, inv, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 130);
			Imgcodecs.imwrite("invertido.jpg",inv);
			
			Imgproc.adaptiveThreshold(inv, fin, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, -1);
			Imgcodecs.imwrite("fin.jpg",fin);
			
			
			
			
			return DucitUtils.mat2Img(fin);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return null;
			
		}
		
	}

}
