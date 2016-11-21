package workers;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class PullPhysicsWorker implements Runnable{

	double baseX;
	double baseY;
	Particle[] array;
	double mult;
	@Override
	public void run() {
		for(int i = 0; i < array.length;i++){
			Particle p = array[i];
			double dist = GamePanel.getDistance(p.x, p.y, baseX, baseY);
			double deltaX = (p.x - baseX) / dist;
			double deltaY = (p.y - baseY) / dist;
			p.speedX -= deltaX * mult;							
			p.speedY -= deltaY * mult;      					 
		}

	}

	public PullPhysicsWorker(double x, double y,Particle[] p,double mult){
		baseX = x;
		baseY = y;
		array = p;
		this.mult = mult * Properties.timeSpeed * Properties.pullStrength;
	}

}
