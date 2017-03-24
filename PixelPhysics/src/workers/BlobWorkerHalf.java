package workers;

import java.awt.Color;
import java.util.ArrayList;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class BlobWorkerHalf implements Runnable{

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
	public BlobWorkerHalf( ArrayList<Particle> p,  int[][] RGBs, int w,  int h){
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
	@Override
	public void run(){
		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			glowRadius = p.getHeight();
			Color c = Particle.getParticleColor(p);
			int r = c.getRed();
			int g = c.getGreen();
			int b = c.getBlue();
			for(int x = 0; x < w;x++){
				for(int y = 0; y < h;y++){
					double dist = 0;
					if(Properties.diamondGlow){
						dist = GamePanel.getDiamondDistance(x, y, p.x, p.y);
					}
					else{
						dist = GamePanel.getDistance(x, y, p.x, p.y);
					}
					int rgb = RGBs[x][y];
					int r1 = rgb >> 16 & 0XFF;
					int g1 = rgb >> 8 & 0XFF;
					int b1 = rgb & 0XFF;
					if(dist > glowRadius)
						continue;
					double a = glowStrength - glowStrength * (dist / glowRadius);
					double r2 = (int)(a * r) / 100;
					double g2 = (int)(a * g) / 100;
					double b2 = (int)(a * b) / 100;
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
					int rgb2 = r1 << 16 & 0XFF0000 | g1 << 8 & 0XFF00 | b1 ;
					RGBs[x][y] = rgb2;
				}
			}
		}
	}

}
