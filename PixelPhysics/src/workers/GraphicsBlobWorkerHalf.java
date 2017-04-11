package workers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import main.Properties;
import stuff.Bounds;

public class GraphicsBlobWorkerHalf implements Callable<BufferedImage> {
	Bounds bounds;
	int h;
	int[][] RGBs;
	int w;
	int x;
	int y;
	static int mult = 2;
	public GraphicsBlobWorkerHalf( int x,  int y, int w,  int h,  int[][] RGBs, Bounds b){
		this.RGBs = RGBs;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = y;
		bounds = b;
	}
	@Override
	public BufferedImage call() throws Exception {
		BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics gr = image.getGraphics();
		gr.fillRect(image.getWidth() - 60, image.getHeight() - 60, image.getWidth(), image.getHeight());
		for(int x = this.x; x < w;x++)
			for(int y = this.y; y < h;y++){
//					if(x < bounds.minX && x > bounds.maxX && y < bounds.minY && y > bounds.maxY)
//					continue;
				int rgb1 = RGBs[x][y];
				int r = rgb1 >> 16 & 0XFF;
			int g = rgb1 >> 8 & 0XFF;
			int b = rgb1 & 0XFF;
			if(r > 255)
				r = 255;
			if(b > 255)
				b = 255;
			if(g > 255)
				g = 255;
			if(r > 5 || g > 5 || b > 5){
				int rgb2 = r << 16 | g << 8 | b ;
				image.setRGB(x - this.x, y - this.y, rgb2);

				if(!Properties.paint)
					RGBs[x][y] = 0;
				else{
					int f = Properties.glowPaintValue;
					r -= f;
					b -= f;
					g -= f;
					RGBs[x][y] = r << 16 & 0XFF0000 | g << 8 & 0XFF00 | b;

				}
			}
			}
		return image;

	}

}
