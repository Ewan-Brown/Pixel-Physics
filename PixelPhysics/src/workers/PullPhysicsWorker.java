package workers;

import main.GamePanel;
import main.Properties;
import stuff.Particle;

public class PullPhysicsWorker implements Runnable{

	Particle[] array;
	double baseX;
	double baseY;
	double mult;
	public PullPhysicsWorker(final double x, final double y,final Particle[] p,final double mult){
		baseX = x;
		baseY = y;
		array = p;
		this.mult = mult * Properties.timeSpeed * Properties.pullStrength;
	}

	@Override
	public void run() {
		for (final Particle element : array) {
			final Particle p = element;
			//TODO XXX getting NullPointers here for no reason?
			final double dist = GamePanel.getDistance(p.x, p.y, baseX, baseY);
			final double deltaX = (p.x - baseX) / dist;
			final double deltaY = (p.y - baseY) / dist;
			p.speedX -= deltaX * mult;
			p.speedY -= deltaY * mult;
		}

	}

}
