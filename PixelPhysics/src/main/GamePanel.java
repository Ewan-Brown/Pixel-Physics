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

import stuff.OccupiedArray;
import stuff.Particle;
import workers.GraphicsWorker;
import workers.PackingWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel implements MouseListener,KeyListener,ActionListener{

	/**
	 * 
	 */
	private ExecutorService executorPhysics = Executors.newCachedThreadPool();
	private ExecutorService executorGraphics = Executors.newCachedThreadPool();
	private static final long serialVersionUID = 1L;
	boolean lmbHeld = false;
	boolean rmbHeld = false;
	public boolean lowPerformance = false;
	public boolean fastMath1 = true;
	public boolean fastMath2 = true;
	public final int maxTimer = 30;
	public int[] cooldowns = new int[2];
	public int maxPixels = 200000;
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public double gravityStrength = 0.007;
	public double[] frictions = {0.0001,0.01,0.1};
	public double[] pulls = {.01,1,5};
	public double frictionStrength = frictions[1];
	public double pullStrength = pulls[1];
	public double timeSpeed = 1;
	public long updateDelay = 10;
	public double lastLag1 = 0;
	public double lastLag2 = 0;
	public double lastLag3 = 0;
	public int[] RGB = new int[3];
	public int RGB_switch = 1;
	public boolean flag = false;
	//	public Graphics bufferG;
	//	public BufferedImage buffer;
	int shiftAmount = 1;
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	int size = 1;
	int maxSize = 10;
	DecimalFormat df = new DecimalFormat("0.00");
	//	private OptionPanel option;
	public GamePanel(int w,int h,int m,int s){
		//		buffer = new BufferedImage(1920,1080,BufferedImage.TYPE_INT_RGB);
		//		bufferG = buffer.getGraphics();
		maxPixels = m;
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
	public void paint(Graphics gg){
		super.paint(gg);
		drawParticles(gg);
		gg.setColor(Color.WHITE);
		gg.drawString(RGB[0] + " " + RGB[1] + " " + RGB[2], getWidth() / 2, getHeight() / 2);
		//		gg.drawString(frictionStrength + " - " + pullStrength + " - " + size + " - " + timeSpeed + " - " + df.format(lastLag1) + " - " + df.format(lastLag2) + " - " + df.format(lastLag3), );	}
	}
	public void update(){
		long t0 = System.nanoTime();
		this.updateParticles();
		this.updateThemkeys();
		this.doMouse();
		this.repaint();
		flag = !flag;
		if(flag){
			shiftColor();
		}
		long t1 = System.nanoTime();
		lastLag3 = (t1 - t0) / 1000000D;
	}
	//			BufferedImage buffImage = getImage(pA);
	//			g.drawImage(buffImage, 0, 0, this);
	//	BufferedImage[] buffImages = getImageWithWorker(pA);
	//	for(int i = 0; i < buffImages.length;i++){
	//		g.drawImage(buffImages[i], 0, 0, this);
	//	}
	public void drawParticles(Graphics gg){
		gg.setColor(new Color(RGB[0],RGB[1],RGB[2]));
		long t0 = System.nanoTime();

		ArrayList<Particle> pA = packify(particleArray);

		long t1 = System.nanoTime();
		for(int i = 0; i < pA.size();i++){
			Particle p = pA.get(i);
			gg.fillRect((int)p.x ,(getHeight() - (int)p.y), size,size);
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
		Future<BufferedImage> w1 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size));
		Future<BufferedImage> w2 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(h, f))),width,height,size));
		Future<BufferedImage> w3 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size));
		Future<BufferedImage> w4 = executorGraphics.submit(new GraphicsWorker((new ArrayList<Particle>(pA.subList(0, h))),width,height,size));


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
				pullStrength += 0.03;
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
				pullStrength -= 0.03;
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
	}
	public void doMouse(){
		if(lmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (getHeight() - p.getY() + this.getLocationOnScreen().getY());
			pullQueue.add(new Point(x,y));
		}
		else if(rmbHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (getHeight() - p.getY() + this.getLocationOnScreen().getY());
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
