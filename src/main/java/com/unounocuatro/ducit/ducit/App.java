package com.unounocuatro.ducit.ducit;

import java.io.IOException;

import com.unounocuatro.ducit.engines.DefaultEngine;
import com.unounocuatro.ducit.engines.Engine;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	        
        try {
        	Engine engine = DefaultEngine.getInstance();
			String result = engine.scan(args[0]);
			System.out.println(result);
			//DucitDAO dao = new DucitDaoImpl();
			//System.out.println(dao.getWordMeaning("perro"));
			//System.out.println(dao.getDefinition("peninsula de valdes"));
			//System.out.println(dao.getSynonyms("paz"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
