package workers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import stuff.Particle;

public class GraphicsWorker implements Callable<BufferedImage>{

	ArrayList<Particle> array;
	int w;
	int h;
	int size;
	public GraphicsWorker(ArrayList<Particle> array,int w,int h,int size){
		this.array = array;
		this.w = w;
		this.h = h;
		this.size = size;
	}
	@Override
	public BufferedImage call() throws Exception {
		BufferedImage buffImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics gg = buffImage.getGraphics();
		for(int i = 0; i < array.size();i++){
			Particle p = array.get(i);
			gg.setColor(p.color);
			gg.fillRect((int)p.x, h - (int)p.y,size,size);
		}
		return buffImage;
	}

}
