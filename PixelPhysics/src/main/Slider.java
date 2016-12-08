package main;

import java.awt.Rectangle;

public class Slider {

	int minX = 0;
	int maxX = 0;
	int length = 100;
	int width = 100;
	int x = 0;
	int y = 0;
	String key = "pull";
	
	public Slider(int x, int y){
		minX = x - length / 2;
		maxX = length;
		this.x = x;
		this.y = y;
	}
	public void onClick(double percent){
		Properties.setValueOf(key, percent);
	}
	public Rectangle getRect(){
		return new Rectangle(minX, y,maxX, y - width);
	}
	
}
