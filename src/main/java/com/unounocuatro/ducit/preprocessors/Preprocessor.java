package com.unounocuatro.ducit.preprocessors;

import java.awt.image.BufferedImage;

public interface Preprocessor {
	BufferedImage doPreprocess(BufferedImage image);
}
