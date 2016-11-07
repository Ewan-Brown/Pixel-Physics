package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import stuff.Bounds;
import stuff.Particle;
import workers.BlobWorker;
import workers.GraphicsBlobWorker;
import workers.GraphicsWorker;
import workers.PackingWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel implements MouseListener,KeyListener,ActionListener{

	/**
	 * 
	 */
	private ExecutorService executorPhysics = Executors.newCachedThreadPool();
	private ExecutorService executorGraphics = Executors.newCachedThreadPool();
	private ExecutorService executorBlobs = Executors.newCachedThreadPool();
	private static final long serialVersionUID = 1L;
	boolean lmbHeld = false;
	boolean rmbHeld = false;
	public final int maxTimer = 30;
	public int[] cooldowns = new int[6];
	public int maxPixels;
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public double gravityStrength = 0.007;
	public double[] frictions = {0.0001,0.01,0.1};
	public double[] pulls = {.01,1,5};
	public double frictionStrength = frictions[1];
	public double pullStrength = pulls[1];
	public int cores = 1;
	public double timeSpeed = 1;
	public long updateDelay = 10;
	public double lastLag1 = 0;
	public double lastLag2 = 0;
	public double lastLag3 = 0;
	public int[] RGB = new int[3];
	public int RGB_switch = 1;
	public boolean flag = false;
	public boolean compound = true;
	int shiftAmount = 1;
	double glowRadius = 10;
	double glowStrength = 100;
	boolean glow = false;
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	int size = 1;
	int maxSize = 10;
	int[][] reds = new int[1920][1080];
	int[][] blues = new int[1920][1080];
	int[][] greens = new int[1920][1080];
	DecimalFormat df = new DecimalFormat("0.00");
	//	private OptionPanel option;
	public GamePanel(int w,int h,int m,int s){
		cores = Runtime.getRuntime().availableProcessors();
		maxPixels = m;
		//		maxPixels = 100;
		size = s;
		for(int i = 0; i < RGB.length;i++){
			RGB[i] = rand.nextInt(255);
		}
		Dimension d = new Dimension(w,h);
		setPreferredSize(d);
		this.setFocusable(true);
	}
	public void switchShift(){
		RGB_switch = rand.nextInt(3);
		shiftAmount = -1;
		if(getPositive(RGB_switch)){
			shiftAmount = 1;
		}
	}
	public boolean getPositive(int index){
		if(RGB[index] < 127){
			return true;
		}
		return false;
	}
	public void shiftColor(){
		RGB[RGB_switch] += shiftAmount;
		if(RGB[RGB_switch] > 255){
			RGB[RGB_switch] = 255;
			switchShift();
		}
		else if(RGB[RGB_switch] < 1){
			RGB[RGB_switch] = 1;
			switchShift();
		}
		else if(rand.nextInt(100) < 1){
			switchShift();
		}
	}
	public void init(){
		addMouseListener(this);
		int a = maxPixels;
		addKeyListener(this);
		setBackground(Color.BLACK);
		for(int i = 0; i < a;i++){
			spawnify();
		}
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		long t0 = System.nanoTime();
		if(glow){
			drawBlobsWithWorkers(g);
//			drawBlobs(g);
		}
		else{
			drawParticles(g);
		}
		long t1 = System.nanoTime();
		lastLag1 = (t1 - t0) / 1000000D;
		double fps = (16D / (double)lastLag1) * 60;
		g.setColor(Color.WHITE);
		g.drawString(df.format(lastLag1) + " (" +(int)fps+ ") - " + df.format(lastLag2) + " - " + df.format(lastLag3),0, 20);
		g.drawString(RGB[0] + " " + RGB[1] + " " + RGB[2] + " " + glowStrength, getWidth() / 2, (getHeight() / 2));
		g.drawString(frictionStrength + " - " + pullStrength + " - " + size + " - " + timeSpeed + " - " + df.format(lastLag1) + " - " + df.format(lastLag2) + " - " + df.format(lastLag3), getWidth() / 2, (getHeight() / 2) + 20);	
	}
	public void update(){
		glowRadius = (size * 2);
		this.updateParticles();
		this.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if(flag){
			shiftColor();
		}
	}

	public void drawBlobs(Graphics gg){
		int w = getWidth();
		int h = getHeight();
		double bounds = glowRadius / 2;
		long t0 = System.nanoTime();
		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			if(p.x < 0 - bounds|| p.x > w + bounds || p.y < 0 - bounds || p.y > h + bounds){
				continue;
			}
			int minX = (int) (p.x - glowRadius);
			int minY = (int) (p.y - glowRadius);
			int maxX = (int) (p.x + glowRadius);
			int maxY = (int) (p.y + glowRadius);
			if(minX < 0){
				minX = 0;
			}
			if(minY < 0){
				minY = 0;
			}
			if(maxX > w){
				maxX = w;
			}
			if(maxY > h){
				maxY = h;
			}
			for(int x = minX; x < maxX;x++){
				for(int y = minY; y < maxY;y++){
					double dist = getDistance(x, y, p.x, p.y);
					if(dist > glowRadius){
						continue;
					}
					double a = Math.floor(100 - (100 * (dist / glowRadius)));
					if(a < 0){
						a = 0;
					}
					double r = (int)(a * RGB[0]) / 100;					
					double g = (int)(a * RGB[1]) / 100;
					double b = (int)(a * RGB[2]) / 100;

					reds[x][y] = (int) r;
					greens[x][y] = (int) g;
					blues[x][y] = (int) b;
				}
			}
		}
		long t1 = System.nanoTime();
		int r;
		int g;
		int b;
		for(int x = 0; x < w;x++){
			for(int y = 0; y < h;y++){
				r = reds[x][y];
				g = greens[x][y];
				b = blues[x][y];
				reds[x][y] = 0;
				greens[x][y] = 0;
				blues[x][y] = 0;
				if(r > 255){
					r = 255;
				}
				if(b > 255){
					b = 255;
				}
				if(g > 255){
					g = 255;
				}
				if(r > 2 || g > 2 || b > 2){
					gg.setColor(new Color(r,g,b));
					gg.fillRect(x, y, 1, 1);
				}
			}
		}
//		long t2 = System.nanoTime();
//		lastLag2 = (t1 - t0) / 1000000D;
//		lastLag3 = (t2 - t1) / 1000000D;
	}
	public void drawBlobsWithWorkers(Graphics gg){
		int qr = particleArray.size() / 4;
		int hf = particleArray.size() / 2;
		int fl = particleArray.size();
		int w = getWidth();
		int h = getHeight();
		long t0 = System.nanoTime();
		Bounds bounds = new Bounds(particleArray);
//		Future<?> w1 = executorBlobs.submit(new BlobWorker(new ArrayList<Particle>(particleArray.subList(0, qr)),reds,greens,blues,w,h,glowStrength,RGB,compound));
//		Future<?> w2 = executorBlobs.submit(new BlobWorker(new ArrayList<Particle>(particleArray.subList(qr, hf)),reds,greens,blues,w,h,glowStrength,RGB,compound));
//		Future<?> w3 = executorBlobs.submit(new BlobWorker(new ArrayList<Particle>(particleArray.subList(hf, hf+qr)),reds,greens,blues,w,h,glowStrength,RGB,compound));
//		Future<?> w4 = executorBlobs.submit(new BlobWorker(new ArrayList<Particle>(particleArray.subList(hf+qr, fl)),reds,greens,blues,w,h,glowStrength,RGB,compound));
		Future<?>[] blobWorkers = new Future<?>[cores];
		int splitSize = particleArray.size() / cores;
		for(int i = 0; i < cores;i++){
			int k = i * splitSize;
			blobWorkers[i] = executorBlobs.submit((new BlobWorker(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)),reds,greens,blues,w,h,glowRadius,RGB,compound,glowStrength)));
		}
		boolean finished = false;
		do{
			finished = true;
			for(int i = 0; i < blobWorkers.length;i++){
				if(!blobWorkers[i].isDone()){
					finished = false;
				}
			}
		}while(!finished);
		long t1 = System.nanoTime();
		Future<BufferedImage> g1 = executorGraphics.submit(new GraphicsBlobWorker(0,0,w / 2,h / 2,reds,greens,blues,bounds));
		Future<BufferedImage> g2 = executorGraphics.submit(new GraphicsBlobWorker(w/2,0,w,h/2,reds,greens,blues,bounds));
		Future<BufferedImage> g3 = executorGraphics.submit(new GraphicsBlobWorker(0,h/2,w/2,h,reds,greens,blues,bounds));
		Future<BufferedImage> g4 = executorGraphics.submit(new GraphicsBlobWorker(w/2,h/2,w,h,reds,greens,blues,bounds));
		do{

		}while(! (g1.isDone() && g2.isDone() && g3.isDone() && g4.isDone()));
		BufferedImage b1 = null;
		BufferedImage b2 = null;
		BufferedImage b3 = null;
		BufferedImage b4 = null;
		try {
			b1 = g1.get();
			b2 = g2.get();
			b3 = g3.get();
			b4 = g4.get();

		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gg.drawImage(b1, 0, 0, this);
		gg.drawImage(b2, w/2, 0, this);
		gg.drawImage(b3, 0, h/2, this);
		gg.drawImage(b4, w/2, h/2, this);
		//		for(int x = 0; x < w;x++){
		//			for(int y = 0; y < h;y++){
		//				if(reds[x][y] > 255){
		//					reds[x][y] = 255;
		//				}
		//				if(blues[x][y] > 255){
		//					blues[x][y] = 255;
		//				}
		//				if(greens[x][y] > 255){
		//					greens[x][y] = 255;
		//				}
		//				if(reds[x][y] < 0){
		//					reds[x][y] = 0;
		//				}
		//				if(blues[x][y] < 0){
		//					blues[x][y] = 0;
		//				}
		//				if(greens[x][y] < 0){
		//					greens[x][y] = 0;
		//				}
		//				if(reds[x][y] > 5 || greens[x][y] > 5 || blues[x][y] > 5){
		//					gg.setColor(new Color(reds[x][y],greens[x][y],blues[x][y]));
		//					gg.fillRect(x, h - y, 1, 1);
		//				}
		//			}
		//		}
		long t2 = System.nanoTime();
		lastLag2 = (t1 - t0) / 1000000D;
		lastLag3 = (t2 - t1) / 1000000D;

	}
	public void drawParticles(Graphics g){
		g.setColor(new Color(RGB[0],RGB[1],RGB[2]));
		long t0 = System.nanoTime();

		ArrayList<Particle> pA = packify(particleArray);

		long t1 = System.nanoTime();
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			g.fillRect((int)p.x ,(int)p.y, size,size);
		}
		long t2 = System.nanoTime();
		lastLag1 = (double)(t1 - t0) / 1000000D;
		lastLag2 = (double)(t2 - t1) / 1000000D;
	}
	public BufferedImage getImage(ArrayList<Particle> pA){
		BufferedImage buffImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics gg = buffImage.getGraphics();		
		gg.setColor(Color.BLUE);
		for(int i = 0; i < pA.size();i++){
			Particle p  = pA.get(i);
			//			gg.setColor(p.color);
			gg.fillRect((int)p.x ,(getHeight() - (int)p.y), size,size);
		}
		return buffImage;

	}
	public BufferedImage[] getImageWithWorker(ArrayList<Particle> pA){
		BufferedImage[] buffImages = new BufferedImage[4];
		int q = pA.size() / 4;
		int h = pA.size() / 2;
		int f = pA.size();
		int width = getWidth();
		int height = getHeight();
		int r =RGB[0];
		int g = RGB[1];
		int b = RGB[2];
		Future<BufferedImage> w1 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size,r,g,b));
		Future<BufferedImage> w2 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(h, f))),width,height,size,r,g,b));
		Future<BufferedImage> w3 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size,r,g,b));
		Future<BufferedImage> w4 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size,r,g,b));


		do{

		}while(!w1.isDone() && !w2.isDone() && !w3.isDone() && !w4.isDone());

		try {
			buffImages[0] = w1.get();
			buffImages[1] = w2.get();
			buffImages[2] = w3.get();
			buffImages[3] = w4.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return buffImages;

	}
	//TODO When multiple particle are in the same spot it swaps between which one it draws, and when different colors = flickering :(
	//TODO With packing, particles are non-existant? wut
	public ArrayList<Particle> packify(ArrayList<Particle> pA){
		boolean[][] occupiedArray = new boolean[1920][1080];
		ArrayList<Particle> newP = new ArrayList<Particle>();
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			int x = (int)p.x;
			int y = (getHeight() - (int)p.y);
			if(x > 0 && x < getWidth() && y > 0 && y < getHeight()){
				if(!occupiedArray[x][y]){
					occupiedArray[x][y] = true;
					newP.add(p);
				}
			}
		}
		return newP;
	}
	public ArrayList<Particle> packifyWithWorker(ArrayList<Particle> pA){
		//XXX Strange casts and could be loops stuff here. Please fix!
		//TODO find out if 4 threads is really faster than just 2
		int q = pA.size() / 4;
		int h = pA.size() / 2;
		int f = pA.size();
		boolean[][] oA = new boolean[1920][1080];
		Future<ArrayList<Particle>> w1 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(0, q)),getWidth(),getHeight(),oA));
		Future<ArrayList<Particle>> w2 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(q, h)),getWidth(),getHeight(),oA));
		Future<ArrayList<Particle>> w3 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(h , f - q)),getWidth(),getHeight(),oA));
		Future<ArrayList<Particle>> w4 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(f - q, f)),getWidth(),getHeight(),oA));

		do{

		}while(!w1.isDone() && !w2.isDone() && !w3.isDone() && !w4.isDone());

		ArrayList<Particle> p1 = null;
		ArrayList<Particle> p2 = null;
		ArrayList<Particle> p3 = null;
		ArrayList<Particle> p4 = null;

		try {
			p1 = w1.get();
			p2 = w2.get();
			p3 = w3.get();
			p4 = w4.get();

			p1.addAll(p2);
			p1.addAll(p3);
			p1.addAll(p4);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return p1;
	}
	public ArrayList<Particle> getParticles(){
		return particleArray;
	}
	public void updateParticles(){

		for(int i = 0; i < pullQueue.size();i++){
			Point2D p = pullQueue.get(i);

			//			pull(p.getX(),p.getY(),1);
			pullWithWorkers(p.getX(),p.getY(),1);

			pullQueue.remove(i);
		}
		for(int i = 0; i < pushQueue.size();i++){
			Point2D p = pushQueue.get(i);

			pullWithWorkers(p.getX(),p.getY(),-1);

			pushQueue.remove(i);
		}


		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			moveify(p);
			//			gravitify(p);
			frictionify(p);
		}

	}
	//	public void pull(double x, double y, double mult){
	//		double angle = 1;	
	//		double deltaX;
	//		double deltaY;
	//		mult *= timeSpeed * pullStrength;
	//		for(int i = 0; i < particleArray.size();i++){
	//			Particle p = particleArray.get(i);
	//			angle = FastMath.atan2((float)(p.y - y), (float)(p.x - x));
	//			deltaX = net.jafama.FastMath.cos(angle);
	//			deltaY = net.jafama.FastMath.sin(angle);
	//			p.speedX -= deltaX * mult;							
	//			p.speedY -= deltaY * mult;      					 
	//		}
	//	}
	public void pullWithWorkers(double x, double y, double mult){
		int q = particleArray.size() / 4;
		int h = particleArray.size() / 2;
		int f = particleArray.size();
		Particle[] p1 = particleArray.subList(0, q).toArray(new Particle[particleArray.size()]);
		Particle[] p2 = particleArray.subList(q, h).toArray(new Particle[particleArray.size()]);
		Particle[] p3 = particleArray.subList(h, h+q).toArray(new Particle[particleArray.size()]);
		Particle[] p4 = particleArray.subList(h+q, f).toArray(new Particle[particleArray.size()]);

		executorPhysics.submit(new PullPhysicsWorker(x,y,p1,mult * timeSpeed * pullStrength));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p2,mult * timeSpeed * pullStrength));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p3,mult * timeSpeed * pullStrength));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p4,mult * timeSpeed * pullStrength));

	}

	public void spawnify(int x, int y, double vx, double vy){
		Particle p = new Particle(x,y,vx,vy,size);
		p.color = GamePanel.randomColor();
		particleArray.add(p);
	}
	public void spawnify(int x, int y){
		spawnify(x,y,(rand.nextFloat() - 0.5) * 10,(rand.nextFloat() - 0.5) * 10);
	}
	public void spawnify(){
		spawnify(rand.nextInt(getWidth()),rand.nextInt(getHeight()));
	}
	public void gravitify(Particle p){
		p.speedY -= gravityStrength;
	}
	public void frictionify(Particle p){
		p.speedX -= p.speedX * frictionStrength;
		p.speedY -= p.speedY * frictionStrength;

	}
	public void moveify(Particle p){
		p.x += p.speedX * timeSpeed;
		p.y += p.speedY * timeSpeed;
		//		p.x += p.tempSpeedX * timeSpeed;
		//		p.y += p.tempSpeedY * timeSpeed;
		//		p.tempSpeedY = 0;
		//		p.tempSpeedX = 0;
	}
	public boolean areCollidifying(Particle p1, Particle p2){
		double diffX = Math.abs(p1.x - p2.x);
		double diffY = Math.abs(p1.y - p2.y);
		if(diffX < (size + size) / 2 && diffY < (size + size) / 2){
			return true;
		}
		return false;
	}
	public void updateThemkeys(){
		for(int i = 0 ; i < cooldowns.length; i++){
			cooldowns[i]--;
			if(cooldowns[i] < 0){
				cooldowns[i] = 0;
			}
		}
		if(keySet.get(KeyEvent.VK_UP)){
			timeSpeed += 0.01;
		}
		if(keySet.get(KeyEvent.VK_DOWN)){
			timeSpeed -= 0.01;
			if(timeSpeed < 0.01){
				timeSpeed = 0.01;
			}
		}
		if(keySet.get(KeyEvent.VK_RIGHT)){
			timeSpeed = 0.5;
		}
		if(keySet.get(KeyEvent.VK_LEFT)){
			timeSpeed = 0;
		}
		//		if(keySet.get(KeyEvent.VK_T)){
		//			if(cooldowns[0] == 0){
		//				lowPerformance = !lowPerformance;
		//			cooldowns[0] = maxTimer;
		//			}
		//		}

		if(keySet.get(KeyEvent.VK_W)){
			if(Math.abs(pullStrength) - 1 <= 0){
				pullStrength += 0.003;
			}
			else{
				pullStrength += Math.abs(pullStrength) / 100D;
			}
			if(pullStrength > pulls[2]){
				pullStrength = pulls[2];
			}
		}
		if(keySet.get(KeyEvent.VK_R)){
			pullStrength = pulls[1];
		}
		if(keySet.get(KeyEvent.VK_F)){
			frictionStrength = frictions[1];
		}

		if(keySet.get(KeyEvent.VK_S)){
			if(Math.abs(pullStrength) - 1 <= 0){
				pullStrength -= 0.003;
			}
			else{
				pullStrength -= Math.abs(pullStrength) / 100D;
			}
			if(pullStrength < pulls[0]){
				pullStrength = pulls[0];
			}

		}
		if(keySet.get(KeyEvent.VK_A)){
			frictionStrength -= frictionStrength / 50D;
			if(frictionStrength < frictions[0]){
				frictionStrength = frictions[0];
			}
		}
		if(keySet.get(KeyEvent.VK_D)){
			frictionStrength += frictionStrength / 50D;
			if(frictionStrength > frictions[2]){
				frictionStrength = frictions[2];
			}
		}
		if(keySet.get(KeyEvent.VK_COMMA)){
			if(cooldowns[0] == 0){
				cooldowns[0] = maxTimer;
				size--;
				if(size < 1){
					size = 1;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_PERIOD)){
			if(cooldowns[1] == 0){
				cooldowns[1] = maxTimer;
				size++;
				if(size > maxSize){
					size = maxSize;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_G)){
			if(cooldowns[2] == 0){
				cooldowns[2] = maxTimer;
				glow = !glow;
			}
		}
		if(keySet.get(KeyEvent.VK_J)){
			if(cooldowns[3] == 0){
				cooldowns[3] = maxTimer;
				compound = !compound;
			}
		}
		if(keySet.get(KeyEvent.VK_L)){
			if(cooldowns[4] == 0){
				cooldowns[4] = maxTimer / 8;
				glowStrength -= 2;
				if(glowStrength < 2){
					glowStrength = 2;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_O)){
			if(cooldowns[5] == 0){
				cooldowns[5] = maxTimer / 8;
				glowStrength += 2;
				if(glowStrength > 255){
					glowStrength = 255;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_H)){
			RGB[0] = 255;
			RGB[1] = 70;
			RGB[2] = 0;
		}
	}
	public void doMouse(){
		if(lmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			pullQueue.add(new Point(x,y));
		}
		else if(rmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) ((int) p.getY() + this.getLocationOnScreen().getY()) - 57;
			pushQueue.add(new Point(x,y));
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			lmbHeld = true;
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			rmbHeld = true;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keySet.set(e.getKeyCode(),true);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		keySet.set(e.getKeyCode(),false);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		lmbHeld = false;
		rmbHeld = false;
	}
	@Override
	public void mouseExited(MouseEvent e) {
		lmbHeld = false;
		rmbHeld = false;
	}
	public static Color randomColor(){
		Random rand = new Random();
		int[] rgb = new int[3];
		rgb[0] = rand.nextInt(255);
		rgb[1] = rand.nextInt(255);
		rgb[2] = rand.nextInt(255);
		int f = rgb[0] + rgb[1] + rgb[2];
		//		if(f < 1750){
		//			int c = rand.nextInt(3);
		//			System.out.println(c);
		//			for(int i = 0 ; i < 2;i++){
		//				if(i != c){
		//					rgb[i] -= 50;
		//					if(rgb[i] < 0){
		//						rgb[i] = 0;
		//					}
		//				}
		//			}
		//		}
		return new Color(rgb[0],rgb[1],rgb[2]);
	}
	public static double getDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
		int x = rand.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

}
