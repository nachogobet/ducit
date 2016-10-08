package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.unounocuatro.ducit.scalars.ColorScalar;
import com.unounocuatro.ducit.utils.DucitUtils;

public  class PreprocessorImpl implements Preprocessor {




	public BufferedImage doPreprocess(String path, ColorScalar scalar, int functionality){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread(path);
		Mat marcado = new Mat();
		Mat hsv = new Mat();
		Mat gauss = new Mat();
		Mat adapt = new Mat();	

		Imgproc.cvtColor(source, hsv, Imgproc.COLOR_BGR2HSV);
		Core.inRange(hsv,scalar.getBottom(), scalar.getTop(), marcado);
		Imgproc.GaussianBlur(marcado, gauss, new Size(3,3), 0);
		Imgproc.adaptiveThreshold(gauss, adapt, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,15, 4);

		
		switch(functionality){
		case(0):
			return DucitUtils.mat2Img(adapt);
		case(1):
			return DucitUtils.cleanLines(DucitUtils.mat2Img(adapt));
		case(2):
			return DucitUtils.mat2Img(gauss);
		case(3):
			return DucitUtils.cleanLines(DucitUtils.mat2Img(adapt));
		case(4):
			return DucitUtils.cleanLines(DucitUtils.mat2Img(adapt));
		}

		System.out.println("estas mandando functionality mal");
		return null;
	}

	public void doPreprocessIMG(String path, String destination, ColorScalar scalar) {
		
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat imgProc = new Mat();
			Mat imgFinal = new Mat();//imagen final que se entregará al PROCESADOR
			Mat imgFuente = Imgcodecs.imread(path);

			List<Mat> imgList = new ArrayList<Mat>();
			Mat canny = new Mat(imgFuente.height(),imgFuente.width(),1);
			List<MatOfPoint> contorno = new ArrayList<MatOfPoint>();
			Mat jerarquia = new Mat();
			
			//Realizamos el 1er proc de la imagen con la función ProcesarImag
			
			imgProc = ProcesarImag(imgFuente, scalar);
			
			
			//Realizamos la búsqueda de contornos
			
			canny = BuscarContorno(imgProc, contorno,jerarquia);
			
			//DIBUJAR CONTORNO
			
			//variables para dibujar los contornos
			MatOfPoint2f curvaAprox = new MatOfPoint2f();
			Mat dibujo = Mat.zeros(canny.size(), CvType.CV_8UC1);
			double distAprox=0;
			
			//convertimos la imagen contorneada de BN a Color
			Imgproc.cvtColor(canny, dibujo, Imgproc.COLOR_GRAY2BGR);
			
			
			//dibujamos los contornos
			
			DibujarContorno(dibujo, contorno, curvaAprox, distAprox, jerarquia,imgFuente, imgFinal, imgList);
			
			
			
			

			//imgList=ValidaImag(imgList);

			
			if(imgList.size() != 0)
				GeneraImag(imgList, destination);
			else
				System.out.println("No se encontraron imágenes\n");
				
			
			
						
		}
			




	//procesamiento de la iamgen aplicando filtros y colores
	private Mat ProcesarImag(Mat Img, ColorScalar s)
	{
		Mat imgAux = new Mat();
		Mat imgMask = new Mat();
			
		
		//seteo la imagen auxiliar al tipo color HSV
		Imgproc.cvtColor(Img, imgAux, Imgproc.COLOR_BGR2HSV);
		
		

		
		//creo una imagen con las cosas que estan en naranja
		Core.inRange(imgAux,s.getBottom(), s.getTop(), imgMask);
		
		
		//filtramos ruido de la imagen
		
		Mat kernel = new Mat(3,3,0);
		Imgproc.morphologyEx(imgMask, imgMask, Imgproc.MORPH_OPEN, kernel);
		Imgproc.morphologyEx(imgMask, imgMask, Imgproc.MORPH_CLOSE, kernel);
		

		
		//difuminar imagen para suavizar contornos
		Mat kernel2 = Mat.ones(5, 5, CvType.CV_8U);
		Imgproc.dilate(imgMask,imgMask, kernel2);
		Imgproc.GaussianBlur(imgMask, imgMask, new Size(5,5), 0);

		
		return(imgMask);
	}

