package com.unounocuatro.ducit.engines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import com.unounocuatro.ducit.daos.DucitDaoImpl;
import com.unounocuatro.ducit.preprocessors.Preprocessor;
import com.unounocuatro.ducit.preprocessors.PreprocessorImpl;
import com.unounocuatro.ducit.processors.Processor;
import com.unounocuatro.ducit.processors.ProcessorImpl;
import com.unounocuatro.ducit.scalars.ColorScalar;
import com.unounocuatro.ducit.utils.DucitUtils;

public class EngineImpl implements Engine {
	
	private static final String USER = "root";
	private static final String PASS = "weblogic1";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/ducit";

	//private BufferedImage image;

	private ColorScalar[] actions = new ColorScalar[5];

	private ColorScalar[] colors = {ColorScalar.YELLOW, ColorScalar.VIOLET, ColorScalar.ORANGE, ColorScalar.BLUE, ColorScalar.PINK};

	private Preprocessor preprocessor;

	private Processor processor;

	private String filePath;
	
	private String destination;
	private DucitDaoImpl dao;
	//private String config;

	private static EngineImpl engine = null;

	private EngineImpl() throws SQLException{	
		this.dao = new DucitDaoImpl(DriverManager.getConnection(DB_URL,USER,PASS));
	}

	public static Engine getInstance() throws SQLException{
		return (engine==null)? new EngineImpl() : engine;
	}

	public void scan(String filePath, String destination, String config) throws Exception {
		//this.config = config;
		this.filePath = filePath;
		this.destination = destination;
		setPreprocessor();
		setProcessor();
		setActions();
		//setBufferedImage(filePath);
		//generatePreview();
		process();
	}


	private void setActions() throws FileNotFoundException {
		@SuppressWarnings("resource")
		//Scanner in = new Scanner(new FileReader(this.config));
		Scanner in = new Scanner(new FileReader("C:/ducit/config.txt"));
		int index;
		int color;
		for(int i=0; i<5; i++){
			index = in.nextInt();
			color = in.nextInt();
			this.actions[index-1] = this.colors[color-1];
		}
	}

	@SuppressWarnings("unused")
	private void generatePreview() throws IOException {
		File outputfile = new File("./src/main/resources/images/preview.jpg");
		ImageIO.write(this.preprocessor.doPreprocess(this.filePath, ColorScalar.VIOLET, 4), "jpg", outputfile);	
	}

	private void process() throws SQLException, Exception{
		printPlainText(this.preprocessor.doPreprocessIMG(this.filePath, this.destination, this.actions[0], false));
		printSynonymsAntonyms(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[1], 1), 1));
		this.preprocessor.doPreprocessIMG(this.filePath, this.destination, this.actions[2], true);
		printWordMeanings(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[3], 3), 3));
		printDefinitions(this.processor.doProcess(this.preprocessor.doPreprocess(this.filePath, this.actions[4], 4), 4));
	}
	
	private void printPlainText(List<Mat> result) throws Exception {
		String text = "";
		for(int i=result.size()-1; i>=0; i--){
			text += this.processor.doProcess(DucitUtils.mat2Img(result.get(i)), 0);
			text += System.getProperty("line.separator");
		}
		text = DucitUtils.cleanPlainText(DucitUtils.cleanText(text));
		
			
		if(text.equals("ERROR: Baja calidad de imagen.")){
			printWithProtocol("error", text, 11);
			System.exit(1);
		}
				
		if(text.length() > 5)
			printWithProtocol("plano", text, 1);
		
	}

	private void printDefinitions(String result) throws Exception {
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++){
			if(array[i].length()>2){
				String word = this.dao.fixWord(DucitUtils.cleanText(array[i]));
				printWithProtocol(word, this.dao.getDefinition(word), 5);
			}			
		}	
	}
	
	private void printWithProtocol(String expression, String result, int functionality){
		if(!expression.isEmpty() && expression != null){
			if(result != null && !result.equals("null"))
				System.out.println(System.currentTimeMillis()+ "|" + functionality + "|" + expression + "|" + result + "#");
			else
				System.out.println(System.currentTimeMillis()+ "|" + functionality + "|" + expression + "|" + "expresión sin resultados" + "#");
		}
	}

	private void printWordMeanings(String result) throws SQLException {
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++){
			if(array[i].length()>2){
				String word = this.dao.fixWord(DucitUtils.cleanText(array[i]));
				printWithProtocol(word, this.dao.getWordMeaning(word), 4);
			}			
		}			
	}

	private void printSynonymsAntonyms(String result) throws SQLException {		
		String[] array = DucitUtils.getStringArray(result);
		for(int i=0; i< array.length; i++){
			if(array[i].length()>2){
				String word = this.dao.fixWord(DucitUtils.cleanText(array[i]));
				printWithProtocol(word, this.dao.getSynonyms(word) + "%" + this.dao.getAntonyms(word), 2);
			}			
		}		
	}

	private void setPreprocessor() {
		this.preprocessor = new PreprocessorImpl();

	}

	private void setProcessor(){
		this.processor = new ProcessorImpl();
	}

	/*private void setBufferedImage(String filePath) throws IOException {
		this.image = ImageIO.read(new File(filePath));
	}*/

}
