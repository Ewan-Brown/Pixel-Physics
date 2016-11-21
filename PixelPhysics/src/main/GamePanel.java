package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import stuff.Bounds;
import stuff.Particle;
import workers.BlobWorker;
import workers.GraphicsBlobWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel{

	/**
	 * 
	 */
	private ExecutorService executorPhysics = Executors.newCachedThreadPool();
	private ExecutorService executorGraphics = Executors.newCachedThreadPool();
	private ExecutorService executorBlobs = Executors.newCachedThreadPool();
	private static final long serialVersionUID = 1L;
	public int[] cooldowns = new int[10];
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public boolean flag = false;
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	int[][] RGBs = new int[1920][1080];
	DecimalFormat df2 = new DecimalFormat("0.00");
	DecimalFormat df3 = new DecimalFormat("0.000");
	VolatileImage paintBufferVolatile;
	VolatileImage gBuffer;
	public GamePanel(int w,int h,int m,int s){
		Properties.cores = Runtime.getRuntime().availableProcessors();
		Properties.maxPixels = m;
		Properties.size = s;
		for(int i = 0; i < Properties.RGB.length;i++){
			Properties.RGB[i] = rand.nextInt(255);
		}
		Dimension d = new Dimension(w,h);
		setPreferredSize(d);
		this.setFocusable(true);
	}

	public void init(){
		Input in = new Input();
		addMouseListener(in);
		int a = Properties.maxPixels;
		addKeyListener(in);
		setBackground(Color.BLACK);
		for(int i = 0; i < a;i++){
			spawnify();
		}
		BufferedImage img = Properties.paintImage;
		int w1 = 0,h1 = 0;
		try{
			w1 = img.getWidth();
			h1 = img.getHeight();
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(new JFrame(),"The file chosen was not a readable image! - Ending program");
			System.exit(0);
		}
		int n = w1 * h1;
		if(w1 > 1000){
			double ratio = (double)h1 / (double)w1;
			int w2 = (int)Math.sqrt(a / ratio);
			int h2 = (int)(w2 * ratio);
			double ratio2 = ((double)w2 / (double)w1);
			Properties.paintImage = MainClass.scale(img, img.getType(),w2,h2, ratio2, ratio2);
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		gBuffer = gc.createCompatibleVolatileImage(1920, 1080, Transparency.TRANSLUCENT);
		Graphics gg = gBuffer.getGraphics();
		gg.setColor(Color.BLACK);
		gg.fillRect(0, 0, 1920, 1080);
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		long t0 = System.nanoTime();
		if(Properties.glow){
			drawBlobsWithWorkers(g);
			//			drawBlobs(g);
		}
		else if(Properties.paint){
			drawParticlesPaint(g);
		}
		else{
			drawParticles(g);
		}
		long t1 = System.nanoTime();
		double lastLag1 = (t1 - t0) / 1000000D;
		double fps = (16D / lastLag1) * 60;
		g.setColor(Color.WHITE);
		g.drawString(df2.format(lastLag1) + " (" +(int)fps+")",0, 20);
		if(Properties.showStats){
			g.drawString(Properties.RGB[0] + " " + Properties.RGB[1] + " " + Properties.RGB[2] + " " + Properties.glowStrength, getWidth() / 2, (getHeight() / 2));
			g.drawString(df3.format(Properties.frictionStrength) + " - " + df2.format(Properties.pullStrength) + " - " + Properties.size + " - " + df2.format(Properties.timeSpeed) + " - " + Properties.glowPaintValue, getWidth() / 2, (getHeight() / 2) + 20);	
		}
	}
	public void doImage(){
		//Paint an image w/ particles!
		BufferedImage img = Properties.paintImage;
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		particleArray.clear();
		for(int x = 0; x < w;x++){
			for(int y = 0; y < h;y++){
				Color c = new Color(img.getRGB(x, y));
				spawnify(x + (x * (Properties.size - 1)), y + (y * (Properties.size - 1)),0,0,c);
			}
		}
		Properties.rainbow = true;
		Properties.imageFlag = false;
	}
	public void update(){
		if(Properties.imageFlag){
			doImage();
		}
		this.updateParticles();
		Input.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if(flag){
			Properties.shiftColor();
		}
	}
	public void drawBlobsWithWorkers(Graphics gg){
		int qr = particleArray.size() / 4;
		int hf = particleArray.size() / 2;
		int fl = particleArray.size();
		int w = getWidth();
		int h = getHeight();
		long t0 = System.nanoTime();
		Bounds bounds = new Bounds(particleArray);
		Future<?>[] blobWorkers = new Future<?>[Properties.cores];
		int splitSize = particleArray.size() / Properties.cores;
		for(int i = 0; i < Properties.cores;i++){
			int k = i * splitSize;
			blobWorkers[i] = executorBlobs.submit((new BlobWorker(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)),RGBs,w,h)));
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
		Future<BufferedImage> g1 = executorGraphics.submit(new GraphicsBlobWorker(0,0,w / 2,h / 2,RGBs,bounds));
		Future<BufferedImage> g2 = executorGraphics.submit(new GraphicsBlobWorker(w/2,0,w,h/2,RGBs,bounds));
		Future<BufferedImage> g3 = executorGraphics.submit(new GraphicsBlobWorker(0,h/2,w/2,h,RGBs,bounds));
		Future<BufferedImage> g4 = executorGraphics.submit(new GraphicsBlobWorker(w/2,h/2,w,h,RGBs,bounds));
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
			e.printStackTrace();
		}
		gg.drawImage(b1, 0, 0, this);
		gg.drawImage(b2, w/2, 0, this);
		gg.drawImage(b3, 0, h/2, this);
		gg.drawImage(b4, w/2, h/2, this);
		long t2 = System.nanoTime();
	}
	public void drawParticles(Graphics g1){
		if(paintBufferVolatile != null){
			paintBufferVolatile = null;
		}
		ArrayList<Particle> pA = packify(particleArray);
		g1.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
		int w = getWidth();
		int h = getHeight();
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			if(Properties.rainbow){
				g1.setColor(p.color);
			}
			if(Properties.abdelmode){
				int r = (int) (p.x % 600) / 5;
				r += (int) (p.x % 100);
				int g = (int) (p.y % 400) / 4;
				g += (int)(p.y % 50);
				int b = (int) (p.x % 10) * 10;
				b += (int) (p.y % 200) / 2;
				if(r < 0){
					r = 0;
				}
				if(g < 0){
					g = 0;
				}				
				if(b < 0){
					b = 0;
				}
				g1.setColor(new Color(r,g,b));
			}
			g1.fillRect((int)p.x ,(int)p.y, Properties.size,Properties.size);
		}
	}
	public void drawParticlesPaint(Graphics g1){
		ArrayList<Particle> pA = packify(particleArray);
		if(paintBufferVolatile == null){
			if(paintBufferVolatile == null){
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
				paintBufferVolatile = gc.createCompatibleVolatileImage(1920, 1080, Transparency.OPAQUE);
				Graphics gg = paintBufferVolatile.createGraphics();
				gg.fillRect(0, 0, 1920, 1080);
			}
		}
		Graphics gg;
			gg = paintBufferVolatile.getGraphics();
		gg.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			if(Properties.rainbow){
				gg.setColor(p.color);
			}
			if(Properties.abdelmode){
				int r = (int) (p.x % 600) / 5;
				r += (int) (p.x % 100);
				int g = (int) (p.y % 400) / 4;
				g += (int)(p.y % 50);
				int b = (int) (p.x % 10) * 10;
				b += (int) (p.y % 200) / 2;
				if(r < 0){
					r = 0;
				}
				if(g < 0){
					g = 0;
				}				
				if(b < 0){
					b = 0;
				}
				gg.setColor(new Color(r,g,b));
			}
			gg.fillRect((int)p.x, (int)p.y, Properties.size, Properties.size);
		}	
		g1.drawImage(paintBufferVolatile, 0, 0, null);
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
	//	public ArrayList<Particle> packifyWithWorker(ArrayList<Particle> pA){
	//		//XXX Strange casts and could be loops stuff here. Please fix!
	//		//TODO find out if 4 threads is really faster than just 2
	//		int q = pA.size() / 4;
	//		int h = pA.size() / 2;
	//		int f = pA.size();
	//		boolean[][] oA = new boolean[1920][1080];
	//		Future<ArrayList<Particle>> w1 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(0, q)),getWidth(),getHeight(),oA));
	//		Future<ArrayList<Particle>> w2 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(q, h)),getWidth(),getHeight(),oA));
	//		Future<ArrayList<Particle>> w3 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(h , f - q)),getWidth(),getHeight(),oA));
	//		Future<ArrayList<Particle>> w4 =  executorPhysics.submit(new PackingWorker(new ArrayList<Particle>(pA.subList(f - q, f)),getWidth(),getHeight(),oA));
	//
	//		do{
	//
	//		}while(!w1.isDone() && !w2.isDone() && !w3.isDone() && !w4.isDone());
	//
	//		ArrayList<Particle> p1 = null;
	//		ArrayList<Particle> p2 = null;
	//		ArrayList<Particle> p3 = null;
	//		ArrayList<Particle> p4 = null;
	//
	//		try {
	//			p1 = w1.get();
	//			p2 = w2.get();
	//			p3 = w3.get();
	//			p4 = w4.get();
	//
	//			p1.addAll(p2);
	//			p1.addAll(p3);
	//			p1.addAll(p4);
	//		} catch (InterruptedException | ExecutionException e) {
	//			e.printStackTrace();
	//		}
	//		return p1;
	//	}
	public void updateParticles(){

		for(int i = 0; i < pullQueue.size();i++){
			Point2D p = pullQueue.get(i);

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
			//TODO XXX Particles are null after doImage() is used
			if(p == null){
				particleArray.remove(i);
				continue;
			}
			moveify(p);
			//gravitify(p);
			frictionify(p);
		}

	}
	public void pullWithWorkers(double x, double y, double mult){
		int q = particleArray.size() / 4;
		int h = particleArray.size() / 2;
		int f = particleArray.size();
		Particle[] p1 = particleArray.subList(0, q).toArray(new Particle[particleArray.size()]);
		Particle[] p2 = particleArray.subList(q, h).toArray(new Particle[particleArray.size()]);
		Particle[] p3 = particleArray.subList(h, h+q).toArray(new Particle[particleArray.size()]);
		Particle[] p4 = particleArray.subList(h+q, f).toArray(new Particle[particleArray.size()]);

		executorPhysics.submit(new PullPhysicsWorker(x,y,p1,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p2,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p3,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p4,mult));


	}
	public void spawnify(int x, int y, double dx, double dy,Color c){
		Particle p = new Particle(x,y,dx,dy,Properties.size);
		p.color = c;
		particleArray.add(p);
	}
	public void spawnify(int x, int y, double dx, double dy){
		spawnify(x,y,dx,dy,GamePanel.randomColor());
	}
	public void spawnify(int x, int y){
		spawnify(x,y,(rand.nextFloat() - 0.5) * 10,(rand.nextFloat() - 0.5) * 10);
	}
	public void spawnify(){
		spawnify(rand.nextInt(getWidth()),rand.nextInt(getHeight()));
	}
	public void gravitify(Particle p){
		p.speedY -= Properties.gravityStrength;
	}
	public void frictionify(Particle p){
		p.speedX -= p.speedX * Properties.frictionStrength;
		p.speedY -= p.speedY * Properties.frictionStrength;

	}
	public void moveify(Particle p){
		p.x += p.speedX * Properties.timeSpeed;
		p.y += p.speedY * Properties.timeSpeed;
	}
	public boolean areCollidifying(Particle p1, Particle p2){
		double diffX = Math.abs(p1.x - p2.x);
		double diffY = Math.abs(p1.y - p2.y);
		int size = Properties.size;
		if(diffX < (size + size) / 2 && diffY < (size + size) / 2){
			return true;
		}
		return false;
	}
	public void doMouse(){
		if(Properties.lmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			pullQueue.add(new Point(x,y));
		}
		else if(Properties.rmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) ((int) p.getY() + this.getLocationOnScreen().getY()) - 57;
			pushQueue.add(new Point(x,y));
		}
	}

	public static Color randomColor(){
		Random rand = new Random();
		int[] rgb = new int[3];
		rgb[0] = rand.nextInt(255);
		rgb[1] = rand.nextInt(255);
		rgb[2] = rand.nextInt(255);
		return new Color(rgb[0],rgb[1],rgb[2]);
	}
	public static double getDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	public static double getDiamondDistance(double x1, double y1, double x2, double y2){
		double x = Math.abs(x2 - x1);
		double y = Math.abs(y2 - y1);
		return x + y;
	}


}
