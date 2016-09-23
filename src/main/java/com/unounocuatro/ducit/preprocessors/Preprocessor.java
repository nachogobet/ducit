package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;
import java.io.File;

import org.opencv.core.Mat;

import com.unounocuatro.ducit.scalars.ColorScalar;

public interface Preprocessor {
	BufferedImage doPreprocess(String path, ColorScalar scalar);
}
