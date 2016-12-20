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
			if(p.x > 0 && p.y > 0 && p.x < 1920 && p.y < 1080)
			trails.add(new ParticleTrail(p.snapshotVector,GamePanel.getParticleColor(p)));
		}
		return trails;
	}


}
