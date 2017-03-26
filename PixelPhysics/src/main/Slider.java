package main;

import java.awt.Color;
import java.awt.Rectangle;

public class Slider extends CustomComponent{

	int height = 10;
	int key;
	int width = 1000;
	int x = 0;
	int y = 0;
	
	public Slider(int x, int y, int key){
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
		double v = Properties.doubles[key][1] - Properties.doubles[key][0];
		double percent = v / Properties.doubles[key][2];
		double xCo = percent * width + x - (width / 2);
		return new Rectangle((int)xCo - 10,y, 20, 10);
	}
	public void update(int width, int height){
		x = (int)((double)width / 2D);
	}
	
}
