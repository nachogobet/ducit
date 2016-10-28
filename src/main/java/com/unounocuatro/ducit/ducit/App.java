package com.unounocuatro.ducit.ducit;

import java.io.IOException;

import com.unounocuatro.ducit.engines.Engine;
import com.unounocuatro.ducit.engines.EngineImpl;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	        
        try {
        	long start = System.currentTimeMillis();
        	Engine engine = EngineImpl.getInstance();
			engine.scan(args[0], null, null);
			System.out.println("Tiempo de ejecución: " + (System.currentTimeMillis() - start) / 1000 + " segundos");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
