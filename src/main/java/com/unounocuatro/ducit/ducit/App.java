package com.unounocuatro.ducit.ducit;

import java.io.IOException;

import com.unounocuatro.ducit.engines.EngineImpl;
import com.unounocuatro.ducit.engines.Engine;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	        
        try {
        	long start = System.currentTimeMillis();
        	Engine engine = EngineImpl.getInstance();
			engine.scan(args[0], null);
			System.out.println("Tiempo de ejecución: " + (System.currentTimeMillis() - start) / 1000 + " segundos");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
