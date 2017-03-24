package workers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import main.GamePanel;
import stuff.Particle;
import stuff.ParticleTrail;

public class PaintTrailWorker implements Callable<ArrayList<ParticleTrail>>
{

	List<Particle> a;
	public PaintTrailWorker(List<Particle> list){
		a = list;
	}

	@Override
	public ArrayList<ParticleTrail> call() throws Exception {
		ArrayList<ParticleTrail> trails = new ArrayList<ParticleTrail>();
		for(int i = 0; i < a.size();i++){
			Particle p = a.get(i);
			if(p.getX() > 0 && p.getY() > 0 && p.getX() < 1920 && p.getY() < 1080){
				if(p.snapshotVector == null){
					continue;
				}
				ParticleTrail pt = new ParticleTrail(p.snapshotVector,Particle.getParticleColor(p),p.getWidth(),p.getHeight());
				trails.add(pt);
			}
		}
		return trails;
	}


}
