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
	int[][] reds;
	int[] RGB = new int[3];
	int[][] RGBs;
	int w;
	//	public BlobWorker(ArrayList<Particle> p, int[][] r, int[][] g, int[][] b,int w, int h, double gr, int[] RGB,boolean type,double gs){
	//		particleArray = p;
	//		glowRadius = gr;
	//		glowStrength = gs;
	//		this.w = w;
	//		this.h = h;
	//		for(int i = 0; i < RGB.length;i++){
	//			this.RGB[i] = RGB[i];
	//		}
	//		reds = r;
	//		greens = g;
	//		blues = b;
	//		compound = type;
	//	}
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
	@Override
	public void run(){
		final double bounds = glowRadius / 2;
		for(int i = 0; i < particleArray.size();i++){
			final Particle p = particleArray.get(i);
			if(p.x < 0 - bounds|| p.x > w + bounds || p.y < 0 - bounds || p.y > h + bounds)
				continue;

			int minX = (int) (p.x - glowRadius);
			int minY = (int) (p.y - glowRadius);
			int maxX = (int) (p.x + glowRadius);
			int maxY = (int) (p.y + glowRadius);
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
			double dist = 0;
			for(int x = minX; x < maxX;x++)
				for(int y = minY; y < maxY;y++){
					if(Properties.diamondGlow)
						dist = GamePanel.getDiamondDistance(x, y, p.x, p.y);
					else
						dist = GamePanel.getDistance(x, y, p.x, p.y);
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
