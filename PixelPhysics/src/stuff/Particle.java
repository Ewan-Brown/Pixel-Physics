package stuff;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

import main.GamePanel;
import main.Properties;
import main.Properties.sizeType;

public class Particle {

	public Random rand = new Random();
	private Color color = Color.GRAY;
	public Line2D snapshotVector;
	public double speedX;
	public double speedY;
	public double x;
	public double y;
	private int size = rand.nextInt(Properties.maxSize);
	private int width = 1;
	private int height = 1;
	public Particle(final int x, final int y, final double speedX,final double speedY,final int size){
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	public void update(){
		width += rand.nextInt(3) - 1;
		height += rand.nextInt(3) - 1;
		if(width < 1){
			width = 1;
		}
		if(height < 1){
			height = 1;
		}
		if(width > Properties.maxSize){
			width = Properties.maxSize;
		}
		if(height > Properties.maxSize){
			height = Properties.maxSize;
		}
	}
	public int getWidth(){
		if(Properties.sizingType == sizeType.ALL){
			return Properties.size;
		}
		else if(Properties.sizingType == sizeType.INDIVIDUAl){
			return size;
		}
		else{
			return width;
		}
	}
	public int getHeight(){
		if(Properties.sizingType == sizeType.ALL){
			return Properties.size;
		}
		else if(Properties.sizingType == sizeType.INDIVIDUAl){
			return size;
		}
		else{
			return height;
		}
	}
	public static Color getParticleColor(Particle p){
		int r = 0;
		int g = 0;
		int b = 0;
		int[] r2 = new int[7];
		int[] g2 = new int[7];
		int[] b2 = new int[7];
		int modesOn = 0;
		if(Properties.singleColor){
			modesOn++;
			r2[0] = Properties.RGB[0];
			g2[0] = Properties.RGB[1];
			b2[0] = Properties.RGB[2];

		}
		if(Properties.rainbowColor){
			modesOn++;
			Color c = p.getColor();
			r2[1] = c.getRed();
			g2[1] = c.getGreen();
			b2[1] = c.getBlue();
		}
		if(Properties.gridColor){
			modesOn++;
			int r2a = (int) (p.x % 600) / 5;
			r2a += (int) (p.x % 100);
			int g2a = (int) (p.y % 400) / 4;
			g2a += (int)(p.y % 50);
			int b2a = (int) (p.x % 10) * 10;
			b2a += (int) (p.y % 200) / 2;
			if(r2a < 0){
				r2a = 0;
			}
			if(g2a < 0){
				g2a = 0;
			}				
			if(b2a < 0){
				b2a = 0;
			}
			r2[2] = r2a;
			g2[2] = g2a;
			b2[2] = b2a;
		}
		if(Properties.velocityColor){
			modesOn++;
			double v = p.getVelocity();
			int r2a = (int)(-0.6*((v - 50) *(v - 50))) + 240;
			if(r2a < 0){
				r2a = 0;
			}
			r2a += 15;
			int g2a = (int)-((v - 20) *(v - 20)) + 240;
			if(g2a < 0){
				g2a = 0;
			}
			g2a += 15;
			int b2a = (int)-((v - 5) *(v - 5)) + 240;
			if(b2a < 0){
				b2a = 0;
			}
			b2a += 15;
			r2[3] = r2a;
			g2[3] = g2a;
			b2[3] = b2a;
		}
		if(Properties.directionalColor){
			modesOn++;
			double a = p.getAngle();
			double deg = (Math.toDegrees(a) + 180) % 360;
			double deg2 = (deg + 120) % 360;
			double deg3 = (deg2 + 120) % 360;
			int r2a = (int)Math.abs((deg)- 180) - 60;
			int g2a = (int)Math.abs((deg2)- 180) - 60;
			int b2a = (int)Math.abs((deg3)- 180) - 60;
			r2[4] = (int)(((double)r2a / 120D) * 255D);
			g2[4] = (int)(((double)g2a / 120D) * 255D);
			b2[4] = (int)(((double)b2a / 120D) * 255D);
		}
		if(Properties.mouseColor){
			modesOn++;
			Point2D pt = MouseInfo.getPointerInfo().getLocation();
			double x = pt.getX() - GamePanel.instance.getLocationOnScreen().getX();
			double y = pt.getY() - GamePanel.instance.getLocationOnScreen().getY();
			double d = GamePanel.getDistance(p.x, p.y, x, y);

			double r2a = 255D - 255D * (d / 500D);
			if(d > 500){
				r2a = 0;	
			}
			double g2a = 100D - 100D * (d / 1000D);
			if(d > 1000){
				g2a = 0;	
			}
			double b2a = 100D - 100D * (d / 500D);
			if(d > 500 || d < 200){
				b2a = 0;	
			}
			r2[5] = (int)r2a;
			g2[5] = (int)g2a;
			b2[5] = (int)b2a;
			
		}
		for(int i = 0; i < r2.length;i++){
			r += (int)((double)r2[i] / (double)modesOn);
			g += (int)((double)g2[i] / (double)modesOn);
			b += (int)((double)b2[i] / (double)modesOn);

		}
		if(r > 255){
			r = 255;
		}
		if(g > 255){
			g = 255;
		}
		if(b > 255){
			b = 255;
		}
		if(r < 0){
			r = 0;
		}
		if(g < 0){
			g = 0;
		}
		if(b < 0){
			b = 0;
		}
		return new Color(r,g,b);
	}
	public double getAngle(){
		return Math.atan2(-speedY, speedX);
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
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
