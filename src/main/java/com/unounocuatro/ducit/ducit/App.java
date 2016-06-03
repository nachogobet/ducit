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
			String result = engine.scan("./src/main/resources/images/dummy.jpg");
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
