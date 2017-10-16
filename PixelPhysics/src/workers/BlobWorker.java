package workers;

import java.awt.Color;
import java.util.ArrayList;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class BlobWorker implements Runnable{

	int[][] blues;
	boolean compound;
	double glowRadius;
	double glowStrength = 100;
	int[][] greens;
	int h;
	boolean LSD;
	ArrayList<Particle> particleArray;
	boolean pixelized;
	int[] RGB = new int[3];
	int[][] RGBs;
	int w;

	public BlobWorker(final ArrayList<Particle> p, final int[][] RGBs,final int w, final int h){
		this.RGBs = RGBs;
		particleArray = p;
		glowRadius = Properties.size * 2;
		glowStrength = Properties.glowStrength;
		this.w = w;
		this.h = h;
		for(int i = 0; i < RGB.length;i++)
			this.RGB[i] = Properties.RGB[i];
		compound = Properties.compound;
		this.LSD = Properties.LSD;
		this.pixelized = Properties.pixelized;
	}
	static double z = 0;
	public static int intSqrt(double s) {
		if(s > z) {
			z = s;
			System.out.println(z);
		}
		int currSum = 0;
		int oddCount = 0;
		for(int i = 1; currSum + i <= s; i+=2) {
			currSum += i;
			oddCount++;
		}
		return oddCount;
	}
	@Override
	public void run(){
		for(int i = 0; i < particleArray.size();i++){
			final Particle p = particleArray.get(i);
			int minX = (int) (p.x - glowRadius);
			int minY = (int) (p.y - glowRadius);
			int maxX = (int) (p.x + glowRadius);
			int maxY = (int) (p.y + glowRadius);
			//Todo CUT OFF BASED ON PARTICLE SIZE AND USE INT SQRT
			if(minX < 0)
				minX = 0;
			if(minY < 0)
				minY = 0;
			if(maxX > w)
				maxX = w;
			if(maxY > h)
				maxY = h;
			Color c = GamePanel.getParticleColor(p);
			int r = c.getRed();
			int g = c.getGreen();
			int b = c.getBlue();
			for(int x = minX; x < maxX;x++){
				for(int y = minY; y < maxY;y++){
					double dist = 0;
					if(Properties.diamondGlow){
						dist = GamePanel.getDiamondDistance(x, y, p.x, p.y);
					}
					else if(Properties.fastSqrt) {
						int xF = (int)Math.abs(p.x - x);
						int yF = (int)Math.abs(p.y - y);
						dist = intSqrt(xF * xF + yF * yF);
					}
					else{
						dist = GamePanel.getDistance(x, y, p.x, p.y);
					}
					final int rgb = RGBs[x][y];
					int r1 = rgb >> 16 & 0XFF;
				int g1 = rgb >> 8 & 0XFF;
		int b1 = rgb & 0XFF;
		if(dist > glowRadius)
			continue;
		final double a = glowStrength - glowStrength * (dist / glowRadius);
		final double r2 = (int)(a * r) / 100;
		final double g2 = (int)(a * g) / 100;
		final double b2 = (int)(a * b) / 100;
		if(compound){
			r1 += r2;
			g1 += g2;
			b1 += b2;
		}
		else{
			r1 = (int)r2;
			g1 = (int)g2;
			b1 = (int)b2;
		}
		if(pixelized){
			r1 += x % 4 * 3 + y % 4 * 3;
			g1 += x % 4 * 3 + y % 4 * 3;
			b1 += x % 4 * 3 + y % 4 * 3;
		}
		if(!LSD){
			if(r1 > 255)
				r1 = 255;
			if(g1 > 255)
				g1 = 255;
			if(b1 > 255)
				b1 = 255;
		}
		final int rgb2 = r1 << 16 & 0XFF0000 | g1 << 8 & 0XFF00 | b1 ;
		RGBs[x][y] = rgb2;
				}
			}
		}
	}

}
