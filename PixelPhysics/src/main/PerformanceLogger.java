package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PerformanceLogger extends JPanel implements Runnable
{

	ArrayList<Integer> lastCalcTimes = new ArrayList<Integer>();
	ArrayList<Integer> lastPaintTimes = new ArrayList<Integer>();
	public PerformanceLogger(){

	}
	public void paint(Graphics g){
		if(lastCalcTimes.size() == 0){
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.RED);
		int border = 40;
		int calcSpacing = (getWidth() - (border * 2)) / lastCalcTimes.size();
		int x = border;
		Point lastPoint = new Point(x,getHeight() - (int)lastCalcTimes.get(0));
		for(int i = 1; i < lastCalcTimes.size();i++){
			x += calcSpacing;
			Point p = new Point(x,(int)lastCalcTimes.get(i));
			g2.drawLine((int)lastPoint.getX(), (int)lastPoint.getX(), (int)p.getX(), (int)p.getX());
			System.out.println((int)lastPoint.getX() + " " + (int)lastPoint.getY() + " " + (int)p.getX() + " " +(int)p.getX());
			lastPoint = p;
		}
		
	}
	@Override
	public void run() {
		JFrame frame = new JFrame("Performance");
		frame.add(this);
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
		while(true){
			GamePanel game = GamePanel.instance;
			int c = (int)game.lastCalcTime;
			int t = (int)game.lastPaintTime;
			if(c != 0 && t != 0){
				lastCalcTimes.add(c / 10000);
				lastPaintTimes.add(t);
				game.lastCalcTime = 0;
				game.lastPaintTime = 0;
				if(lastCalcTimes.size() > 20){
					lastCalcTimes.remove(0);
				}
				if(lastPaintTimes.size() > 20){
					lastCalcTimes.remove(0);
				}
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.paint(this.getGraphics());
		}
	}

}
