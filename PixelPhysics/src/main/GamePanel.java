package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.geom.Line2D;
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

import javax.swing.JPanel;

import stuff.Bounds;
import stuff.Particle;
import stuff.Wall;
import workers.BlobWorker;
import workers.GraphicsBlobWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel{

	public static final Random rand = new Random();
	private static final long serialVersionUID = 1L;
	public static void wallCollide(final Line2D l,final Particle p,final double over){
		p.x -= p.speedX * Properties.timeSpeed;
		p.y -= p.speedY * Properties.timeSpeed;
		final double speed = Math.sqrt(p.speedX*p.speedX + p.speedY*p.speedY);
		final double wAngle = Math.toDegrees(getAngle(l));
		final double bAngle = Math.toDegrees(p.getAngle());
		final double diff = wAngle - bAngle;
		final double newAngle = (bAngle + 2 * diff) % 360;
		final double x = Math.cos(Math.toRadians(newAngle)) * speed;
		final double y = Math.sin(Math.toRadians(newAngle)) * speed;
		final double dX = Math.cos(Math.toRadians(newAngle)) * over;
		final double dY = -Math.sin(Math.toRadians(newAngle)) * over;
		p.x += dX;
		p.y += dY;
		p.speedX = x;
		p.speedY = -y;
		//		p.speedY -= p.speedY / 20;
		//		p.speedX -= p.speedX / 20;
	}
	public static double getAngle(final Line2D l){
		double xD;
		double yD;
		final double x1 = l.getX1();
		final double y1 = l.getY1();
		final double x2 = l.getX2();
		final double y2 = l.getY2();
		if(y2 > y1){
			yD = y2 - y1;
			xD = x2 - x1;
		}
		else{
			yD = y1 - y2;
			xD = x1 - x2;
		}
		return -1 * Math.atan2(yD, xD);
	}
	public static double getDiamondDistance(final double x1, final double y1, final double x2, final double y2){
		final double x = Math.abs(x2 - x1);
		final double y = Math.abs(y2 - y1);
		return x + y;
	}
	public static double getDistance(final double x1, final double y1, final double x2, final double y2){
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	public static Point2D getIntersect(final Line2D l1, final Line2D l2){
		double m1 = (l1.getY2() - l1.getY1()) / (l1.getX2() - l1.getX1());
		double m2 = (l2.getY2() - l2.getY1()) / (l2.getX2() - l2.getX1());
		if(m1 == Double.NEGATIVE_INFINITY)
			m1 = - 100000;
		if(m1 == Double.POSITIVE_INFINITY)
			m1 = 100000;
		if(m2 == Double.NEGATIVE_INFINITY)
			m2 = -100000;
		if(m2 == Double.POSITIVE_INFINITY)
			m2 = 100000;
		final Point2D p1 = l1.getP2();
		final Point2D p2 = l2.getP2();
		final double b1 = p1.getY() - m1 * p1.getX();
		final double b2 = p2.getY() - m2 * p2.getX();
		final double x = (b2 - b1) / (m1 - m2);
		final double y = m1 * x + b1;
		return new Point((int) x, (int) y);
	}
	public static Color randomColor(){
		final Random rand = new Random();
		final int[] rgb = new int[3];
		rgb[0] = rand.nextInt(255);
		rgb[1] = rand.nextInt(255);
		rgb[2] = rand.nextInt(255);
		return new Color(rgb[0],rgb[1],rgb[2]);
	}
	public int[] cooldowns = new int[10];
	DecimalFormat df2 = new DecimalFormat("0.00");
	DecimalFormat df3 = new DecimalFormat("0.000");
	private final ExecutorService executorBlobs = Executors.newCachedThreadPool();
	private final ExecutorService executorGraphics = Executors.newCachedThreadPool();
	/**
	 *
	 */
	private static final ExecutorService executorPhysics = Executors.newCachedThreadPool();
	public boolean flag = false;
	VolatileImage gBuffer;
	public BitSet keySet = new BitSet(256);
	double lastLag1 = 0;
	VolatileImage paintBufferVolatile;
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	int[][] RGBs = new int[1920][1080];
	public GamePanel(final int w,final int h,final int m,final int s){
		Properties.cores = Runtime.getRuntime().availableProcessors();
		Properties.maxPixels = m;
		Properties.size = s;
		for(int i = 0; i < Properties.RGB.length;i++)
			Properties.RGB[i] = rand.nextInt(255);
		final Dimension d = new Dimension(w,h);
		setPreferredSize(d);
		this.setFocusable(true);
	}
	public boolean areCollidifying(final Particle p1, final Particle p2){
		final double diffX = Math.abs(p1.x - p2.x);
		final double diffY = Math.abs(p1.y - p2.y);
		final int size = Properties.size;
		if(diffX < (size + size) / 2 && diffY < (size + size) / 2)
			return true;
		return false;
	}
	public void doImage(){
		//Paint an image w/ particles!
		final BufferedImage img = Properties.paintImage;
		final int w = img.getWidth(null);
		final int h = img.getHeight(null);
		particleArray.clear();
		for(int x = 0; x < w;x++)
			for(int y = 0; y < h;y++){
				final Color c = new Color(img.getRGB(x, y));
				spawnify(x + x * (Properties.size - 1), y + y * (Properties.size - 1),0,0,c);
			}
		Properties.rainbow = true;
		Properties.imageFlag = false;
	}
	public void doMouse(){
		if(Properties.lmbHeld){
			final Point p = MouseInfo.getPointerInfo().getLocation() ;
			final int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			final int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			pullQueue.add(new Point(x,y));
		}
		else if(Properties.rmbHeld){
			final Point p = MouseInfo.getPointerInfo().getLocation() ;
			final int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			final int y = (int) ((int) p.getY() + this.getLocationOnScreen().getY()) - 57;
			pushQueue.add(new Point(x,y));
		}
	}
	public void drawBlobsWithWorkers(final Graphics gg){
		//		int qr = particleArray.size() / 4;
		//		int hf = particleArray.size() / 2;
		//		int fl = particleArray.size();
		final int w = getWidth();
		final int h = getHeight();
		final Bounds bounds = new Bounds(particleArray);
		final Future<?>[] blobWorkers = new Future<?>[Properties.cores];
		final int splitSize = particleArray.size() / Properties.cores;
		for(int i = 0; i < Properties.cores;i++){
			final int k = i * splitSize;
			blobWorkers[i] = executorBlobs.submit(new BlobWorker(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)),RGBs,w,h));
		}
		boolean finished = false;
		do{
			finished = true;
			for(int i = 0; i < blobWorkers.length;i++)
				if(!blobWorkers[i].isDone())
					finished = false;
		}while(!finished);
		final Future<BufferedImage> g1 = executorGraphics.submit(new GraphicsBlobWorker(0,0,w / 2,h / 2,RGBs,bounds));
		final Future<BufferedImage> g2 = executorGraphics.submit(new GraphicsBlobWorker(w/2,0,w,h/2,RGBs,bounds));
		final Future<BufferedImage> g3 = executorGraphics.submit(new GraphicsBlobWorker(0,h/2,w/2,h,RGBs,bounds));
		final Future<BufferedImage> g4 = executorGraphics.submit(new GraphicsBlobWorker(w/2,h/2,w,h,RGBs,bounds));
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
	}
	public void drawParticles(final Graphics g1){
		//TODO unused Pause value checker
		if(Properties.paused)
			return;
		if(paintBufferVolatile != null)
			paintBufferVolatile = null;
		//		ArrayList<Particle> pA = packify(particleArray);
		//TODO Does packing actually help anything? So far only adds lag.
		final ArrayList<Particle> pA = particleArray;
		g1.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
		for(int i = 0; i < pA.size();i++){
			final Particle p = pA.get(i);
			if(Properties.rainbow)
				g1.setColor(p.color);
			if(Properties.colorGrid){
				int r = (int) (p.x % 600) / 5;
				r += (int) (p.x % 100);
				int g = (int) (p.y % 400) / 4;
				g += (int)(p.y % 50);
				int b = (int) (p.x % 10) * 10;
				b += (int) (p.y % 200) / 2;
				if(r < 0)
					r = 0;
				if(g < 0)
					g = 0;
				if(b < 0)
					b = 0;
				g1.setColor(new Color(r,g,b));
			}
			g1.fillRect((int)p.x ,(int)p.y, Properties.size,Properties.size);
		}
	}
	public void drawParticlesPaint(final Graphics g1){
		final ArrayList<Particle> pA = packify(particleArray);
		if(paintBufferVolatile == null)
			if(paintBufferVolatile == null){
				final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
				paintBufferVolatile = gc.createCompatibleVolatileImage(1920, 1080, Transparency.OPAQUE);
				final Graphics gg = paintBufferVolatile.createGraphics();
				gg.fillRect(0, 0, 1920, 1080);
			}
		Graphics gg;
		gg = paintBufferVolatile.getGraphics();
		gg.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
		for(int i = 0; i < pA.size();i++){
			final Particle p = pA.get(i);
			if(Properties.rainbow)
				gg.setColor(p.color);
			if(Properties.colorGrid){
				int r = (int) (p.x % 600) / 5;
				r += (int) (p.x % 100);
				int g = (int) (p.y % 400) / 4;
				g += (int)(p.y % 50);
				int b = (int) (p.x % 10) * 10;
				b += (int) (p.y % 200) / 2;
				if(r < 0)
					r = 0;
				if(g < 0)
					g = 0;
				if(b < 0)
					b = 0;
				gg.setColor(new Color(r,g,b));
			}
			gg.fillRect((int)p.x, (int)p.y, Properties.size, Properties.size);
		}
		super.paint(g1);
		g1.drawImage(paintBufferVolatile, 0,0, this);
	}
	public void frictionify(final Particle p){
		p.speedX -= p.speedX * Properties.frictionStrength;
		p.speedY -= p.speedY * Properties.frictionStrength;

	}
	public void planetifyParticles(){
		Particle p1,p2;
		for(int i = 0 ; i < particleArray.size();i++){
			p1 = particleArray.get(i);
			for(int j = i + 1; j < particleArray.size();j++){
				p2 = particleArray.get(j);
				planetify(p1, p2);
			}
		}
	}
	public static void collidePlanets(final Particle p1, final Particle p2){
		double u1x = p1.speedX;
		double u1y = p1.speedY;
		double u2x = p2.speedX;
		double u2y = p2.speedY;
		double u1xP = u1x - u2x;
		double u1yP = u1y - u2y;
		//Angle of collision from u1 to u2;
		double angleOfCollision = Math.atan2(p2.y - p1.y, p2.y - p2.x);
		//Normalization angle
		double rotationalAngle = -angleOfCollision;
		double u1xP2 = (u1xP * Math.cos(rotationalAngle)) - (u1yP * Math.sin(rotationalAngle));
		double u1yP2 = (u1xP * Math.sin(rotationalAngle)) + (u1yP * Math.cos(rotationalAngle));
		double v2yP2 = 0;
		double v1yP2 = u1yP2;
		double v2xP2 = (u1xP2 + Math.sqrt(u1xP2 * u1xP2 - 2 * (v1yP2 * v1yP2 - u1yP2 * u1yP2))) / 2;
		double v1xP2 = u1xP2 - v2xP2;
		//Undo transformations (Rotate & Translate from u1-u2 normalization)
		rotationalAngle = -rotationalAngle;
		double v1xP = (v1xP2 * Math.cos(rotationalAngle)) - (v1yP2 * Math.sin(rotationalAngle));
		double v1yP = (v1xP2 * Math.sin(rotationalAngle)) + (v1yP2 * Math.cos(rotationalAngle));;
		double v2xP = (v2xP2 * Math.cos(rotationalAngle)) - (v2yP2 * Math.sin(rotationalAngle));;
		double v2yP = (v2xP2 * Math.sin(rotationalAngle)) + (v2yP2 * Math.cos(rotationalAngle));;
		double v1x = v1xP + u2x;
		double v1y = v1yP + u2y;
		double v2x = v2xP + u2x;
		double v2y = v2yP + u2y;
		p1.speedX = v1x;
		p1.speedY = v1y;
		p2.speedX = v2x;
		p2.speedY = v2y;


	}
	public void planetify(final Particle p1, final Particle p2){
		final double dist = GamePanel.getDistance(p2.x, p2.y, p1.x, p1.y);
		if(dist < 3){
			return;
		}
		double mult = 2;
		double a =  1D / (dist * dist);
//		double b = 1 / (dist * dist);
		//		double b = 5D / (0.1 * (dist + 5));
		//		a += b;
		if(a < 0){
			a = 0;
		}
		mult *= a;
//		if(dist < Properties.size){
//			mult = 0;
//			collidePlanets(p1, p2);
//		}
		final double deltaX = (p2.x - p1.x) / dist;
		final double deltaY = (p2.y - p1.y) / dist;
		final double deltaX2 = (p1.x - p2.x) / dist;
		final double deltaY2 = (p1.y - p2.y) / dist;
		p2.speedX -= deltaX * Properties.timeSpeed * mult;
		p2.speedY -= deltaY * Properties.timeSpeed * mult;
		p1.speedX -= deltaX2 * Properties.timeSpeed * mult;
		p1.speedY -= deltaY2 * Properties.timeSpeed * mult;
	}
	public void gravitify(final Particle p){
		p.speedY += Properties.gravityStrength;
	}
	public void init(){
		final Input in = new Input();
		addMouseListener(in);
		addKeyListener(in);

		final int a = Properties.maxPixels;
		setBackground(Color.BLACK);
		for(int i = 0; i < a;i++)
			spawnify();
		BufferedImage img = Properties.paintImage;
		int w1 = 0,h1 = 0;
		w1 = img.getWidth();
		h1 = img.getHeight();
		final double ratio = (double)h1 / (double)w1;
		final int w2 = (int)Math.sqrt(a / ratio);
		final int h2 = (int)(w2 * ratio);
		final double ratio2 = (double)w2 / (double)w1;
		Properties.paintImage = MainClass.scale(img, img.getType(),w2,h2, ratio2, ratio2);
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		gBuffer = gc.createCompatibleVolatileImage(1920, 1080, Transparency.TRANSLUCENT);
		final Graphics gg = gBuffer.getGraphics();
		gg.setColor(Color.BLACK);
		gg.fillRect(0, 0, 1920, 1080);
	}
	public Line2D moveify(final Particle p){
		final double x =  p.x;
		final double y =  p.y;
		p.x += p.speedX * Properties.timeSpeed;
		p.y += p.speedY * Properties.timeSpeed;
		return new Line2D.Double(x,y,p.x,p.y);
	}
	//TODO When multiple particle are in the same spot it swaps between which one it draws, and when different colors = flickering :(
	//TODO With packing, particles are non-existant? wut
	public ArrayList<Particle> packify(final ArrayList<Particle> pA){
		final boolean[][] occupiedArray = new boolean[1920][1080];
		final ArrayList<Particle> newP = new ArrayList<Particle>();
		for(int i = 0; i < pA.size();i++){
			final Particle p = pA.get(i);
			final int x = (int)p.x;
			final int y = getHeight() - (int)p.y;
			if(x > 0 && x < getWidth() && y > 0 && y < getHeight())
				if(!occupiedArray[x][y]){
					occupiedArray[x][y] = true;
					newP.add(p);
				}
		}
		return newP;
	}
	@Override
	public void paint(final Graphics g){
		super.paint(g);
		if(Properties.paused)
			return;
		if(Properties.glow)
			drawBlobsWithWorkers(g);
		else if(Properties.paint)
			drawParticlesPaint(g);
		else
			drawParticles(g);
		for(int i = 0; i < Properties.walls.size();i++){
			g.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
			g.fillPolygon(Properties.walls.get(i).p);
		}
		final double fps = 16D / lastLag1 * 60;
		g.setColor(Color.WHITE);
		g.drawString(df2.format(lastLag1) + " (" +(int)fps+")",0, 20);
		if(Properties.showStats){
			g.drawString(Properties.RGB[0] + " " + Properties.RGB[1] + " " + Properties.RGB[2] + " " + Properties.glowStrength, getWidth() / 2, getHeight() / 2);
			g.drawString(df3.format(Properties.frictionStrength) + " - " + df2.format(Properties.pullStrength) + " - " + Properties.size + " - " + df2.format(Properties.timeSpeed) + " - " + Properties.glowPaintValue, getWidth() / 2, getHeight() / 2 + 20);
		}
	}
	public void pullWithWorkers(final double x, final double y, final double mult){
		final int q = particleArray.size() / 4;
		final int h = particleArray.size() / 2;
		final int f = particleArray.size();
		final Particle[] p1 = particleArray.subList(0, q).toArray(new Particle[particleArray.size()]);
		final Particle[] p2 = particleArray.subList(q, h).toArray(new Particle[particleArray.size()]);
		final Particle[] p3 = particleArray.subList(h, h+q).toArray(new Particle[particleArray.size()]);
		final Particle[] p4 = particleArray.subList(h+q, f).toArray(new Particle[particleArray.size()]);

		final Future<?> f1 = executorPhysics.submit(new PullPhysicsWorker(x,y,p1,mult));
		final Future<?> f2 = executorPhysics.submit(new PullPhysicsWorker(x,y,p2,mult));
		final Future<?> f3 = executorPhysics.submit(new PullPhysicsWorker(x,y,p3,mult));
		final Future<?> f4 = executorPhysics.submit(new PullPhysicsWorker(x,y,p4,mult));

		try {
			f1.get();
			f2.get();
			f3.get();
			f4.get();
		} catch (InterruptedException | ExecutionException e) {
			//				e.printStackTrace();
			//TODO XXX same null errors for pulling here
		}

	}
	public void spawnify(){
		spawnify(rand.nextInt(getWidth()),rand.nextInt(getHeight()));
	}
	public void spawnify(final int x, final int y){
		spawnify(x,y,(rand.nextFloat() - 0.5) * 10,(rand.nextFloat() - 0.5) * 10);
	}

	public void spawnify(final int x, final int y, final double dx, final double dy){
		spawnify(x,y,dx,dy,GamePanel.randomColor());
	}
	public void spawnify(final int x, final int y, final double dx, final double dy,final Color c){
		final Particle p = new Particle(x,y,dx,dy,Properties.size);
		p.color = c;
		particleArray.add(p);
	}
	public void update(){
		if(Properties.paused)
			return;
		if(Properties.imageFlag)
			doImage();
		final double t0 = System.nanoTime();
		this.updateParticles();
		final double t1 = System.nanoTime();
		lastLag1 = (t1 - t0) / 1000000D;
		Input.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if(flag)
			Properties.shiftColor();
	}
	public void updateParticles(){
		if(Properties.planetMode){
			planetifyParticles();
		}
		for(int i = 0; i < pullQueue.size();i++){
			final Point2D p = pullQueue.get(i);

			pullWithWorkers(p.getX(),p.getY(),1);

			pullQueue.remove(i);
		}
		for(int i = 0; i < pushQueue.size();i++){
			final Point2D p = pushQueue.get(i);

			pullWithWorkers(p.getX(),p.getY(),-1);

			pushQueue.remove(i);
		}


		for(int i = 0; i < particleArray.size();i++){
			final Particle p = particleArray.get(i);
			//TODO XXX Particles are null after doImage() is used
			if(p == null){
				particleArray.remove(i);
				continue;
			}
			final Line2D vector = moveify(p);
			double dist = Double.MAX_VALUE;
			Point2D intersect = null;
			Line2D intersectLine = null;
			for(int j = 0; j < Properties.walls.size();j++)
			{
				final Wall w = Properties.walls.get(j);
				Line2D l = new Line2D.Double(w.oX[0], w.oY[0], w.oX[1], w.oY[1]);
				if(l.intersectsLine(vector)){
					final Point2D point = getIntersect(vector, l);
					final double distNew = getDistance(point.getX(), point.getY(), p.x, p.y);
					if(distNew < dist){
						dist = distNew;
						intersect = point;
						intersectLine = l;
					}
				}
			}
			if(intersect != null && intersectLine != null){
				p.x = intersect.getX();
				p.y = intersect.getY();
				wallCollide(intersectLine, p,dist);
			}
			if(Properties.gravity)
				gravitify(p);
			frictionify(p);
		}

	}


}
