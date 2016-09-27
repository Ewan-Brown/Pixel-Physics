package workers;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import stuff.Particle;

public class PackingWorker implements Callable<ArrayList<Particle>>{

	
	ArrayList<Particle> pA;
	ArrayList<Particle> newP = new ArrayList<Particle>();
	boolean[][] occupiedArray = new boolean[1920][1080];
	int w;
	int h;
	public void run() {
		
	}
	public ArrayList<Particle> getArray(){
		return newP;
		
	}
	public PackingWorker(ArrayList<Particle> pA,int w,int h,boolean[][] oc){
		occupiedArray = oc;
		this.pA = pA;
		this.w = w;
		this.h = h;
	}
	@Override
	public ArrayList<Particle> call() throws Exception {
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			int x = (int)p.x;
			int y = (h - (int)p.y);
			if(x > 0 && x < w && y > 0 && y < h){
				if(!occupiedArray[x][y]){
					occupiedArray[x][y] = true;
					newP.add(p);
				}
			}
		}
		return newP;
	}

}
