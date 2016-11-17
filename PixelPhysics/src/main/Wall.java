package main;

import java.awt.Color;
import java.awt.Polygon;
import java.util.Random;

public class Wall {
	static double radius = 2;
	int[] x = new int[4];
	int[] y = new int[4];
	Polygon p;
	Color c;
	boolean flag = false;
	public Wall(double x1, double y1, double x2, double y2){
		Random rand = new Random();
		c = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
		double m1 = (y2 - y1) / (x2 - x1);
		double m2 = -1D / m1;
		double a = Math.atan(m2);
		double dX = Math.cos(a) * radius;
		double dY = Math.sin(a) * radius;
		x[0] = (int) (x1 + dX);
		y[0] = (int) (y1 + dY);
		x[1] = (int) (x2 + dX);
		y[1] = (int) (y2 + dY);
		x[2] = (int) (x2 - dX);
		y[2] = (int) (y2 - dY);
		x[3] = (int) (x1 - dX);
		y[3] = (int) (y1 - dY);
		p = new Polygon(x,y,4);
	}
	public double getAngle(){
		double xD;		
		double yD;
		if(y[1] > y[0]){
			yD = y[1] - y[0];
			xD = x[1] - x[0];
		}
		else{
			yD = y[0] - y[1];
			xD = x[0] - x[1];
		}
		return -1 * Math.atan2(yD, xD);

	}

}
