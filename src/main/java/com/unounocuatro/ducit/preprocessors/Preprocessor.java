package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;

import com.unounocuatro.ducit.scalars.ColorScalar;

public interface Preprocessor {
	BufferedImage doPreprocess(String path, ColorScalar scalar, int functionality);
	void doPreprocessIMG(String path, String destination, ColorScalar scalar);
}
