package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;
import java.io.File;

import org.opencv.core.Mat;

public interface Preprocessor {
	BufferedImage doPreprocess(BufferedImage image, BufferedImage binary);

	BufferedImage toBinary(BufferedImage image);

	BufferedImage toRaw(BufferedImage image);
	
	BufferedImage toClean(String path);
}
