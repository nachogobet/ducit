package com.unounocuatro.ducit.processors;

import java.awt.image.BufferedImage;

public interface Processor {
	String doProcess(BufferedImage image, int functionality) throws Exception;
}
