package com.example.cameratest;

public class TimeTest {
	private long timeStart;
	private long timeEnd;
	
	public void getTimeStart(){
		timeStart = System.currentTimeMillis();
	}
	
	public void getTimeEnd(){
		timeEnd = System.currentTimeMillis();
	}
	
	public long getStartEndDistance(){
		return timeEnd - timeStart;
	}
}
