package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Button extends CustomComponent implements ActionListener{
	
	public static final Color COLOR_OFF = Color.BLUE;
	public static final Color COLOR_ON = Color.GREEN;
	public int bounds = 2;
	boolean flag = true;
	public int height = 50;
	Timer t = new Timer(500,this);
	public int width = 50;
	public Button(int x, int y, String key){
		t.start();
		this.x = x;
		this.y = y;
		this.key = key;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		flag = true;
	}
	public Color[] getColors(){
		Color c = (Properties.getValueOfBool(key)) ? COLOR_ON : COLOR_OFF;
		return new Color[]{Color.WHITE,c};
	}
	private Rectangle getRect(){
		return new Rectangle(x - width / 2, y - height / 2,width, height);
	}
	public Rectangle[] getRects(){
		return new Rectangle[]{getRect(),getValueRect()};
	}
	private Rectangle getValueRect(){
		return new Rectangle((x - width / 2) + bounds, (y - height / 2) + bounds,width- bounds * 2, height - bounds * 2);
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
	public void update(int width, int height){
		
	}
}
