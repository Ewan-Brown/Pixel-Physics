package stuff;

import main.FastMath;

public class PullWorkerThread implements Runnable{

	double baseX;
	double baseY;
	Particle[] array;
	double mult;
	@Override
	public void run() {
		for(int i = 0; i < array.length;i++){
			Particle p = array[i];
			double angle = FastMath.atan2((float)(p.y - baseY), (float)(p.x - baseX));
			double deltaX = net.jafama.FastMath.cosQuick(angle);
			double deltaY = net.jafama.FastMath.sinQuick(angle);
			p.speedX -= deltaX * mult;							
			p.speedY -= deltaY * mult;      					 
		}

	}

	public PullWorkerThread(double x, double y,Particle[] p,double mult){
		baseX = x;
		baseY = y;
		array = p;
		this.mult = mult;
	}

}
