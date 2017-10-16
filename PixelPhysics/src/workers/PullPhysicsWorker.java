package workers;

import java.util.Random;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class PullPhysicsWorker implements Runnable{

	Particle[] array;
	double baseX;
	double baseY;
	double mult;
	static Random rand = new Random();
	public PullPhysicsWorker(final double x, final double y,final Particle[] p,final double mult){
		baseX = x;
		baseY = y;
		array = p;
		this.mult = mult * Properties.timeSpeed * Properties.pullStrength;
	}

	public void get(){
		
	}
	@Override
	public void run() {
		try{
		for (Particle element : array) {
			Particle p = element;
			//TODO XXX getting null Particles for some reason :(
			if(p == null){
				continue;
			}
			double dist = GamePanel.getDistance(p.x, p.y, baseX, baseY);
			double random = (Math.random() - 0.5) / 10;
			double deltaX = (p.x - baseX) / dist;
			double deltaY = (p.y - baseY) / dist;
			deltaX += random*deltaX;
			deltaX += random*deltaY;
			p.speedX -= deltaX * mult;
			p.speedY -= deltaY * mult;
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

}
