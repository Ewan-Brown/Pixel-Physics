package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Button extends CustomComponent implements ActionListener{
	
	public int width = 50;
	public int height = 50;
	public int bounds = 2;
	boolean flag = true;
	Timer t = new Timer(1000,this);
	public Button(int x, int y, String key){
		t.start();
		this.x = x;
		this.y = y;
		this.key = key;
	}
	public void update(int width, int height){
		
	}
	public boolean onClick(Point click){

		if (getRect().contains(click)){
			if(!flag){
				return true;
			}
			flag = false;
			t.restart();
			Properties.setValueOfBool(key, !Properties.getValueOfBool(key));
			return true;
		}
		return false;
	}
	public Rectangle[] getRects(){
		return new Rectangle[]{getRect(),getValueRect()};
	}
	public Color[] getColors(){
		Color c = (Properties.getValueOfBool(key)) ? Color.GREEN : Color.RED;
		return new Color[]{Color.WHITE,c};
	}
	private Rectangle getRect(){
		return new Rectangle(x - width / 2, y - height / 2,width, height);
	}
	private Rectangle getValueRect(){
		return new Rectangle((x - width / 2) + bounds, (y - height / 2) + bounds,width- bounds * 2, height - bounds * 2);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		flag = true;
	}
}
