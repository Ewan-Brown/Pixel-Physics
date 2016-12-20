package main;

import java.awt.Color;
import java.awt.Rectangle;

public class Slider extends CustomComponent{

	int height = 10;
	String key;
	int width = 1000;
	int x = 0;
	int y = 0;
	
	public Slider(int x, int y, String key){
		this.x = x;
		this.y = y;
		this.key = key;
	}
	public Color[] getColors(){
		return new Color[]{Color.WHITE,Color.RED};
	}
	private Rectangle getRect(){
		return new Rectangle(x - width / 2, y,width, height);
	}
	public Rectangle[] getRects(){
		return new Rectangle[]{getRect(),getValueRect()};
	}
	private Rectangle getValueRect(){
		double v = Properties.getValueOfDouble(key) - Properties.getValueOfDouble(key + "Min");
		double percent = v / Properties.getValueOfDouble(key + "Max");
		double xCo = percent * width + x - (width / 2);
		return new Rectangle((int)xCo - 10,y, 20, 10);
	}
	public void update(int width, int height){
		x = (int)((double)width / 2D);
	}
	
}