	//procesamiento de la imagen ya filtrada para la búsqueda de contorno
	private Mat BuscarContorno(Mat Img, List<MatOfPoint> c, Mat j)
	{	
		Mat ca = new Mat(Img.height(),Img.width(),1);
		Imgproc.Canny(Img, ca, 1,2);

		//busco contornos y guardo las areas
		Imgproc.findContours(ca, c, j, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		return ca;
	}

	private void DibujarContorno(Mat d, List<MatOfPoint> c, MatOfPoint2f cap, double dis, Mat jer, Mat ifuente, Mat ifin, List<Mat> ilist)
	{
		double[] area = new double[c.size()];
			

		for(int i=0; i<c.size();i++)
		{	
			area[i]=Imgproc.contourArea(c.get(i));
			
		}
			
		
		int xInicial = 0,xFinal = 0,yInicial = 0,yFinal = 0, cont = 0;
		for(int j=0; j<area.length;j++)
			{	
				Imgproc.drawContours(d, c, j, new Scalar(0,255,255), 2, 8, jer, 0, new Point());
				MatOfPoint2f contorno2f = new MatOfPoint2f(c.get(j).toArray());
				dis = Imgproc.arcLength(contorno2f, true)*0.02;
				Imgproc.approxPolyDP(contorno2f, cap, dis, true);
				MatOfPoint puntos = new MatOfPoint(cap.toArray());
					
				//unimos los limites de las rectas
					
				Rect recta = Imgproc.boundingRect(puntos);
				
					
				//veo de quedarme solo con los contornos externos de una misma imagen

				if(area[j]>10000)	
				{	
					
					Imgproc.rectangle(d, new Point(recta.x, recta.y), new Point(recta.x + recta.width, recta.y + (recta.height)), new Scalar(255,0,0));
					ifin=ifuente.submat(recta.y, recta.y + recta.height, recta.x, recta.x + recta.width);
					if(cont>0)
						{	
						
							if((yInicial - recta.y) >=150 || (recta.x - xFinal) >= 150)
								{
									ilist.add(ifin);
								}
						
						
						
							/*if((recta.x >= xInicial && recta.y >= yInicial )|| (recta.y+recta.height)<=yFinal&&(recta.x+recta.width)<=xFinal)
								
								{
									//System.out.println("Contorno dentro de contorno-->IGNORO!!!\n");
								}
							else
								{
									ilist.add(ifin);
									xInicial=recta.x;
									xFinal=recta.x + recta.width;
									yInicial=recta.y;
									yFinal=recta.y+recta.height;
									
								}*/
						}
					else
						{
							ilist.add(ifin);
						
						}
					xInicial=recta.x;
					xFinal=recta.x + recta.width;
					yInicial=recta.y;
					yFinal=recta.y + recta.height;
					cont++;
					
				}
				
				
			}

		
	}
	//función que verifica multiples contornos para una misma imagen-->solo guarda un contorno por imagen
	/*private List<Mat> ValidaImag(List<Mat> M)
	{
		List<Mat> aux = new ArrayList<Mat>();
		int act;
		int ant=0;
		float div;
		aux.add(M.get(0));
		for(int i=0; i<M.size();i++)
			{
				act=i;
				
				div = (float)M.get(ant).total()/M.get(act).total();
				
				if(i>0 && (div >= 1.1 || div <= 0.9 ) )
					{	
						aux.add(M.get(act));
					
					}
				ant=act;
			}
		return aux;
		
	}*/


	//función que genera los output de las imagenes procesadas

	private void GeneraImag(List<Mat> M, String destination)
	{
		for(int i=0;i<M.size();i++)
			{
				Imgcodecs.imwrite("C:/ducit/generadas/IMG_N" + (i+1) + ".jpg", M.get(i));
			}
		
	}

		
	}








