package main;

import java.awt.Rectangle;

public class Slider {

	int width = 1000;
	int height = 10;
	int x = 0;
	int y = 0;
	String key;
	
	public Slider(int x, int y, String key){
		this.x = x;
		this.y = y;
		this.key = key;
	}
	public void onClick(double percent){
		Properties.setValueOf(key, percent);
	}
	public Rectangle getRect(){
		return new Rectangle(x - width / 2, y,width, height);
	}
	public Rectangle getValueRect(){
		double v = Properties.getValueOf(key) - Properties.getValueOf(key + "Min");
		double percent = v / Properties.getValueOf(key + "Max");
		double xCo = percent * width + x - (width / 2);
		return new Rectangle((int)xCo - 10,y, 20, 10);
	}
	
}
