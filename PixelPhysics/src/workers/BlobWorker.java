package workers;

import java.util.ArrayList;

import main.GamePanel;
import stuff.Particle;

public class BlobWorker implements Runnable{

	ArrayList<Particle> particleArray;
	double glowStrength;
	int w;
	int h;
	int[] RGB = new int[3];
	int[][] reds;
	int[][] greens;
	int[][] blues;
	boolean compound;
	public BlobWorker(ArrayList<Particle> p, int[][] r, int[][] g, int[][] b,int w, int h, double gs, int[] RGB,boolean type){
		particleArray = p;
		glowStrength = gs;
		this.w = w;
		this.h = h;
		for(int i = 0; i < RGB.length;i++){
			this.RGB[i] = RGB[i];
		}
		reds = r;
		greens = g;
		blues = b;
		compound = type;
	}
	public void run(){
		double bounds = glowStrength / 2;
		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			if(p.x < 0 - bounds|| p.x > w + bounds || p.y < 0 - bounds || p.y > h + bounds){
				continue;
			}
			int minX = (int) (p.x - glowStrength);
			int minY = (int) (p.y - glowStrength);
			int maxX = (int) (p.x + glowStrength);
			int maxY = (int) (p.y + glowStrength);
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
					if(dist > glowStrength){
						continue;
					}
					double a = Math.floor(100 - (100 * (dist / glowStrength)));
					if(a < 0){
						a = 0;
					}
					double r = (int)(a * RGB[0]) / 100;					
					double g = (int)(a * RGB[1]) / 100;
					double b = (int)(a * RGB[2]) / 100;
					if(compound){
						reds[x][y] += r;
						greens[x][y] += g;
						blues[x][y] += b;
					}
					else{
						reds[x][y] = (int)r;
						greens[x][y] = (int)g;
						blues[x][y] = (int)b;
					}
				}
			}
		}
	}

}
