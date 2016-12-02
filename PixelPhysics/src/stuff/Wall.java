package stuff;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import main.Properties;

public class Wall {
	static double radius = 2;
	public int[] x = new int[4];
	public int[] y = new int[4];
	public int[] oX = new int[2];
	public int[] oY = new int[2];
	public Polygon p;
	boolean flag = false;
	public Wall(double x1, double y1, double x2, double y2){
		double m1 = (y2 - y1) / (x2 - x1);
		double m2 = -1D / m1;
		double a = Math.atan(m2);
		double dX = Math.cos(a) * radius;
		double dY = Math.sin(a) * radius;
		oX[0] = (int)x1;
		oX[1] = (int)x2;
		oY[0] = (int)y1;
		oY[1] = (int)y2;
		x[0] = (int)(x1 + dX);
		y[0] = (int)(y1 + dY);
		x[1] = (int)(x2 + dX);
		y[1] = (int)(y2 + dY);
		x[2] = (int)(x2 - dX);
		y[2] = (int)(y2 - dY);
		x[3] = (int)(x1 - dX);
		y[3] = (int)(y1 - dY);
		p = new Polygon(x,y,4);
	}
	public boolean isColliding(Particle b){
		Rectangle2D rect = new Rectangle2D.Double(b.x, b.y, Properties.size,Properties.size);
		return p.intersects(rect);
	}
	public double getAngle(){
		double xD;		
		double yD;
		if(oY[1] > oY[0]){
			yD = oY[1] - oY[0];
			xD = oX[1] - oX[0];
		}
		else{
			yD = oY[0] - oY[1];
			xD = oX[0] - oX[1];
		}
		return -1 * Math.atan2(yD, xD);

	}

}
