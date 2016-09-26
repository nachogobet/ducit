package com.unounocuatro.ducit.engines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.unounocuatro.ducit.daos.DucitDAO;
import com.unounocuatro.ducit.daos.DucitDaoImpl;
import com.unounocuatro.ducit.preprocessors.Preprocessor;
import com.unounocuatro.ducit.preprocessors.PreprocessorImpl;
import com.unounocuatro.ducit.processors.Processor;
import com.unounocuatro.ducit.processors.ProcessorImpl;
import com.unounocuatro.ducit.scalars.ColorScalar;
import com.unounocuatro.ducit.utils.DucitUtils;

public class EngineImpl implements Engine {
	
	DucitDAO dao = new DucitDaoImpl();

	private BufferedImage image;

	private ColorScalar[] actions = new ColorScalar[5];

	private ColorScalar[] colors = {ColorScalar.YELLOW, ColorScalar.VIOLET, ColorScalar.ORANGE, ColorScalar.BLUE, ColorScalar.PINK};

	private Preprocessor preprocessor;

	private Processor processor;

	private String filePath;

	private static EngineImpl engine = null;

	private EngineImpl(){		
	}

	public static Engine getInstance(){
		return (engine==null)? new EngineImpl() : engine;
	}

	public void scan(String filePath) throws Exception {
		this.filePath = filePath;
		setPreprocessor();
		setProcessor();
		setActions();
		setBufferedImage(filePath);
		generatePreview();
		process();
	}


	private void setActions() throws FileNotFoundException {
		Scanner in = new Scanner(new FileReader("./src/main/resources/config.txt"));
		int index;
		int color;
		for(int i=0; i<5; i++){
			index = in.nextInt();
			color = in.nextInt();
			this.actions[index-1] = this.colors[color-1];
		}
	}

	private void generatePreview() throws IOException {
		File outputfile = new File("./src/main/resources/images/preview.jpg");
		ImageIO.write(this.preprocessor.doPreprocess(this.filePath, ColorScalar.YELLOW), "jpg", outputfile);	
	}

	private void process() throws SQLException, Exception{
		System.out.println(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[0])));
		//printSynonyms(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[1])));
		//printAntonyms(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[2])));
		//printWordMeanings(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[3])));
		//printDefinitions(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[4])));
	}

	private void printDefinitions(String result) throws Exception {
		System.out.println(this.dao.getDefinition(result));	
	}

	private void printWordMeanings(String result) throws SQLException {
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++) System.out.println(this.dao.getWordMeaning(array[i]));
	}

	private void printSynonyms(String result) throws SQLException {
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++) System.out.println(this.dao.getSynonyms(array[i]));
	}
	
	private void printAntonyms(String result) throws SQLException {
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++) System.out.println(this.dao.getAntonyms(array[i]));
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
