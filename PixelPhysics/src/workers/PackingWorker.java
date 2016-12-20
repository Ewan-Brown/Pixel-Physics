package workers;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import stuff.Particle;

public class PackingWorker implements Callable<ArrayList<Particle>>{


	int h;
	ArrayList<Particle> newP = new ArrayList<Particle>();
	boolean[][] occupiedArray = new boolean[1920][1080];
	ArrayList<Particle> pA;
	int w;
	public PackingWorker(final ArrayList<Particle> pA,final int w,final int h,final boolean[][] oa){
		occupiedArray = oa;
		this.pA = pA;
		this.w = w;
		this.h = h;
	}
	@Override
	public ArrayList<Particle> call() throws Exception {
		for(int i = 0; i < pA.size();i++){
			final Particle p = pA.get(i);
			final int x = (int)p.x;
			final int y = h - (int)p.y;
			if(x > 0 && x < w && y > 0 && y < h)
				if(!occupiedArray[x][y]){
					occupiedArray[x][y] = true;
					newP.add(p);
				}
		}
		return newP;
	}
	public ArrayList<Particle> getArray(){
		return newP;

	}
	public void run() {

	}

}
