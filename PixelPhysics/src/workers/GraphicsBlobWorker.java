package workers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class GraphicsBlobWorker implements Callable<BufferedImage> {
	int w;
	int h;
	int x;
	int y;
	int[][] reds;
	int[][] blues;
	int[][] greens;
	public GraphicsBlobWorker(int x, int y,int w, int h, int[][] reds,int[][] greens,int[][] blues){
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
	}
	public BufferedImage call() throws Exception {
		BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		for(int x = this.x; x < w;x++){
			for(int y = this.y; y < h;y++){
				if(reds[x][y] > 255){
					reds[x][y] = 255;
				}
				if(blues[x][y] > 255){
					blues[x][y] = 255;
				}
				if(greens[x][y] > 255){
					greens[x][y] = 255;
				}
				if(reds[x][y] < 0){
					reds[x][y] = 0;
				}
				if(blues[x][y] < 0){
					blues[x][y] = 0;
				}
				if(greens[x][y] < 0){
					greens[x][y] = 0;
				}
				if(reds[x][y] > 5 || greens[x][y] > 5 || blues[x][y] > 5){
					g.setColor(new Color(reds[x][y],greens[x][y],blues[x][y]));
					g.fillRect(x - this.x, y - this.y, 1, 1);
				}
			}
		}
		return image;
	}

}
