package workers;

import java.util.ArrayList;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class BlobWorker implements Runnable{

	ArrayList<Particle> particleArray;
	double glowRadius;
	double glowStrength = 100;
	int w;
	int h;
	int[] RGB = new int[3];
	int[][] RGBs;
	int[][] reds;
	int[][] greens;
	int[][] blues;
	boolean compound;
	boolean LSD;
	boolean pixelized;
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
	public BlobWorker(ArrayList<Particle> p, int[][] RGBs,int w, int h){
		this.RGBs = RGBs;
		particleArray = p;
		glowRadius = Properties.glowRadius;
		glowStrength = Properties.glowStrength;
		this.w = w;
		this.h = h;
		for(int i = 0; i < RGB.length;i++){
			this.RGB[i] = Properties.RGB[i];
		}
		compound = Properties.compound;
		this.LSD = Properties.LSD;
		this.pixelized = Properties.pixelized;
	}
	public void run(){
		double bounds = glowRadius / 2;
		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			if(p.x < 0 - bounds|| p.x > w + bounds || p.y < 0 - bounds || p.y > h + bounds){
				continue;
			}
			
			int minX = (int) (p.x - glowRadius);
			int minY = (int) (p.y - glowRadius);
			int maxX = (int) (p.x + glowRadius);
			int maxY = (int) (p.y + glowRadius);
			if(minX < 0){
				minX = 0;
			}
			if(minY < 0){
				minY = 0;
			}
			if(maxX > w){
				maxX = w;
			}
			if(maxY > h){
				maxY = h;
			}
			for(int x = minX; x < maxX;x++){
				for(int y = minY; y < maxY;y++){
					double dist = GamePanel.getDistance(x, y, p.x, p.y);
					//TODO less accurate but faster distances
					int rgb = RGBs[x][y];
					int r1 = (rgb >> 16) & 0XFF;
					int g1 = (rgb >> 8 ) & 0XFF;
					int b1 = (rgb) & 0XFF;
					if(dist > glowRadius){
						continue;
					}
					double a = glowStrength - (glowStrength * (dist / glowRadius));
					double r2 = (int)(a * RGB[0]) / 100;
					double g2 = (int)(a * RGB[1]) / 100;
					double b2 = (int)(a * RGB[2]) / 100;
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
						r1 += (x % 3) + (y % 3);
						g1 += (x % 3) + (y % 3);
						b1 += (x % 3) + (y % 3);
					}
					if(!LSD){
						if(r1 > 255){
							r1 = 255;
						}
						if(g1 > 255){
							g1 = 255;
						}
						if(b1 > 255){
							b1 = 255;
						}
					}
					int rgb2 = (r1 << 16) & 0XFF0000 | (g1 << 8) & 0XFF00 | b1 ;
					RGBs[x][y] = rgb2;
					//TODO RGBs are always 0 for some reason;
				}
			}
		}
	}

}
