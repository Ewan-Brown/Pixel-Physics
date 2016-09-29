package workers;

import main.FastMath;
import main.GamePanel;
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
//			double angle = FastMath.atan2((float)(p.y - baseY), (float)(p.x - baseX));
//			double deltaX = net.jafama.FastMath.cosQuick(angle);
//			double deltaY = net.jafama.FastMath.sinQuick(angle);
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
		this.mult = mult;
	}

}
