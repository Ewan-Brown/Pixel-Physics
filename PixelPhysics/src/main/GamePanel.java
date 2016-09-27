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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stuff.Particle;
import workers.PackingWorker;
import workers.PullPhysicsWorker;

public class GamePanel extends JPanel implements MouseListener,KeyListener,ActionListener,ChangeListener{

	/**
	 * 
	 */
	private ExecutorService exec = Executors.newCachedThreadPool();
	private static final long serialVersionUID = 1L;
	boolean lmbHeld = false;
	boolean rmbHeld = false;
	boolean flag = false;
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
	public long updateDelay = 5;
	public double lastLag1 = 0;
	public double lastLag2 = 0;
	public ArrayList<Point2D> pullQueue = new ArrayList<Point2D>();
	public ArrayList<Point2D> pushQueue = new ArrayList<Point2D>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	int size = 1;
	int maxSize = 10;
	DecimalFormat df = new DecimalFormat("#.#");
	//	private OptionPanel option;
	public GamePanel(int w,int h,int m,int s){
		maxPixels = m;
		size = s;
		Dimension d = new Dimension(w,h);
		setPreferredSize(d);
		this.setFocusable(true);

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
	public void paint(Graphics g){
		super.paint(g);
		drawParticles(g);
		g.setColor(Color.WHITE);
		g.drawString(frictionStrength + " - " + pullStrength + " - " + size + " - " + timeSpeed + " - " + df.format(lastLag1) + " - " + df.format(lastLag2), getWidth() / 2, getHeight() / 2);
	}
	public void update(){
		long t0 = System.nanoTime();
		this.updateParticles();
		this.updateThemkeys();
		this.doMouse();
		this.repaint();
		long t1 = System.nanoTime();
//		lastLag1 = (double)(t1 - t0) / 1000000D;
	}
	public void drawParticles(Graphics g){
		long t0 = System.nanoTime();
//		ArrayList<Particle> pa = packify(particleArray);
		ArrayList<Particle> pa = packifyWithWorker(particleArray);
		long t1 = System.nanoTime();
		for(int i = 0; i < pa.size(); i++){
//			Particle p = pa.get(i);
//			g.setColor(p.color);
//			g.fillRect((int)p.x ,(getHeight() - (int)p.y), size,size);
		}
		long t2 = System.nanoTime();
		lastLag1 = (double)(t1 - t0) / 1000000D;
		lastLag2 = (double)(t2 - t1) / 1000000D;

	}
	//Cuts down drawing lag by 
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
		Future<ArrayList<Particle>> pw =  exec.submit(new PackingWorker(pA,getWidth(),getHeight()));
		do{
			
		}while(!pw.isDone());
		ArrayList<Particle> p2 = null;
		try {
			p2 = pw.get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return p2;
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

			pull(p.getX(),p.getY(),-1);

			pushQueue.remove(i);
		}


		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			moveify(p);
			//			gravitify(p);
			frictionify(p);
		}

	}
	public void pull(double x, double y, double mult){
		double angle = 1;	
		double deltaX;
		double deltaY;
		mult *= timeSpeed * pullStrength;
		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			angle = FastMath.atan2((float)(p.y - y), (float)(p.x - x));
			deltaX = net.jafama.FastMath.cosQuick(angle);
			deltaY = net.jafama.FastMath.sinQuick(angle);
			p.speedX -= deltaX * mult;							
			p.speedY -= deltaY * mult;      					 
		}
	}
	public void pullWithWorkers(double x, double y, double mult){
		Particle[] p = particleArray.toArray(new Particle[particleArray.size()]);
		exec.submit(new PullPhysicsWorker(x,y,p,mult * timeSpeed * pullStrength));
	}

	public void spawnify(int x, int y, double vx, double vy){
		Particle p = new Particle(x,y,vx,vy,size);
		p.color = this.randomColor();
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
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			lmbHeld = true;
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			rmbHeld = true;
		}
	}

	public void keyPressed(KeyEvent e) {
		keySet.set(e.getKeyCode(),true);
	}
	public void keyReleased(KeyEvent e) {
		keySet.set(e.getKeyCode(),false);
	}
	public void mouseReleased(MouseEvent e) {
		lmbHeld = false;
		rmbHeld = false;
	}
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
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void keyTyped(KeyEvent e) {}
	@Override
	public void actionPerformed(ActionEvent e) {


	}
	@Override
	public void stateChanged(ChangeEvent e) {
		//		if(e.getSource() == option.slider_friction){
		//			frictionStrength = (int)option.slider_friction.getValue() / 700D;
		//		}
	}

}
