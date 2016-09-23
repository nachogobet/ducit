package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.unounocuatro.ducit.scalars.ColorScalar;
import com.unounocuatro.ducit.utils.DucitUtils;

public class PreprocessorImpl implements Preprocessor {

	public BufferedImage doPreprocess(String path, ColorScalar scalar){
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

		return DucitUtils.cleanLines(DucitUtils.mat2Img(adapt));
	}

}
