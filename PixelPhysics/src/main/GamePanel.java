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
import stuff.ParticleTrail;
import stuff.Wall;
import workers.BlobWorker;
import workers.GraphicsBlobWorker;
import workers.PaintTrailWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel {

	/**
	 *
	 */

	private static final ExecutorService executorPhysics = Executors.newCachedThreadPool();
	public static final Random rand = new Random();

	private static final long serialVersionUID = 1L;

	public ArrayList<Slider> colors = new ArrayList<Slider>();

	public ArrayList<CustomComponent> components = new ArrayList<CustomComponent>();

	public int[] cooldowns = new int[10];

	DecimalFormat df2 = new DecimalFormat("0.00");

	DecimalFormat df3 = new DecimalFormat("0.000");

	private final ExecutorService executorBlobs = Executors.newCachedThreadPool();

	private final ExecutorService executorGraphics = Executors.newCachedThreadPool();

	public boolean flag = false;
	public Slider frictionSlider = new Slider(960, 50, "friction");
	VolatileImage gBuffer;
	public Slider gravitySlider = new Slider(960, 70, "gravity"){
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
	public Slider pullSlider = new Slider(960, 30, "pull");
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	int[][] RGBs = new int[1920][1080];

	public ArrayList<ParticleTrail> temporaryDrawLines = new ArrayList<ParticleTrail>();

	public static void collidePlanets(final Particle p1, final Particle p2) {
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
		t = (-b + Math.sqrt((b * b) - (4D * a * c)))/ 2D * a;
		System.out.println(t);
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
	public static double getAngle(final Line2D l) {
		double xD;
		double yD;
		final double x1 = l.getX1();
		final double y1 = l.getY1();
		final double x2 = l.getX2();
		final double y2 = l.getY2();
		if (y2 > y1) {
			yD = y2 - y1;
			xD = x2 - x1;
		} else {
			yD = y1 - y2;
			xD = x1 - x2;
		}
		return -1 * Math.atan2(yD, xD);
	}
	public static double getDiamondDistance(final double x1, final double y1, final double x2, final double y2) {
		final double x = Math.abs(x2 - x1);
		final double y = Math.abs(y2 - y1);
		//		final double x = 0;
		//		final double y = 0;
		return x + y;
	}
	public static double getDistance(final double x1, final double y1, final double x2, final double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	public static Point2D getIntersect(final Line2D l1, final Line2D l2) {
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
		final Point2D p1 = l1.getP2();
		final Point2D p2 = l2.getP2();
		final double b1 = p1.getY() - (m1 * p1.getX());
		final double b2 = p2.getY() - (m2 * p2.getX());
		final double x = (b2 - b1) / (m1 - m2);
		final double y = (m1 * x) + b1;
		return new Point((int) x, (int) y);
	}
	public static Color getParticleColor(Particle p){
		int r = 0;
		int g = 0;
		int b = 0;
		int[] r2 = new int[6];
		int[] g2 = new int[6];
		int[] b2 = new int[6];
		int modesOn = 0;
		if(Properties.singleColor){
			modesOn++;
			r2[0] = Properties.RGB[0];
			g2[0] = Properties.RGB[1];
			b2[0] = Properties.RGB[2];

		}
		if(Properties.rainbowColor){
			modesOn++;
			Color c = p.getColor();
			r2[1] = c.getRed();
			g2[1] = c.getGreen();
			b2[1] = c.getBlue();
		}
		if(Properties.gridColor){
			modesOn++;
			int r2a = (int) (p.x % 600) / 5;
			r2a += (int) (p.x % 100);
			int g2a = (int) (p.y % 400) / 4;
			g2a += (int)(p.y % 50);
			int b2a = (int) (p.x % 10) * 10;
			b2a += (int) (p.y % 200) / 2;
			if(r2a < 0){
				r2a = 0;
			}
			if(g2a < 0){
				g2a = 0;
			}				
			if(b2a < 0){
				b2a = 0;
			}
			r2[2] = r2a;
			g2[2] = g2a;
			b2[2] = b2a;
		}
		if(Properties.velocityColor){
			modesOn++;
			double v = p.getVelocity();
			int r2a = (int)-((v - 50) *(v - 50)) + 240;
			if(r2a < 0){
				r2a = 0;
			}
			r2a += 15;
			int g2a = (int)-((v - 20) *(v - 20)) + 240;
			if(g2a < 0){
				g2a = 0;
			}
			g2a += 15;
			int b2a = (int)-((v - 5) *(v - 5)) + 240;
			if(b2a < 0){
				b2a = 0;
			}
			b2a += 15;
			r2[3] = r2a;
			g2[3] = g2a;
			b2[3] = b2a;
		}
		if(Properties.directionalColor){
			modesOn++;
			double a = p.getAngle();
			double deg = (Math.toDegrees(a) + 180) % 360;
			double deg2 = (deg + 120) % 360;
			double deg3 = (deg2 + 120) % 360;
			int r2a = (int)Math.abs((deg)- 180) - 60;
			int g2a = (int)Math.abs((deg2)- 180) - 60;
			int b2a = (int)Math.abs((deg3)- 180) - 60;
			r2[4] = (int)(((double)r2a / 120D) * 255D);
			g2[4] = (int)(((double)g2a / 120D) * 255D);
			b2[4] = (int)(((double)b2a / 120D) * 255D);
		}
		for(int i = 0; i < r2.length;i++){
			r += (int)((double)r2[i] / (double)modesOn);
			g += (int)((double)g2[i] / (double)modesOn);
			b += (int)((double)b2[i] / (double)modesOn);

		}
		if(r > 255){
			r = 255;
		}
		if(g > 255){
			g = 255;
		}
		if(b > 255){
			b = 255;
		}
		if(r < 0){
			r = 0;
		}
		if(g < 0){
			g = 0;
		}
		if(b < 0){
			b = 0;
		}
		return new Color(r,g,b);
	}
	public static void planetify(final Particle p1, final Particle p2) {
		//		Line2D vector1 = p1.lastVector;
		//		Line2D vector2 = p2.lastVector;
		if(getDistance(p1.x, p1.y, p2.x, p2.y) < (Properties.size * 2)){
			collidePlanets(p1, p2);
		}
		//		double dist = GamePanel.getDistance(p2.x, p2.y, p1.x, p1.y);
		//		double deltaX = (p2.x - p1.x) / dist;
		//		double deltaY = (p2.y - p1.y) / dist;
		//		double distMin = 3;
		//		dist = Math.max(dist, distMin);
		//		double mult = Properties.trueGravity;
		//		double g = 1D / (dist * dist);
		//		if (g < 0) {
		//			g = 0;
		//		}
		//		mult *= g;
		//
		//		p2.speedX -= deltaX * Properties.getValueOfDouble("time") * mult;
		//		p2.speedY -= deltaY * Properties.getValueOfDouble("time") * mult;
		//		p1.speedX -= -deltaX * Properties.getValueOfDouble("time") * mult;
		//		p1.speedY -= -deltaY * Properties.getValueOfDouble("time") * mult;
	}
	public static Color randomColor() {
		final Random rand = new Random();
		final int[] rgb = new int[3];
		rgb[0] = rand.nextInt(255);
		rgb[1] = rand.nextInt(255);
		rgb[2] = rand.nextInt(255);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
	public static void wallCollide(final Line2D l, final Particle p, final double over) {
		p.x -= p.speedX * Properties.getValueOfDouble("time");
		p.y -= p.speedY * Properties.getValueOfDouble("time");
		final double speed = Math.sqrt((p.speedX * p.speedX) + (p.speedY * p.speedY));
		final double wAngle = Math.toDegrees(getAngle(l));
		final double bAngle = Math.toDegrees(p.getAngle());
		final double diff = wAngle - bAngle;
		final double newAngle = (bAngle + (2 * diff)) % 360;
		final double x = Math.cos(Math.toRadians(newAngle)) * speed;
		final double y = Math.sin(Math.toRadians(newAngle)) * speed;
		final double dX = Math.cos(Math.toRadians(newAngle)) * over;
		final double dY = -Math.sin(Math.toRadians(newAngle)) * over;
		p.x += dX;
		p.y += dY;
		p.speedX = x;
		p.speedY = -y;
		// p.speedY -= p.speedY / 20;
		// p.speedX -= p.speedX / 20;
	}

	public GamePanel(final int w, final int h, final int m, final int s) {
		Properties.cores = Runtime.getRuntime().availableProcessors();
		Properties.maxPixels = m;
		Properties.size = s;
		for (int i = 0; i < Properties.RGB.length; i++) {
			Properties.RGB[i] = rand.nextInt(255);
		}
		final Dimension d = new Dimension(w, h);
		setPreferredSize(d);
		this.setFocusable(true);
	}

	public boolean areCollidifying(final Particle p1, final Particle p2) {
		final double diffX = Math.abs(p1.x - p2.x);
		final double diffY = Math.abs(p1.y - p2.y);
		final int size = Properties.size;
		if ((diffX < ((size + size) / 2)) && (diffY < ((size + size) / 2))) {
			return true;
		}
		return false;
	}

	public void doMouse() {
		if (Properties.lmbHeld) {

			final Point p = MouseInfo.getPointerInfo().getLocation();
			final int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			final int y = (int) (p.getY() - this.getLocationOnScreen().getY());
			Point p2 = new Point(x, y);
			for(CustomComponent c : components){
				if(c.onClick(p2)){
					return;
				}
			}
			pullQueue.add(p2);
		} else if (Properties.rmbHeld) {
			final Point p = MouseInfo.getPointerInfo().getLocation();

			final int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			final int y = (int) ((int) p.getY() + this.getLocationOnScreen().getY()) - 57;
			pushQueue.add(new Point(x, y));
		}
	}

	public void drawBlobsWithWorkers(final Graphics gg) {
		final int w = getWidth();
		final int h = getHeight();
		final Bounds bounds = new Bounds(particleArray);
		long t0 = System.nanoTime();
		final Future<?>[] blobWorkers = new Future<?>[Properties.cores];
		final int splitSize = particleArray.size() / Properties.cores;
		for (int i = 0; i < Properties.cores; i++) {
			final int k = i * splitSize;
			blobWorkers[i] = executorBlobs.submit(
					new BlobWorker(new ArrayList<Particle>(particleArray.subList(k, k + splitSize)), RGBs, w, h));
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
		long t1 = System.nanoTime();
		final Future<BufferedImage> g1 = executorGraphics
				.submit(new GraphicsBlobWorker(0, 0, w / 2, h / 2, RGBs, bounds));
		final Future<BufferedImage> g2 = executorGraphics
				.submit(new GraphicsBlobWorker(w / 2, 0, w, h / 2, RGBs, bounds));
		final Future<BufferedImage> g3 = executorGraphics
				.submit(new GraphicsBlobWorker(0, h / 2, w / 2, h, RGBs, bounds));
		final Future<BufferedImage> g4 = executorGraphics
				.submit(new GraphicsBlobWorker(w / 2, h / 2, w, h, RGBs, bounds));
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
		long t2 = System.nanoTime();
		double a = t1 - t0;
		double b = t2 - t1;
		//		lastLag2 = (b / a);
		//		lastLag2 = (int)(a / 10000);
		gg.drawImage(b1, 0, 0, this);
		gg.drawImage(b2, w / 2, 0, this);
		gg.drawImage(b3, 0, h / 2, this);
		gg.drawImage(b4, w / 2, h / 2, this);
	}

	public void drawParticles(final Graphics g1) {
		// TODO unused Pause value checker
		if (Properties.paused) {
			return;
		}
		if (paintBufferVolatile != null) {
			paintBufferVolatile = null;
		}
		// ArrayList<Particle> pA = packify(particleArray);
		// TODO Does packing actually help anything? So far only adds lag.
		final ArrayList<Particle> pA = particleArray;
		for (int i = 0; i < pA.size(); i++) {
			final Particle p = pA.get(i);
			g1.setColor(getParticleColor(p));
			g1.fillRect((int) p.x, (int) p.y, Properties.size, Properties.size);
		}
	}

	public void drawParticlesPaint(final Graphics g1) {
		if (paintBufferVolatile == null) {
			if (paintBufferVolatile == null) {
				final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
				paintBufferVolatile = gc.createCompatibleVolatileImage(1920, 1080, Transparency.OPAQUE);
				final Graphics gg = paintBufferVolatile.createGraphics();
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
				System.out.println("Panel Line 471: Occasional Index Bounds Exception fix pleease");
				pt = temporaryDrawLines.get(i);
			}catch(java.lang.IndexOutOfBoundsException e){
				e.printStackTrace();
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

	public void frictionify(final Particle p) {
		p.speedX -= p.speedX * (Properties.frictionStrength * Properties.getValueOfDouble("time"));
		p.speedY -= p.speedY * (Properties.frictionStrength * Properties.getValueOfDouble("time"));

	}

	public void gravitify(final Particle p) {
		p.speedY += Properties.fallStrength;
	}
	public void init() {
		components.add(pullSlider);
		components.add(frictionSlider);
		components.add(gravitySlider);
		components.add(new Button(100,100,"singlecolor"));
		components.add(new Button(100,200,"rainbowcolor"));
		components.add(new Button(100,300,"gridcolor"));
		components.add(new Button(100,400,"velocitycolor"));
		components.add(new Button(100,500,"directionalcolor"));
		components.add(new Button(100,600,"diamondglow"));


		final Input in = new Input();
		addMouseListener(in);
		addKeyListener(in);

		final int a = Properties.maxPixels;
		setBackground(Color.BLACK);
		for (int i = 0; i < a; i++) {
			spawnify();
		}
		BufferedImage img = Properties.paintImage;
		int w1 = 0, h1 = 0;
		w1 = img.getWidth();
		h1 = img.getHeight();
		final double ratio = (double) h1 / (double) w1;
		final int w2 = (int) Math.sqrt(a / ratio);
		final int h2 = (int) (w2 * ratio);
		final double ratio2 = (double) w2 / (double) w1;
		Properties.paintImage = MainClass.scale(img, img.getType(), w2, h2, ratio2, ratio2);
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		gBuffer = gc.createCompatibleVolatileImage(1920, 1080, Transparency.TRANSLUCENT);
		final Graphics gg = gBuffer.getGraphics();
		gg.setColor(Color.BLACK);
		gg.fillRect(0, 0, 1920, 1080);
	}

	public Line2D moveify(final Particle p) {
		final double x = p.x;
		final double y = p.y;
		p.x += p.speedX * Properties.getValueOfDouble("time");
		p.y += p.speedY * Properties.getValueOfDouble("time");
		Line2D l = new Line2D.Double(x, y, p.x, p.y);
		p.snapshotVector = l;
		return l;
	}

	// TODO When multiple particle are in the same spot it swaps between which
	// one it draws, and when different colors = flickering :(
	// TODO With packing, particles are non-existant? wtf
	public ArrayList<Particle> packify(final ArrayList<Particle> pA) {
		final boolean[][] occupiedArray = new boolean[1920][1080];
		final ArrayList<Particle> newP = new ArrayList<Particle>();
		for (int i = 0; i < pA.size(); i++) {
			final Particle p = pA.get(i);
			final int x = (int) p.x;
			final int y = getHeight() - (int) p.y;
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
	public void paint(final Graphics g) {
		super.paint(g);
		final double t0 = System.nanoTime();
		if (Properties.paused) {
			return;
		}
		if (Properties.glow) {
			drawBlobsWithWorkers(g);
		} else if (Properties.paint) {
			drawParticlesPaint(g);
		} else {
			drawParticles(g);
		}
		final double t1 = System.nanoTime();
		//		lastLag1 = (t1 - t0) / 1000000D;
		for (int i = 0; i < Properties.walls.size(); i++) {
			g.setColor(new Color(Properties.RGB[0], Properties.RGB[1], Properties.RGB[2]));
			g.fillPolygon(Properties.walls.get(i).p);
		}
		final double fps = (16D / lastLag1) * 60;
		g.setColor(Color.WHITE);
		g.drawString(df2.format(lastLag1) + " (" + (int) fps + ")" + " - " + lastLag2, 0, 20);
		if (Properties.showStats) {
			g.drawString(Properties.RGB[0] + " " + Properties.RGB[1] + " " + Properties.RGB[2] + " "
					+ Properties.glowStrength, getWidth() / 2, getHeight() / 2);
			g.drawString(df3.format(Properties.frictionStrength) + " - " + df2.format(Properties.pullStrength) + " - "
					+ Properties.size + " - " + df2.format(Properties.getValueOfDouble("time")) 
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
	}

	public void paintImage() {
		// Paint an image w/ particles!
		final BufferedImage img = Properties.paintImage;
		final int w = img.getWidth(null);
		final int h = img.getHeight(null);
		particleArray.clear();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				final Color c = new Color(img.getRGB(x, y));
				spawnify(x + (x * (Properties.size - 1)), y + (y * (Properties.size - 1)), 0, 0, c);
			}
		}
		Properties.rainbowColor = true;
		Properties.imageFlag = false;
	}

	public void planetifyParticles() {
		Particle p1, p2;
		int f = particleArray.size();
		int h = f / 2;
		int q = f / 4;
		Particle[] pA1 = particleArray.subList(0, h).toArray(new Particle[h]);
		Particle[] pA2 = particleArray.subList(h, f).toArray(new Particle[h]);
		for (int i = 0; i < particleArray.size(); i++) {
			p1 = particleArray.get(i);
			for (int j = i + 1; j < particleArray.size(); j++) {
				p2 = particleArray.get(j);
				planetify(p1, p2);
			}
		}
	}
	public void pullWithWorkers(final double x, final double y, final double mult) {
		final int q = particleArray.size() / 4;
		final int h = particleArray.size() / 2;
		final int f = particleArray.size();
		final Particle[] p1 = particleArray.subList(0, q).toArray(new Particle[particleArray.size() / 4]);
		final Particle[] p2 = particleArray.subList(q, h).toArray(new Particle[particleArray.size() / 4]);
		final Particle[] p3 = particleArray.subList(h, h + q).toArray(new Particle[particleArray.size() / 4]);
		final Particle[] p4 = particleArray.subList(h + q, f).toArray(new Particle[particleArray.size() / 4]);

		final Future<?> f1 = executorPhysics.submit(new PullPhysicsWorker(x, y, p1, mult));
		final Future<?> f2 = executorPhysics.submit(new PullPhysicsWorker(x, y, p2, mult));
		final Future<?> f3 = executorPhysics.submit(new PullPhysicsWorker(x, y, p3, mult));
		final Future<?> f4 = executorPhysics.submit(new PullPhysicsWorker(x, y, p4, mult));

		try {
			f1.get();
			f2.get();
			f3.get();
			f4.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	public void spawnify() {
		spawnify(rand.nextInt(getWidth()), rand.nextInt(getHeight()));
	}

	public void spawnify(final int x, final int y) {
		spawnify(x, y, (rand.nextFloat() - 0.5) * 10, (rand.nextFloat() - 0.5) * 10);
	}

	public void spawnify(final int x, final int y, final double dx, final double dy) {
		spawnify(x, y, dx, dy, GamePanel.randomColor());
	}

	public void spawnify(final int x, final int y, final double dx, final double dy, final Color c) {
		final Particle p = new Particle(x, y, dx, dy, Properties.size);
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
		final double t0 = System.nanoTime();
		this.updateParticles();
		final double t1 = System.nanoTime();
		lastLag1 = (t1 - t0) / 1000000D;
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
			//			planetifyParticles();
		}
		for (int i = 0; i < pullQueue.size(); i++) {
			final Point2D p = pullQueue.get(i);

			pullWithWorkers(p.getX(), p.getY(), 1);

			pullQueue.remove(i);
		}
		for (int i = 0; i < pushQueue.size(); i++) {
			final Point2D p = pushQueue.get(i);
			pullWithWorkers(p.getX(), p.getY(), -1);

			pushQueue.remove(i);
		}
		temporaryDrawLines = new ArrayList<ParticleTrail>();
		int h = particleArray.size() / 2;
		int q = h / 2;
		final Future<ArrayList<ParticleTrail>> f1 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(0, q)));
		final Future<ArrayList<ParticleTrail>> f2 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q, q * 2)));
		final Future<ArrayList<ParticleTrail>> f3 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q * 2, q * 3)));
		final Future<ArrayList<ParticleTrail>> f4 = executorBlobs.submit(new PaintTrailWorker(particleArray.subList(q * 3, q * 4)));

		try {
			temporaryDrawLines.addAll(f1.get());
			temporaryDrawLines.addAll(f2.get());
			temporaryDrawLines.addAll(f3.get());
			temporaryDrawLines.addAll(f4.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < particleArray.size(); i++) {
			final Particle p = particleArray.get(i);

			// TODO XXX Particles are null after doImage() is used
			if (p == null) {
				particleArray.remove(i);
				continue;
			}
			final Line2D vector = moveify(p);
			double dist = Double.MAX_VALUE;
			Point2D intersect = null;
			Line2D intersectLine = null;
			for (int j = 0; j < Properties.walls.size(); j++) {
				final Wall w = Properties.walls.get(j);
				Line2D l = new Line2D.Double(w.oX[0], w.oY[0], w.oX[1], w.oY[1]);
				if (l.intersectsLine(vector)) {
					final Point2D point = getIntersect(vector, l);
					final double distNew = getDistance(point.getX(), point.getY(), p.x, p.y);
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
