package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class CustomComponent {

	int key;
	int x = 0;
	int y = 0;
	public Color[] getColors(){
		assert false;
		return null;
	}
	public Rectangle[] getRects(){
		return null;
	}
	public boolean isHidden(){
		return false;
	}
	public boolean onClick(Point click){
		return false;
	}
	public void update(int width, int height){
		
	}
	
}
