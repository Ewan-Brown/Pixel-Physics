package workers;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class PlanetWorker implements Runnable{
	double mult;
	Particle[] pA;
	Particle[] all;
	public PlanetWorker(Particle[] pA,Particle[] all){
		this.pA = pA;
		this.all = all;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < pA.length; i++) {
			Particle p1 = pA[i];
			for(int j = 0 ; j < all.length;j++){
				Particle p2 = all[j];
				if(p1 != p2){
					planetify(p1,p2);
				}
			}
		}
	}
	public static void planetify(final Particle p1, final Particle p2) {
		//		Line2D vector1 = p1.lastVector;
		//		Line2D vector2 = p2.lastVector;
		//		if(getDistance(p1.x, p1.y, p2.x, p2.y) < (Properties.size * 2)){
		//			collidePlanets(p1, p2);
		//		}
		double dist = GamePanel.getDistance(p2.x, p2.y, p1.x, p1.y);
		double deltaX = (p2.x - p1.x) / dist;
		double deltaY = (p2.y - p1.y) / dist;
		double distMin = 3;
		dist = Math.max(dist, distMin);
		double mult = Properties.trueGravity;
		double g = 1D / (dist * dist);
		if (g < 0) {
			g = 0;
		}
		mult *= g;

		p2.speedX -= deltaX * Properties.getValueOfDouble("time") * mult;
		p2.speedY -= deltaY * Properties.getValueOfDouble("time") * mult;
		p1.speedX -= -deltaX * Properties.getValueOfDouble("time") * mult;
		p1.speedY -= -deltaY * Properties.getValueOfDouble("time") * mult;
	}

	
	
}
