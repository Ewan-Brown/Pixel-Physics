package stuff;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

public class Particle {

	public static Random rand = new Random();
	private Color color = Color.GRAY;
	public Line2D snapshotVector;
	public double speedX;
	public double speedY;
	public double x;
	public double y;
	public Particle(final int x, final int y, final double speedX,final double speedY,final int size){
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	public double getAngle(){
		return Math.atan2(-speedY, speedX);
	}
	public Color getColor(){
		
		return color;
	}
	public Point2D getPoint(){
		return new Point2D.Double(x,y);
	}
	public double getVelocity(){
		return Math.sqrt(speedX * speedX + speedY * speedY);
	}
	public void setColor(final Color c){
		this.color = c;
	}
}
