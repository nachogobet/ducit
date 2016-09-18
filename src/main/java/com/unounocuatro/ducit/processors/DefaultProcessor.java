package com.unounocuatro.ducit.processors;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract1;

public class DefaultProcessor implements Processor {
	
	private ITesseract instance = new Tesseract1();
	
	private Properties properties = new Properties();	
	

	public String doProcess(BufferedImage image) throws Exception {
		//loadConfiguration();
		setOCRConfig();
		return instance.doOCR(image);
	}

	private void loadConfiguration() throws IOException {
		InputStream in = getClass().getResource("./src/main/resources/config.properties").openStream();
		properties = new Properties();
		properties.load(in);
		in.close();
		//InputStream input = DefaultProcessor.class.getResourceAsStream("./src/main/resources/config.properties");
		//properties.load(input);
}

private void setOCRConfig() {
	instance.setTessVariable("load_system_dawg", "F");
	instance.setTessVariable("load_freq_dawg", "F");
	instance.setLanguage("spa");
	instance.setDatapath("C:/");
//	instance.setTessVariable("load_system_dawg", properties.getProperty("load_system_dawg"));
//	instance.setTessVariable("load_freq_dawg", properties.getProperty("load_freq_dawg"));
//	instance.setLanguage(properties.getProperty("language"));
//	instance.setDatapath(properties.getProperty("data_path"));
	//instance.setPageSegMode(4);
	instance.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_DEFAULT);
}
}
