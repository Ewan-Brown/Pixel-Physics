package workers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import stuff.Bounds;

public class GraphicsBlobWorker implements Callable<BufferedImage> {
	int w;
	int h;
	int x;
	int y;
	int[][] reds;
	int[][] blues;
	int[][] greens;
	Bounds bounds;
	public GraphicsBlobWorker(int x, int y,int w, int h, int[][] reds,int[][] greens,int[][] blues,Bounds b){
		this.w = w;
		this.h = h;
		this.reds = new int[w][h];
		this.blues = new int[w][h];
		this.greens = new int[w][h];
		this.reds = reds;
		this.blues = blues;
		this.greens = greens;
		this.x = x;
		this.y = y;
		bounds = b;
	}
	public BufferedImage call() throws Exception {
		BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics gg = image.getGraphics();
		for(int x = this.x; x < w;x++){
			for(int y = this.y; y < h;y++){
				if(x < bounds.minX && x > bounds.maxX && y < bounds.minY && y > bounds.maxY){
					continue;
				}
				int r = reds[x][y];
				int g = greens[x][y];
				int b = blues[x][y];
				reds[x][y] = 0;
				greens[x][y] = 0;
				blues[x][y] = 0;
				if(r > 255){
					r = 255;
				}
				if(b > 255){
					b = 255;
				}
				if(g > 255){
					g = 255;
				}
				if(r < 0){
					r = 0;
				}
				if(b < 0){
					b = 0;
				}
				if(g < 0){
					g = 0;
				}
				if(r > 5 || g > 5 || b > 5){
					int rgb = r;
					rgb = (rgb << 8) + g;
					rgb = (rgb << 8) + b;
					image.setRGB(x - this.x, y - this.y, rgb);
				}
			}
		}
		return image;

	}

}
