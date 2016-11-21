package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;

import stuff.Wall;

public class Input implements KeyListener,MouseListener{
	public static BitSet keySet = new BitSet(256);
	public static final int maxTimer = 60;
	public static int[] cooldowns = new int[20];
	public static void updateThemkeys(){
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
			if(cooldowns[6] == 0){
				cooldowns[6] = maxTimer;
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
			Properties.shiftColor();
		}
		if(keySet.get(KeyEvent.VK_H)){
			Properties.RGB[0] = 255;
			Properties.RGB[1] = 70;
			Properties.RGB[2] = 0;
		}
		if(keySet.get(KeyEvent.VK_V)){
			if(cooldowns[9] == 0){
				cooldowns[9] = maxTimer;
				Properties.showStats = !Properties.showStats;
			}
		}
		if(keySet.get(KeyEvent.VK_T)){
			if(cooldowns[10] == 0){
				cooldowns[10] = maxTimer;
				Properties.rainbow = !Properties.rainbow;
			}
		}
		if(keySet.get(KeyEvent.VK_C)){
			if(cooldowns[11] == 0){
				cooldowns[11] = maxTimer;
				Properties.abdelmode = !Properties.abdelmode; 
			}
		}
		if(keySet.get(KeyEvent.VK_Z)){
			if(cooldowns[12] == 0){
				cooldowns[12] = maxTimer / 4;
				Properties.glowPaintValue -= 5;
				if(Properties.glowPaintValue < 5){
					Properties.glowPaintValue = 5;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_X)){
			if(cooldowns[13] == 0){
				cooldowns[13] = maxTimer / 4;
				Properties.glowPaintValue += 5;
				if(Properties.glowPaintValue > 100){
					Properties.glowPaintValue = 100;
				}
			}
		}
		if(keySet.get(KeyEvent.VK_N)){
			if(cooldowns[14] == 0){
				cooldowns[14] = maxTimer / 2;
				Properties.imageFlag = true;
			}
		}
		if(keySet.get(KeyEvent.VK_B)){
			if(cooldowns[15] == 0){
				cooldowns[15] = maxTimer / 2;
				Properties.diamondGlow = !Properties.diamondGlow;
			}
		}
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			Properties.lmbHeld = true;
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			Properties.rmbHeld = true;
		}
		if(e.getButton() == MouseEvent.BUTTON2){
			Point p = e.getPoint();
			if(Properties.lastClick == null){
				Properties.lastClick = p;
			}
			else{
				Properties.walls.add(new Wall(Properties.lastClick.x,Properties.lastClick.y,p.x,p.y));
				Properties.lastClick = null;
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
		Properties.lmbHeld = false;
		Properties.rmbHeld = false;
	}
	@Override
	public void mouseExited(MouseEvent e) {
		Properties.lmbHeld = false;
		Properties.rmbHeld = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}
