package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stuff.Particle;

public class GamePanel extends JPanel implements MouseListener,KeyListener,ActionListener,ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean mouseHeld = false;
	int width;
	int height;
	boolean flag = false;
	public boolean pushBuffer = false;
	public boolean fastMath1 = true;
	public boolean fastMath2 = true;
	public ArrayList<Particle> particleArray = new ArrayList<Particle>();
	public double gravityStrength = 0.007;
	public double frictionStrength = 0.01;
	public double[] frictions = {0.0001,00.1,0.1};
	public double pushStrength = 1;
	public double[] pushes = {0,1,10};
	public double timeSpeed = 1;
	public long updateDelay = 7;
	public ArrayList<Point2D> queue = new ArrayList<Point2D>();
	public static final Random rand = new Random();
	public BitSet keySet = new BitSet(256);
	//	private OptionPanel option;
	public GamePanel(int w,int h,OptionPanel p){
		//		option = p;
		width = w;
		height = h;
		Dimension d = new Dimension(w,h);
		setPreferredSize(d);
		this.setFocusable(true);

	}
	public void init(){
		addMouseListener(this);
		int a = 200000;
		addKeyListener(this);
		setBackground(Color.BLACK);
		for(int i = 0; i < a;i++){
			spawnify();
		}
	}
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D) graphics;
		super.paint(g);
		drawParticles(g);
		g.setColor(Color.WHITE);
		g.drawString(pushBuffer + " " + frictionStrength + " " + pushStrength + " " + fastMath1 + " " + fastMath2, getWidth() / 2, getHeight() / 2);
	}
	public void update(){
		this.updateParticles();
		this.updateThemkeys();
		this.doMouse();
		this.repaint();


	}
	public void drawParticles(Graphics2D g){
		for(int i = 0; i < particleArray.size(); i++){
			Particle p = particleArray.get(i);
			g.setColor(p.color);
			g.fillRect((int)p.x  - ((p.width - 1) / 2) ,(height - (int)p.y) - ((p.height - 1) / 2), p.width,p.height);
		}
	}
	public ArrayList<Particle> getParticles(){
		return particleArray;
	}
	public void updateParticles(){

		for(int i = 0; i < queue.size();i++){
			Point2D p = queue.get(i);

			push(p.getX(),p.getY());

			queue.remove(i);
		}


		for(int i = 0; i < particleArray.size();i++){
			Particle p = particleArray.get(i);
			moveify(p);
			//gravitify(p);
			frictionify(p);
		}

	}
	public void push(double x, double y){
		if(!pushBuffer){
			flag = true;
		}
		if(flag){
			double speed;
			double angle = 1;
			double deltaX;
			double deltaY;
//			long t1 = System.nanoTime();
				for(int i = 0; i < particleArray.size();i++){
					Particle p = particleArray.get(i);
					speed = pushStrength ;						 
					if(fastMath1){
						angle = FastMath.atan2((float)(p.y - y), (float)(p.x - x));
					}
					else{
						angle = Math.atan2(p.y - y, p.x - x); //XXX SLOWER THAN FASTMATH, SHOULDN'T USE!

					}
					if(fastMath2){
						deltaX = net.jafama.FastMath.cos(angle);
						deltaY = net.jafama.FastMath.sin(angle);
					}
					else{
					deltaX = Math.cos(angle);	    
					deltaY = Math.sin(angle);	 
					}

					p.speedX -= deltaX * speed * timeSpeed;							
					p.speedY -= deltaY * speed * timeSpeed;      					 
					if(pushBuffer){
						p.tempSpeedX = deltaX;
						p.tempSpeedY = deltaY;
					}
				}
//			long t2 = System.nanoTime();
//			System.out.println((double)(t2 - t1) / 10D);

		}
		flag = !flag;
	}
	public void spawnify(int x, int y, double vx, double vy){
		Particle p = new Particle(x,y,vx,vy);
		p.color = this.randomColor();
		particleArray.add(p);
	}
	public void spawnify(int x, int y){
		spawnify(x,y,(rand.nextFloat() - 0.5) * 10,(rand.nextFloat() - 0.5) * 10);
	}
	public void spawnify(){
		spawnify(rand.nextInt(width),rand.nextInt(height));
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
		p.x += p.tempSpeedX * timeSpeed;
		p.y += p.tempSpeedY * timeSpeed;
		p.tempSpeedY = 0;
		p.tempSpeedX = 0;
	}
	public boolean areCollidifying(Particle p1, Particle p2){
		double diffX = Math.abs(p1.x - p2.x);
		double diffY = Math.abs(p1.y - p2.y);
		if(diffX < (p1.width + p2.width) / 2 && diffY < (p1.height + p2.height) / 2){
			return true;
		}
		return false;
	}
	public void updateThemkeys(){
		if(keySet.get(KeyEvent.VK_UP)){
			timeSpeed += 0.01;
		}
		if(keySet.get(KeyEvent.VK_DOWN)){
			timeSpeed -= 0.01;
		}
		if(keySet.get(KeyEvent.VK_SPACE)){
			timeSpeed = 0.5;
		}
		if(timeSpeed < 0.01){
			timeSpeed = 0.01;
		}
		if(keySet.get(KeyEvent.VK_T)){
			pushBuffer = !pushBuffer;
		}
		if(keySet.get(KeyEvent.VK_1)){
			fastMath1 = !fastMath1;
		}
		if(keySet.get(KeyEvent.VK_2)){
			fastMath2 = !fastMath2;
		}
		if(keySet.get(KeyEvent.VK_W)){
			pushStrength += pushStrength / 100D;
			if(pushStrength > pushes[2]){
				pushStrength = pushes[2];
			}
		}
		if(keySet.get(KeyEvent.VK_S)){
			pushStrength -= pushStrength / 100D;
			if(pushStrength < pushes[0]){
				pushStrength = pushes[0];
			}

		}
		if(keySet.get(KeyEvent.VK_A)){
			frictionStrength -= frictionStrength / 100D;
			if(frictionStrength < frictions[0]){
				frictionStrength = frictions[0];
			}
		}
		if(keySet.get(KeyEvent.VK_D)){
			frictionStrength += frictionStrength / 100D;
			if(frictionStrength > frictions[2]){
				frictionStrength = frictions[2];
			}
		}
	}
	public void doMouse(){
		if(mouseHeld){
			Point p = MouseInfo.getPointerInfo().getLocation() ;
			int x = (int) (p.getX() - this.getLocationOnScreen().getX());
			int y = (int) (height - p.getY() + this.getLocationOnScreen().getY());
			queue.add(new Point(x,y));
		}
	}
	public void mousePressed(MouseEvent e) {
		mouseHeld = true;
	}

	public void keyPressed(KeyEvent e) {
		keySet.set(e.getKeyCode(),true);
	}
	public void keyReleased(KeyEvent e) {
		keySet.set(e.getKeyCode(),false);
	}
	public void mouseReleased(MouseEvent e) {
		mouseHeld = false;
	}
	public void mouseExited(MouseEvent e) {
		mouseHeld = false;
	}
	public static Color randomColor(){
		Random rand = new Random();
		return new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
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
