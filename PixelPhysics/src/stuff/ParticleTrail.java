package stuff;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import main.Properties;

public class ParticleTrail {

	public Color color;
	public ArrayList<Rectangle> trail = new ArrayList<Rectangle>();
	
	public ParticleTrail(Line2D l, Color c){
		this.color = c;
		double x1 = l.getX1();
		double x2 = l.getX2();
		double y1 = l.getY1();
		double y2 = l.getY2();
		double minX = (int)Math.min(x1, x2);
		double minY = (int)Math.min(y1, y2);
		double maxX = (int)Math.max(x1, x2);
		double maxY = (int)Math.max(y1, y2);
		double m = (y2 - y1) / (x2 - x1);
		double mInverse = (x2 - x1) / (y2 - y1);
		int mult = 1;
		if(x2 < x1){
			mult = -1;
		}

		double dX = Math.abs(x2 - x1);
 		for(int i = 0; i < dX;i += 1){
 			int x = (int)x1 + (i * mult);
			int y = (int) (((double)(x - x1) * m) + y1);
			if(y < minY|| y > maxY){
				continue;
			}
			Rectangle r = new Rectangle(x,y,Properties.size,Properties.size);
			trail.add(r);
		}		
 		
		int mult2 = 1;
 		if(y2 < y1){
			mult2 = -1;
		}
		double dY = Math.abs(y2 - y1);
 		for(int i = 0; i < dY;i += 1){
 			int y = (int)y1 + (i * mult2);
			int x = (int) (((double)(y - y1) * mInverse) + x1);
			if(x < minX|| x > maxX){
				continue;
			}
			Rectangle r = new Rectangle(x,y,Properties.size,Properties.size);
			trail.add(r);
		}
	}
	
}
