package stuff;

import java.util.ArrayList;

public class Bounds {

	public int minX = 1920;
	public int maxX = 0;
	public int minY = 1080;
	public int maxY = 0;

	public Bounds(ArrayList<Particle> pA){
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			int x = (int)p.x;
			int y = (int)p.y;
			if(x < minX){
				minX = x;
			}
			if(y < minY){
				minY = y;
			}
			if(x > maxX){
				maxX = x;
			}
			if(y > maxY){
				maxY = y;
			}
		}
	}

}
