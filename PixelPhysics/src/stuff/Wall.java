package stuff;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import main.Properties;

public class Wall {
	static double radius = 2;
	boolean flag = false;
	public int[] oX = new int[2];
	public int[] oY = new int[2];
	public Polygon p;
	public int[] x = new int[4];
	public int[] y = new int[4];
	public Wall(final double x1, final double y1, final double x2, final double y2){
		final double m1 = (y2 - y1) / (x2 - x1);
		final double m2 = -1D / m1;
		final double a = Math.atan(m2);
		final double dX = Math.cos(a) * radius;
		final double dY = Math.sin(a) * radius;
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
	public boolean isColliding(final Particle b){
		final Rectangle2D rect = new Rectangle2D.Double(b.x, b.y, Properties.size,Properties.size);
		return p.intersects(rect);
	}

}
