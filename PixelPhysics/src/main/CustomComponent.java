package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class CustomComponent {

	int x = 0;
	int y = 0;
	String key;
	public void update(int width, int height){
		
	}
	public boolean onClick(Point click){
		return false;
	}
	public Rectangle[] getRects(){
		return null;
	}
	public Color[] getColors(){
		return null;
	}
	public boolean isHidden(){
		return false;
	}
	
}
