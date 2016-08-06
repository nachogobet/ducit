package com.unounocuatro.ducit.engines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.unounocuatro.ducit.preprocessors.NewPreprocessor;
import com.unounocuatro.ducit.preprocessors.Preprocessor;
import com.unounocuatro.ducit.processors.DefaultProcessor;
import com.unounocuatro.ducit.processors.Processor;

public class DefaultEngine implements Engine {
	
	private BufferedImage image;
	
	private BufferedImage binary;
	
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
		generateBinary();
		preprocess();
		String clean = process();
				
		generatePreview();
		
		return clean;
	}
	

	private void generateBinary() {
		this.binary = this.preprocessor.toBinary(this.image);
	}

	private void generatePreview() throws IOException {
		File outputfile = new File("./src/main/resources/images/preview.jpg");
		ImageIO.write(this.binary, "jpg", outputfile);
		
	}

	private void preprocess() {
		this.image = this.preprocessor.doPreprocess(this.image, this.binary);
	}
	
	private String process() throws Exception{
		return this.processor.doProcess(this.binary);
	}

	private void setPreprocessor() {
		this.preprocessor = new NewPreprocessor();
		
	}
	
	private void setProcessor(){
		this.processor = new DefaultProcessor();
	}

	private void setBufferedImage(String filePath) throws IOException {
			this.image = ImageIO.read(new File(filePath));	
	}
	
}
