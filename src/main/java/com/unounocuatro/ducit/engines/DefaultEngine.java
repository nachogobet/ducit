package com.unounocuatro.ducit.engines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.unounocuatro.ducit.preprocessors.DefaultPreprocessor;
import com.unounocuatro.ducit.preprocessors.Preprocessor;
import com.unounocuatro.ducit.processors.DefaultProcessor;
import com.unounocuatro.ducit.processors.Processor;

public class DefaultEngine implements Engine {
	
	private BufferedImage image;
	
	private Preprocessor preprocessor;
	
	private Processor processor;
	
	private static DefaultEngine engine = null;
	
	private DefaultEngine(){		
	}
	
	public static Engine getInstance(){
		if(engine == null)
			engine = new DefaultEngine();
		
		return engine;
	}

	public String scan(String filePath) throws Exception {
		setPreprocessor();
		setProcessor();
		setBufferedImage(filePath);
		//String raw = process();
		preprocess();
		String clean = process();
				
		generatePreview();
		
		return clean;
	}
	

	private void generatePreview() throws IOException {
		File outputfile = new File("./src/main/resources/images/preview.jpg");
		ImageIO.write(this.image, "jpg", outputfile);
		
	}

	private void preprocess() {
		this.image = this.preprocessor.doPreprocess(this.image);
	}
	
	private String process() throws Exception{
		return this.processor.doProcess(this.image);
	}

	private void setPreprocessor() {
		this.preprocessor = new DefaultPreprocessor();
		
	}
	
	private void setProcessor(){
		this.processor = new DefaultProcessor();
	}

	private void setBufferedImage(String filePath) throws IOException {
			this.image = ImageIO.read(new File(filePath));	
	}
	
}
