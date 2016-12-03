package workers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import stuff.Particle;

public class GraphicsWorker implements Callable<BufferedImage>{

	ArrayList<Particle> array;
	int b;
	int g;
	int h;
	int r;
	int size;
	int w;
	public GraphicsWorker(final ArrayList<Particle> array,final int w,final int h,final int size,final int r, final int g, final int b){
		this.array = array;
		this.w = w;
		this.h = h;
		this.size = size;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	@Override
	public BufferedImage call() throws Exception {
		final BufferedImage buffImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < array.size();i++){
			final Particle p = array.get(i);
			int rgb = r;
			rgb = (rgb << 8) + g;
			rgb = (rgb << 8) + b;
			buffImage.setRGB((int)p.x, (int)p.y, rgb);
		}
		return buffImage;
	}

}
