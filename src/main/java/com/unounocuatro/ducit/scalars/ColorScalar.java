package com.unounocuatro.ducit.scalars;

import org.opencv.core.Scalar;

public enum ColorScalar {
	YELLOW(new Scalar(26,85,75), new Scalar(50,255,255)), ORANGE(new Scalar(5,75,130), new Scalar(25,255,255)),
	GREEN(new Scalar(35,50,50), new Scalar(107,255,255)), BLUE(new Scalar(55,60,65), new Scalar(110,255,255)),
	VIOLET(new Scalar(100,20,90), new Scalar(155,255,255)), PINK(new Scalar(155,75,140), new Scalar(255,255,255));
	
	private Scalar bottom;
	private Scalar top;
	
	private ColorScalar(Scalar bottom, Scalar top){
		this.bottom = bottom;
		this.top = top;
	}
	
	public Scalar getBottom(){
		return this.bottom;
	}
	
	public Scalar getTop(){
		return this.top;
	}
}
