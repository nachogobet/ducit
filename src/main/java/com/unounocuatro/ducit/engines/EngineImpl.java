package com.unounocuatro.ducit.engines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.unounocuatro.ducit.preprocessors.PreprocessorImpl;
import com.unounocuatro.ducit.preprocessors.Preprocessor;
import com.unounocuatro.ducit.processors.ProcessorImpl;
import com.unounocuatro.ducit.scalars.ColorScalar;
import com.unounocuatro.ducit.processors.Processor;

public class EngineImpl implements Engine {
	
	private BufferedImage image;
	
	private BufferedImage raw;
	
	private BufferedImage binary;
	
	private Preprocessor preprocessor;
	
	private Processor processor;
	
	private String result;
	
	private String filePath;
	
	private static EngineImpl engine = null;
	
	private EngineImpl(){		
	}
	
	public static Engine getInstance(){
		return (engine==null)? new EngineImpl() : engine;
	}

	public String scan(String filePath) throws Exception {
		this.filePath = filePath;
		setPreprocessor();
		setProcessor();
		setBufferedImage(filePath);
		generatePreview();
		return process().replaceAll(" i ", "");
	}
	

	private void generatePreview() throws IOException {
		File outputfile = new File("./src/main/resources/images/preview.jpg");
		ImageIO.write(this.preprocessor.doPreprocess(this.filePath, ColorScalar.YELLOW), "jpg", outputfile);	
	}
	
	private String process() throws Exception{
		StringBuilder builder = new StringBuilder("");
		builder.append(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.BLUE)));
		//builder.append(" " + this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.GREEN)));
		builder.append(" " + this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.ORANGE)));
		builder.append(" " + this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.PINK)));
		//builder.append(" " + this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.VIOLET)));
		builder.append(" " + this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, ColorScalar.YELLOW)));
		return builder.toString();
	}

	private void setPreprocessor() {
		this.preprocessor = new PreprocessorImpl();
		
	}
	
	private void setProcessor(){
		this.processor = new ProcessorImpl();
	}

	private void setBufferedImage(String filePath) throws IOException {
			this.image = ImageIO.read(new File(filePath));	
	}
	
}
