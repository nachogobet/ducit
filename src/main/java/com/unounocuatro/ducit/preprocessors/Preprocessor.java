package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;

import com.unounocuatro.ducit.scalars.ColorScalar;

public interface Preprocessor {
	BufferedImage doPreprocess(String path, ColorScalar scalar);
	void doPreprocessIMG(String path, ColorScalar scalar);
}
