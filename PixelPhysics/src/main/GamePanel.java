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
	public int[] cooldowns = new int[10];
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public double lastLag1 = 0;
	public double lastLag2 = 0;
	public double lastLag3 = 0;
	public int RGB_switch = 1;
	public boolean flag = false;
	public Point lastClick = null;
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public ArrayList<Wall> wallArray = new ArrayList<Wall>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	int[][] RGBs = new int[1920][1080];
	DecimalFormat df = new DecimalFormat("0.00");
	BufferedImage paintBuffer;
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
	public void switchShift(){
		RGB_switch = rand.nextInt(3);
		Properties.shiftAmount = -1;
		if(getPositive(RGB_switch)){
			Properties.shiftAmount = 1;
		}
	}
	public boolean getPositive(int index){
		if(Properties.RGB[index] < 127){
			return true;
		}
		return false;
	}
	public void shiftColor(){
		Properties.RGB[RGB_switch] += Properties.shiftAmount;
		if(Properties.RGB[RGB_switch] > 255){
			Properties.RGB[RGB_switch] = 255;
			switchShift();
		}
		else if(Properties.RGB[RGB_switch] < 1){
			Properties.RGB[RGB_switch] = 1;
			switchShift();
		}
		else if(rand.nextInt(100) < 1){
			switchShift();
		}
	}
	public void init(){
		addMouseListener(this);
		int a = Properties.maxPixels;
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
		for(int i = 0; i < wallArray.size();i++){
			Wall w = wallArray.get(i);
			g.setColor(w.c);
			g.fillPolygon(w.p);
		}
		long t1 = System.nanoTime();
		lastLag1 = (t1 - t0) / 1000000D;
		double fps = (16D / (double)lastLag1) * 60;
		g.setColor(Color.WHITE);
		g.drawString(df.format(lastLag1) + " (" +(int)fps+ ") - " + df.format(lastLag2) + " - " + df.format(lastLag3),0, 20);
		g.drawString(Properties.RGB[0] + " " + Properties.RGB[1] + " " + Properties.RGB[2] + " " + Properties.glowStrength, getWidth() / 2, (getHeight() / 2));
		g.drawString(Properties.frictionStrength + " - " + Properties.pullStrength + " - " + Properties.size + " - " + Properties.timeSpeed + " - " + df.format(lastLag1) + " - " + df.format(lastLag2) + " - " + df.format(lastLag3), getWidth() / 2, (getHeight() / 2) + 20);	
	}
	public void addWall(int x1,int y1, int x2, int y2){
		Wall w = new Wall(x1,y1,x2,y2);
		wallArray.add(w);
	}
	public void update(){
		Properties.glowRadius = (Properties.size * 2);
		this.updateParticles();
		this.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if(flag){
			shiftColor();
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
		lastLag2 = (t1 - t0) / 1000000D;
		lastLag3 = (t2 - t1) / 1000000D;

	}
	public void drawParticles(Graphics g){
		if(paintBuffer != null){
			paintBuffer = null;
		}
		ArrayList<Particle> pA = packify(particleArray);
		g.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			g.fillRect((int)p.x ,(int)p.y, Properties.size,Properties.size);
		}
	}
	public void drawParticlesPaint(Graphics g){
		ArrayList<Particle> pA = packify(particleArray);
		if(paintBuffer == null){
			paintBuffer = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
		}
		Graphics gg = paintBuffer.getGraphics();
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			gg.setColor(new Color(Properties.RGB[0],Properties.RGB[1],Properties.RGB[2]));
			gg.fillRect((int)p.x, (int)p.y, Properties.size, Properties.size);
		}	
		g.drawImage(paintBuffer, 0, 0, null);
	}
	public BufferedImage getImage(ArrayList<Particle> pA){
		BufferedImage buffImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics gg = buffImage.getGraphics();		
		gg.setColor(Color.BLUE);
		for(int i = 0; i < pA.size();i++){
			Particle p  = pA.get(i);
			//			gg.setColor(p.color);
			gg.fillRect((int)p.x ,(getHeight() - (int)p.y), Properties.size,Properties.size);
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
		int r = Properties.RGB[0];
		int g = Properties.RGB[1];
		int b = Properties.RGB[2];
		Future<BufferedImage> w1 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,Properties.size,r,g,b));
		Future<BufferedImage> w2 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(h, f))),width,height,Properties.size,r,g,b));
		Future<BufferedImage> w3 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,Properties.size,r,g,b));
		Future<BufferedImage> w4 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,Properties.size,r,g,b));


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

		executorPhysics.submit(new PullPhysicsWorker(x,y,p1,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p2,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p3,mult));
		executorPhysics.submit(new PullPhysicsWorker(x,y,p4,mult));

	}

	public void spawnify(int x, int y, double vx, double vy){
		Particle p = new Particle(x,y,vx,vy,Properties.size);
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
		p.speedY -= Properties.gravityStrength;
	}
	public void frictionify(Particle p){
		p.speedX -= p.speedX * Properties.frictionStrength;
		p.speedY -= p.speedY * Properties.frictionStrength;

	}
	public void moveify(Particle p){
		p.x += p.speedX * Properties.timeSpeed;
		p.y += p.speedY * Properties.timeSpeed;
		//		p.x += p.tempSpeedX * timeSpeed;
		//		p.y += p.tempSpeedY * timeSpeed;
		//		p.tempSpeedY = 0;
		//		p.tempSpeedX = 0;
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
	public void updateThemkeys(){
		for(int i = 0 ; i < cooldowns.length; i++){
			cooldowns[i]--;
			if(cooldowns[i] < 0){
				cooldowns[i] = 0;
			}
		}
		if(keySet.get(KeyEvent.VK_UP)){
			Properties.timeSpeed += 0.01;
		}
		if(keySet.get(KeyEvent.VK_DOWN)){
			Properties.timeSpeed -= 0.01;
			if(Properties.timeSpeed < 0.01){
				Properties.timeSpeed = 0.01;
			}
		}
		if(keySet.get(KeyEvent.VK_RIGHT)){
			Properties.timeSpeed = 0.5;
		}
		if(keySet.get(KeyEvent.VK_LEFT)){
			Properties.timeSpeed = 0;
		}
		//		if(keySet.get(KeyEvent.VK_T)){
		//			if(cooldowns[0] == 0){
		//				lowPerformance = !lowPerformance;
		//			cooldowns[0] = maxTimer;
		//			}
		//		}

		if(keySet.get(KeyEvent.VK_W)){
			if(Math.abs(Properties.pullStrength) - 1 <= 0){
				Properties.pullStrength += 0.003;
			}
			else{
				Properties.pullStrength += Math.abs(Properties.pullStrength) / 100D;
			}
			if(Properties.pullStrength > Properties.pulls[2]){
				Properties.pullStrength = Properties.pulls[2];
			}
		}
		if(keySet.get(KeyEvent.VK_R)){
			Properties.pullStrength = Properties.pulls[1];
		}
		if(keySet.get(KeyEvent.VK_F)){
			Properties.frictionStrength = Properties.frictions[1];
		}

		if(keySet.get(KeyEvent.VK_S)){
			if(Math.abs(Properties.pullStrength) - 1 <= 0){
				Properties.pullStrength -= 0.003;
			}
			else{
				Properties.pullStrength -= Math.abs(Properties.pullStrength) / 100D;
			}
			if(Properties.pullStrength < Properties.pulls[0]){
				Properties.pullStrength = Properties.pulls[0];
			}

		}
		if(keySet.get(KeyEvent.VK_A)){
			Properties.frictionStrength -= Properties.frictionStrength / 50D;
			if(Properties.frictionStrength < Properties.frictions[0]){
				Properties.frictionStrength = Properties.frictions[0];
			}
		}
		if(keySet.get(KeyEvent.VK_D)){
			Properties.frictionStrength += Properties.frictionStrength / 50D;
			if(Properties.frictionStrength > Properties.frictions[2]){
				Properties.frictionStrength = Properties.frictions[2];
			}
		}
		if(keySet.get(KeyEvent.VK_COMMA)){
			if(cooldowns[0] == 0){
				cooldowns[0] = maxTimer;
				Properties.size--;
				if(Properties.size < 1){
					Properties.size = 1;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_PERIOD)){
			if(cooldowns[1] == 0){
				cooldowns[1] = maxTimer;
				Properties.size++;
				if(Properties.size > Properties.maxSize){
					Properties.size = Properties.maxSize;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_G)){
			if(cooldowns[2] == 0){
				cooldowns[2] = maxTimer;
				Properties.glow = !Properties.glow;
			}
		}
		if(keySet.get(KeyEvent.VK_J)){
			if(cooldowns[3] == 0){
				cooldowns[3] = maxTimer;
				Properties.compound = !Properties.compound;
			}
		}
		if(keySet.get(KeyEvent.VK_L)){
			if(cooldowns[4] == 0){
				cooldowns[4] = maxTimer / 8;
				Properties.glowStrength -= 2;
				if(Properties.glowStrength < 2){
					Properties.glowStrength = 2;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_O)){
			if(cooldowns[5] == 0){
				cooldowns[5] = maxTimer / 8;
				Properties.glowStrength += 2;
				if(Properties.glowStrength > 255){
					Properties.glowStrength = 255;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_U)){
			if(cooldowns[7] == 0){
				cooldowns[7] = maxTimer;
				Properties.pixelized= !Properties.pixelized;
			}
		}
		if(keySet.get(KeyEvent.VK_Y)){
			if(cooldowns[7] == 0){
				cooldowns[7] = maxTimer;
				Properties.LSD = !Properties.LSD;
			}
		}
		if(keySet.get(KeyEvent.VK_P)){
			if(cooldowns[8] == 0){
				cooldowns[8] = maxTimer;
				Properties.paint = !Properties.paint;
			}
		}
		if(keySet.get(KeyEvent.VK_E)){
			shiftColor();
		}
		if(keySet.get(KeyEvent.VK_H)){
			Properties.RGB[0] = 255;
			Properties.RGB[1] = 70;
			Properties.RGB[2] = 0;
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
		if(e.getButton() == MouseEvent.BUTTON2){
			System.out.println(lastClick);
			Point p = e.getPoint();
			if(lastClick == null){
				lastClick = p;
			}
			else{
				addWall(lastClick.x, lastClick.y, p.x, p.y);
				lastClick = null;
			}
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
