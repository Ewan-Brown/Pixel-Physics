package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import stuff.Bounds;
import stuff.Particle;
import stuff.ParticleTrail;
import stuff.Wall;
import workers.BlobWorkerHalf;
import workers.GraphicsBlobWorkerHalf;
import workers.PaintTrailWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel {

	/**
	 *
	 */

	private static  ExecutorService executorPhysics = Executors.newCachedThreadPool();
	public static  Random rand = new Random();


	public ArrayList<Slider> colors = new ArrayList<Slider>();

	public ArrayList<CustomComponent> components = new ArrayList<CustomComponent>();

	public int[] cooldowns = new int[10];

	DecimalFormat df2 = new DecimalFormat("0.00");

	DecimalFormat df3 = new DecimalFormat("0.000");

	private  ExecutorService executorBlobs = Executors.newCachedThreadPool();

	private  ExecutorService executorGraphics = Executors.newCachedThreadPool();

	public static GamePanel instance;

	public static Logger logger;
	public long lastCalcTime = 0;
	public long lastPaintTime = 0;
	public boolean flag = false;
	public Slider pullSlider = new Slider(960, 30, Properties.PULL);
	public Slider frictionSlider = new Slider(960, 50, Properties.FRICTION);
	VolatileImage gBuffer;
	public Slider gravitySlider = new Slider(960, 70, Properties.GRAVITY){
		public boolean isHidden(){
			return !Properties.planetMode;
		}
	};
	public BitSet keySet = new BitSet(256);
	double lastLag1 = 0;
	double lastLag2 = 0;
	VolatileImage paintBufferVolatile;
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public ArrayList<Magnet> magnets = new ArrayList<Magnet>();
	int[][] RGBs = new int[1920][1080];

	public ArrayList<ParticleTrail> temporaryDrawLines = new ArrayList<ParticleTrail>();

	public static void collidePlanets( Particle p1,  Particle p2) {
		double u1x = p1.speedX;
		double u1y = p1.speedY;
		double u2x = p2.speedX;
		double u2y = p2.speedY;
		double t = 0; // THE SEARCH FOR TIME BEGINS
		Line2D vector1 = p1.snapshotVector;
		Line2D vector2 = p2.snapshotVector;
		double Qx = vector2.getX1() - vector1.getX1();
		double Qy = vector2.getY1() - vector1.getY1();
		double Dx = u2x - u1x;
		double Dy = u2y - u1y;
		double a = (Qx * Qx) + (Qy * Qy);
		double b = 2*((Qx * Dx)+(Qy * Dy));
		double c = -(Properties.size * 2D) * (Properties.size * 2D);
		//Test if addition or subtraction here?
		//		t = (-b + Math.sqrt((b * b) - (4D * a * c)))/ 2D * a;
		//		p1.x = vector1.getX1() + (u1x * t * Properties.timeSpeed);
		//		p1.y = vector1.getY1() + (u1y * t * Properties.timeSpeed);
		//		p2.x = vector2.getX1() + (u2x * t * Properties.timeSpeed);
		//		p2.y = vector2.getY1() + (u2y * t * Properties.timeSpeed);
		//		double u1xP = u1x - u2x;
		//		double u1yP = u1y - u2y;
		//		// Angle of collision from u1 to u2;
		//		double angleOfCollision = Math.atan2(p2.y - p1.y, p2.y - p2.x);
		//		// Normalization angle
		//		double rotationalAngle = -angleOfCollision;
		//		double u1xP2 = (u1xP * Math.cos(rotationalAngle)) - (u1yP * Math.sin(rotationalAngle));
		//		double u1yP2 = (u1xP * Math.sin(rotationalAngle)) + (u1yP * Math.cos(rotationalAngle));
		//		double v2yP2 = 0;
		//		double v1yP2 = u1yP2;
		//		double v2xP2 = (u1xP2 + Math.sqrt((u1xP2 * u1xP2) - (2 * ((v1yP2 * v1yP2) - (u1yP2 * u1yP2))))) / 2;
		//		double v1xP2 = u1xP2 - v2xP2;
		//		// Undo transformations (Rotate & Translate from u1-u2 normalization)
		//		rotationalAngle = -rotationalAngle;
		//		double v1xP = (v1xP2 * Math.cos(rotationalAngle)) - (v1yP2 * Math.sin(rotationalAngle));
		//		double v1yP = (v1xP2 * Math.sin(rotationalAngle)) + (v1yP2 * Math.cos(rotationalAngle));
		//		;
		//		double v2xP = (v2xP2 * Math.cos(rotationalAngle)) - (v2yP2 * Math.sin(rotationalAngle));
		//		;
		//		double v2yP = (v2xP2 * Math.sin(rotationalAngle)) + (v2yP2 * Math.cos(rotationalAngle));
		//		;
		//		double v1x = v1xP + u2x;
		//		double v1y = v1yP + u2y;
		//		double v2x = v2xP + u2x;
		//		double v2y = v2yP + u2y;
		//		p1.speedX = v1x;
		//		p1.speedY = v1y;
		//		p2.speedX = v2x;
		//		p2.speedY = v2y;

	}
	public static double getAngle( Line2D l) {
		double xD;
		double yD;
		double x1 = l.getX1();
		double y1 = l.getY1();
		double x2 = l.getX2();
		double y2 = l.getY2();
		if (y2 > y1) {
			yD = y2 - y1;
			xD = x2 - x1;
		} else {
			yD = y1 - y2;
			xD = x1 - x2;
		}
		return -1 * Math.atan2(yD, xD);
	}
	public static double getDiamondDistance( double x1,  double y1,  double x2,  double y2) {
		double x = Math.abs(x2 - x1);
		double y = Math.abs(y2 - y1);
		//		 double x = 0;
		//		 double y = 0;
		return x + y;
	}
	public static double getDistance( double x1,  double y1,  double x2,  double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	public static Point2D getIntersect( Line2D l1,  Line2D l2) {
		double m1 = (l1.getY2() - l1.getY1()) / (l1.getX2() - l1.getX1());
		double m2 = (l2.getY2() - l2.getY1()) / (l2.getX2() - l2.getX1());
		if (m1 == Double.NEGATIVE_INFINITY) {
			m1 = -100000;
		}
		if (m1 == Double.POSITIVE_INFINITY) {
			m1 = 100000;
		}
		if (m2 == Double.NEGATIVE_INFINITY) {
			m2 = -100000;
		}
		if (m2 == Double.POSITIVE_INFINITY) {
			m2 = 100000;
		}
		Point2D p1 = l1.getP2();
		Point2D p2 = l2.getP2();
		double b1 = p1.getY() - (m1 * p1.getX());
		double b2 = p2.getY() - (m2 * p2.getX());
		double x = (b2 - b1) / (m1 - m2);
		double y = (m1 * x) + b1;
		return new Point((int) x, (int) y);
	}

	public static void planetify( Particle p1,  Particle p2) {
		//		Line2D vector1 = p1.lastVector;
		//		Line2D vector2 = p2.lastVector;
		//		if(getDistance(p1.x, p1.y, p2.x, p2.y) < (Properties.size * 2)){
		//			collidePlanets(p1, p2);
		//		}
		double dist = GamePanel.getDistance(p2.getX(), p2.getY(), p1.getX(), p1.getY());
		double deltaX = (p2.getX() - p1.getX()) / dist;
		double deltaY = (p2.getY() - p1.getY()) / dist;
		double distMin = 3;
		dist = Math.max(dist, distMin);
		double mult = Properties.doubles[Properties.GRAVITY][1];
		double g = 1D / (dist * dist);
		if (g < 0) {
			g = 0;
		}
		mult *= g;
		double t = Properties.timeSpeed;
		p2.speedX -= deltaX * t * mult;
		p2.speedY -= deltaY * t * mult;
		p1.speedX -= -deltaX * t * mult;
		p1.speedY -= -deltaY * t * mult;
	}
	public static Color randomColor() {
		Random rand = new Random();
		int[] rgb = new int[3];
		rgb[0] = rand.nextInt(255);
		rgb[1] = rand.nextInt(255);
		rgb[2] = rand.nextInt(255);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
	public static void wallCollide( Line2D l,  Particle p,  double over) {
		p.x -= p.speedX * Properties.timeSpeed;
		p.y -= p.speedY * Properties.timeSpeed;
		double speed = Math.sqrt((p.speedX * p.speedX) + (p.speedY * p.speedY));
		double wAngle = Math.toDegrees(getAngle(l));
		double bAngle = Math.toDegrees(p.getAngle());
		double diff = wAngle - bAngle;
		double newAngle = (bAngle + (2 * diff)) % 360;
		double x = Math.cos(Math.toRadians(newAngle)) * speed;
		double y = Math.sin(Math.toRadians(newAngle)) * speed;
		double dX = Math.cos(Math.toRadians(newAngle)) * over;
		double dY = -Math.sin(Math.toRadians(newAngle)) * over;
		p.x += dX;
		p.y += dY;
		p.speedX = x;
		p.speedY = -y;
		// p.speedY -= p.speedY / 20;
		// p.speedX -= p.speedX / 20;
	}

	public GamePanel( int w,  int h,  int m) {
		//		logger = new Logger();
		instance = this;
		Properties.cores = Runtime.getRuntime().availableProcessors();
		Properties.maxPixels = m;
		Properties.size = 5;
		for (int i = 0; i < Properties.RGB.length; i++) {
			Properties.RGB[i] = rand.nextInt(255);
		}
		Dimension d = new Dimension(w, h);
		setPreferredSize(d);
		this.setFocusable(true);
	}

	public boolean areCollidifying( Particle p1,  Particle p2) {
		double diffX = Math.abs(p1.getX() - p2.getX());
		double diffY = Math.abs(p1.getY() - p2.getY());
		int size = Properties.size;
		if ((diffX < ((size + size) / 2)) && (diffY < ((size + size) / 2))) {
			return true;
		}
		return false;
	}

	public void doMouse() {
		if (Properties.lmbHeld) {

			Point p = MouseInfo.getPointerInfo().getLocation();
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			Point p2 = new Point(x, y);
			for(CustomComponent c : components){
				if(c.onClick(p2)){
					return;
				}
			}
			pullQueue.add(p2);
		} else if (Properties.rmbHeld) {
			Point p = MouseInfo.getPointerInfo().getLocation();

			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) ((int) p.getY() + this.getLocationOnScreen().getY()) - 57;
			pushQueue.add(new Point(x, y));
		}
		if(Properties.mmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation();
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			Point p2 = new Point(x, y);
			magnets.add(new Magnet(p2.x, p2.y));
		}
	}

	public void drawBlobsWithWorkers( Graphics gg) {
		int w = getWidth();
		int h = getHeight();
		Bounds bounds = new Bounds(particleArray);
		//		long t0 = System.nanoTime();
		Future<?>[] blobWorkers = new Future<?>[Properties.cores];
		int splitSize = particleArray.size() / Properties.cores;
		for (int i = 0; i < Properties.cores; i++) {
			int k = i * splitSize;
			blobWorkers[i] = executorBlobs.submit(
//					new BlobWorker(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)), RGBs, w, h));
					new BlobWorkerHalf(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)), RGBs, w, h));
		}
		boolean finished = false;
		do {
			finished = true;
			for (int i = 0; i < blobWorkers.length; i++) {
				if (!blobWorkers[i].isDone()) {
					finished = false;
				}
			}
		} while (!finished);
		//		long t1 = System.nanoTime();
		Future<BufferedImage> g1 = executorGraphics
				.submit(new GraphicsBlobWorkerHalf(0, 0, w / 2, h / 2, RGBs, bounds));
		Future<BufferedImage> g2 = executorGraphics
				.submit(new GraphicsBlobWorkerHalf(w / 2, 0, w, h / 2, RGBs, bounds));
		Future<BufferedImage> g3 = executorGraphics
				.submit(new GraphicsBlobWorkerHalf(0, h / 2, w / 2, h, RGBs, bounds));
		Future<BufferedImage> g4 = executorGraphics
				.submit(new GraphicsBlobWorkerHalf(w / 2, h / 2, w, h, RGBs, bounds));
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
		//		long t2 = System.nanoTime();
		//		double a = t1 - t0;
		//		double b = t2 - t1;
		//		lastLag2 = (b / a);
		//		lastLag2 = (int)(a / 10000);
		gg.drawImage(b1, 0, 0, this);
//		gg.drawImage(b2, w / 2, 0, this);
//		gg.drawImage(b3, 0, h / 2, this);
//		gg.drawImage(b4, w / 2, h / 2, this);
	}

	public void drawParticles( Graphics g1) {
		// TODO unused Pause value checker
		if (Properties.paused) {
			return;
		}
		if (paintBufferVolatile != null) {
			paintBufferVolatile = null;
		}
		// ArrayList<Particle> pA = packify(particleArray);
		// TODO Does packing actually help anything? So far only adds lag.
		ArrayList<Particle> pA = particleArray;
		for (int i = 0; i < pA.size(); i++) {
			Particle p = pA.get(i);
			g1.setColor(Particle.getParticleColor(p));
			g1.fillRect((int) p.x, (int) p.y, p.getWidth(), p.getHeight());
		}
	}

	public void drawParticlesPaint( Graphics g1) {
		//TODO XXX save screenshot of game
		if (paintBufferVolatile == null) {
			if (paintBufferVolatile == null) {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
				paintBufferVolatile = gc.createCompatibleVolatileImage(1920, 1080, Transparency.OPAQUE);
				Graphics gg = paintBufferVolatile.createGraphics();
				gg.fillRect(0, 0, 1920, 1080);
			}
		}
		Graphics gg;
		gg = paintBufferVolatile.getGraphics();
		Graphics2D g2 = (Graphics2D) gg;
		for(int i = 0; i < temporaryDrawLines.size(); i ++){
			ParticleTrail pt = null;
			try{
				//TODO XXX Index Out Of Bounds Exception here!
				pt = temporaryDrawLines.get(i);
			}catch(java.lang.IndexOutOfBoundsException e){
				System.out.println(i + " " + temporaryDrawLines.size());
			}
			if(pt != null){
				gg.setColor(pt.color);
				for(int j = 0; j < pt.trail.size();j++){
					g2.fill(pt.trail.get(j));
				}
			}

		}
		super.paint(g1);
		g1.drawImage(paintBufferVolatile, 0, 0, this);
	}

	public void frictionify( Particle p) {
		double t = Properties.timeSpeed;
		double f = Properties.doubles[Properties.FRICTION][1];
		p.speedX -= p.speedX * (f * t);
		p.speedY -= p.speedY * (f * t);

	}

	public void gravitify( Particle p) {
		p.speedY += Properties.fallStrength;
	}
	public void init() {
		Thread t = new Thread(logger);
		t.start();
		components.add(pullSlider);
		components.add(frictionSlider);
		components.add(gravitySlider);
		components.add(new Button(100,100,Properties.SINGLECOLOR));
		components.add(new Button(100,200,Properties.RAINBOWCOLOR));
		components.add(new Button(100,300,Properties.GRIDCOLOR));
		components.add(new Button(100,400,Properties.VELOCITYCOLOR));
		components.add(new Button(100,500,Properties.DIRECTIONALCOLOR));
		components.add(new Button(100,600,Properties.MOUSECOLOR));


		Input in = new Input();
		addMouseListener(in);
		addKeyListener(in);

		int a = Properties.maxPixels;
		setBackground(Color.BLACK);
		for (int i = 0; i < a; i++) {
			spawnify();
		}
		BufferedImage img = Properties.paintImage;
		int w1 = 0, h1 = 0;
		w1 = img.getWidth();
		h1 = img.getHeight();
		double ratio = (double) h1 / (double) w1;
		int w2 = (int) Math.sqrt(a / ratio);
		int h2 = (int) (w2 * ratio);
		double ratio2 = (double) w2 / (double) w1;
		Properties.paintImage = MainClass.scale(img, img.getType(), w2, h2, ratio2, ratio2);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		gBuffer = gc.createCompatibleVolatileImage(1920, 1080, Transparency.TRANSLUCENT);
		Graphics gg = gBuffer.getGraphics();
		gg.setColor(Color.BLACK);
		gg.fillRect(0, 0, 1920, 1080);
	}

	public Line2D moveify( Particle p) {
		double t = Properties.timeSpeed;
		double x = p.getX();
		double y = p.getY();
		p.x += p.speedX * t;
		p.y += p.speedY * t;
		Line2D l = new Line2D.Double(x, y, p.getX(), p.getY());
		p.snapshotVector = l;
		return l;
	}

	// TODO When multiple particle are in the same spot it swaps between which
	// one it draws, and when different colors = flickering :(
	// TODO With packing, particles are non-existant? wtf
	public ArrayList<Particle> packify( ArrayList<Particle> pA) {
		boolean[][] occupiedArray = new boolean[1920][1080];
		ArrayList<Particle> newP = new ArrayList<Particle>();
		for (int i = 0; i < pA.size(); i++) {
			Particle p = pA.get(i);
			int x = (int) p.getX();
			int y = getHeight() - (int) p.getY();
			if ((x > 0) && (x < getWidth()) && (y > 0) && (y < getHeight())) {
				if (!occupiedArray[x][y]) {
					occupiedArray[x][y] = true;
					newP.add(p);
				}
			}
		}
		return newP;
	}

	@Override
	public void paint( Graphics g1) {
		super.paint(g1);
		long t0 = System.nanoTime();
		BufferedImage b = new BufferedImage(1920,1080, BufferedImage.TYPE_3BYTE_BGR);;
		Graphics2D g = (Graphics2D) g1;
		if(Properties.captureFlag){
			g = b.createGraphics();
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		//		 double t0 = System.nanoTime();
		if (Properties.paused) {
			return;
		}
		if (Properties.glow) {
			drawBlobsWithWorkers(g);
		} else if (Properties.paint && temporaryDrawLines != null) {
			drawParticlesPaint(g);
		} else {
			drawParticles(g);
		}
		for (int i = 0; i < Properties.walls.size(); i++) {
			g.setColor(new Color(Properties.RGB[0], Properties.RGB[1], Properties.RGB[2]));
			g.fillPolygon(Properties.walls.get(i).p);
		}
		if(Properties.captureFlag){
			try {
				int i = 0;
				File f = new File("PixelPhysics-ScreenShot.png");
				do{
					f = new File("PixelPhysics-ScreenShot("+i+").png");
					i++;
				}while(f.exists());
				ImageIO.write(b, "bmp", f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		double fps = (16D / lastLag1) * 60;
		g.setColor(Color.WHITE);
		g.drawString(df2.format(lastPaintTime / 10000), 0, 20);// + " (" + (int) fps + ")" + " - " + lastLag2, 0, 20);
		if (Properties.showStats) {
			g.drawString(Properties.RGB[0] + " " + Properties.RGB[1] + " " + Properties.RGB[2] + " "
					+ Properties.glowStrength, getWidth() / 2, getHeight() / 2);
			g.drawString(df3.format(Properties.doubles[Properties.FRICTION][1]) + " - " 
					+ df2.format(Properties.doubles[Properties.PULL][1]) + " - "
					+ Properties.size + " - " + df2.format(Properties.timeSpeed) 
					+ " - " + Properties.glowPaintValue,
					getWidth() / 2, (getHeight() / 2) + 20);
		}
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0;i < components.size();i++){
			CustomComponent component = components.get(i);
			component.update(getWidth(), getHeight());
			if(component.isHidden()){
				continue;
			}
			Rectangle[] rA = component.getRects();
			Color[] cA = component.getColors();
			for(int j= 0; j < rA.length;j++){
				g2.setColor(cA[j]);
				g2.fill(rA[j]);
			}
		}
		for(int i = 0; i < magnets.size();i++){
			Magnet m = magnets.get(i);
			int w = 10;
			int h = 10;
			g2.fillOval(m.x - (w / 2), m.y - (h / 2), w, h);
		}
		Properties.captureFlag = false;
		long t1 = System.nanoTime();
		lastPaintTime = t1 - t0;
	}

	public void paintImage() {
		// Paint an image w/ particles!
		BufferedImage img = Properties.paintImage;
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		particleArray.clear();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color c = new Color(img.getRGB(x, y));
				spawnify(x + (x * (Properties.size - 1)), y + (y * (Properties.size - 1)), 0, 0, c);
			}
		}
		Properties.rainbowColor = true;
		Properties.imageFlag = false;
	}

	public void planetifyParticles() {
		Particle p1, p2;
		//		int f = particleArray.size();
		//		int h = f / 2;
		//		int q = f / 4;
		//TODO Make gravity multithreaded
		//		Particle[] pA1 = particleArray.subList(0, h).toArray(new Particle[h]);
		//		Particle[] pA2 = particleArray.subList(h, f).toArray(new Particle[h]);
		for (int i = 0; i < particleArray.size(); i++) {
			p1 = particleArray.get(i);
			for (int j = i + 1; j < particleArray.size(); j++) {
				p2 = particleArray.get(j);
				planetify(p1, p2);
			}
		}
	}
	public void pullWithWorkers( double x,  double y,  double mult) {
		int q = particleArray.size() / 4;
		int h = particleArray.size() / 2;
		int f = particleArray.size();
		Particle[] p1 = particleArray.subList(0, q).toArray(new Particle[particleArray.size() / 4]);
		Particle[] p2 = particleArray.subList(q, h).toArray(new Particle[particleArray.size() / 4]);
		Particle[] p3 = particleArray.subList(h, h + q).toArray(new Particle[particleArray.size() / 4]);
		Particle[] p4 = particleArray.subList(h + q, f).toArray(new Particle[particleArray.size() / 4]);

		Future<?> f1 = executorPhysics.submit(new PullPhysicsWorker(x, y, p1, mult));
		Future<?> f2 = executorPhysics.submit(new PullPhysicsWorker(x, y, p2, mult));
		Future<?> f3 = executorPhysics.submit(new PullPhysicsWorker(x, y, p3, mult));
		Future<?> f4 = executorPhysics.submit(new PullPhysicsWorker(x, y, p4, mult));

		try {
			f1.get();
			f2.get();
			f3.get();
			f4.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}
	public void updateMagnets(){
		for(int i = 0; i < magnets.size();i++){
			Magnet m = magnets.get(i);
			pullWithWorkers(m.x, m.y, 0.1);
			m.life--;
			System.out.println(m.life);
			if(m.life < 0){
				magnets.remove(i);
			}
		}
	}
	public void spawnify() {
		spawnify(rand.nextInt(getWidth()), rand.nextInt(getHeight()));
	}

	public void spawnify( int x,  int y) {
		spawnify(x, y, (rand.nextFloat() - 0.5) * 10, (rand.nextFloat() - 0.5) * 10);
	}

	public void spawnify( int x,  int y,  double dx,  double dy) {
		spawnify(x, y, dx, dy, GamePanel.randomColor());
	}

	public void spawnify( int x,  int y,  double dx,  double dy,  Color c) {
		Particle p = new Particle(x, y, dx, dy, Properties.size);
		p.setColor(c);
		particleArray.add(p);
	}
	public void update() {
		if (Properties.paused) {
			return;
		}
		if (Properties.imageFlag) {
			paintImage();
		}
		long t0 = System.nanoTime();
		updateMagnets();
		this.updateParticles();
		long t1 = System.nanoTime();
		lastLag1 = ((double)t1 - (double)t0) / 1000000D;
		lastCalcTime = t1 - t0;
		Input.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if (flag) {
			Properties.shiftColor();
		}
	}
	public void updateParticles() {
		if (Properties.planetMode) {
			planetifyParticles();
		}
		for (int i = 0; i < pullQueue.size(); i++) {
			Point2D p = pullQueue.get(i);

			pullWithWorkers(p.getX(), p.getY(), 1);

			pullQueue.remove(i);
		}
		for (int i = 0; i < pushQueue.size(); i++) {
			Point2D p = pushQueue.get(i);
			pullWithWorkers(p.getX(), p.getY(), -1);

			pushQueue.remove(i);
		}
		if(Properties.paint){
			temporaryDrawLines = new ArrayList<ParticleTrail>();
			int h = particleArray.size() / 2;
			int q = h / 2;
			Future<ArrayList<ParticleTrail>> f1 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(0, q)));
			Future<ArrayList<ParticleTrail>> f2 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q, q * 2)));
			Future<ArrayList<ParticleTrail>> f3 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q * 2, q * 3)));
			Future<ArrayList<ParticleTrail>> f4 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q * 3, q * 4)));
			try {
				temporaryDrawLines.addAll(f1.get());
				temporaryDrawLines.addAll(f2.get());
				temporaryDrawLines.addAll(f3.get());
				temporaryDrawLines.addAll(f4.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}	
		else{
			temporaryDrawLines = null;
		}
		for (int i = 0; i < particleArray.size(); i++) {
			Particle p = particleArray.get(i);

			// TODO XXX Particles are null after doImage() is used
			if (p == null) {
				particleArray.remove(i);
				continue;
			}
			p.update();
			Line2D vector = moveify(p);
			double dist = Double.MAX_VALUE;
			Point2D intersect = null;
			Line2D intersectLine = null;
			for (int j = 0; j < Properties.walls.size(); j++) {
				Wall w = Properties.walls.get(j);
				Line2D l = new Line2D.Double(w.oX[0], w.oY[0], w.oX[1], w.oY[1]);
				if (l.intersectsLine(vector)) {
					Point2D point = getIntersect(vector, l);
					double distNew = getDistance(point.getX(), point.getY(), p.getX(), p.getY());
					if (distNew < dist) {
						dist = distNew;
						intersect = point;
						intersectLine = l;
					}
				}
			}
			if ((intersect != null) && (intersectLine != null)) {
				p.x = intersect.getX();
				p.y = intersect.getY();
				wallCollide(intersectLine, p, dist);
			}
			if (Properties.fall) {
				gravitify(p);
			}
			frictionify(p);
		}

	}

}
