package stuff;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;

public class Particle {

	public Color color = Color.GRAY;
	public double x;
	public double y;
	public int width = 1;
	public int height = 1;
	public double speedX;
	public double speedY;
	public double tempSpeedY;
	public double tempSpeedX;
	public static Random rand = new Random();
	
	public Particle(int x, int y, double speedX,double speedY){
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	public void setColor(Color c){
		this.color = c;
	}
	public Point2D getPoint(){
		return new Point2D.Double(x,y);
	}
	public double getLeftSide(){
		return (this.x - (this.width - 1) / 2);
	}
	public double getRightSide(){
		return (this.x +(this.width - 1) / 2);
		
	}
	public double getTopSide(){
		return (this.y + (this.height - 1) / 2);
	}
	public double getBottomSide(){
		return(this.y -(this.height - 1) / 2);
	}
}
