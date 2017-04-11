package workers;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class PullPhysicsWorker implements Runnable{

	Particle[] array;
	double baseX;
	double baseY;
	double mult;
	public PullPhysicsWorker(double x, double y,Particle[] p,double mult){
		baseX = x;
		baseY = y;
		array = p;
		this.mult = mult * Properties.timeSpeed * Properties.doubles[Properties.PULL][1];
	}

	public void get(){
		
	}
	@Override
	public void run() {
		try{
		for (Particle p : array) {
			double dist = GamePanel.getDistance(p.x, p.y, baseX, baseY);
			double deltaX = (p.x - baseX) / dist;
			double deltaY = (p.y - baseY) / dist;
			p.speedX -= deltaX * mult;
			p.speedY -= deltaY * mult;
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

}
