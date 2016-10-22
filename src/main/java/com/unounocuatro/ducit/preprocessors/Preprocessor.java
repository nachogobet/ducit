package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;
import java.util.List;

import org.opencv.core.Mat;

import com.unounocuatro.ducit.scalars.ColorScalar;

public interface Preprocessor {
	BufferedImage doPreprocess(String path, ColorScalar scalar, int functionality);
	List<Mat> doPreprocessIMG(String path, String destination, ColorScalar scalar, boolean color);
}
