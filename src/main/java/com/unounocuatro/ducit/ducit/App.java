package com.unounocuatro.ducit.ducit;

import java.io.IOException;

import com.unounocuatro.ducit.engines.EngineImpl;
import com.unounocuatro.ducit.engines.Engine;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	        
        try {
        	Engine engine = EngineImpl.getInstance();
			engine.scan(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
