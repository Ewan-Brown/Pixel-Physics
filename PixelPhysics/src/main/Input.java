package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stuff.Wall;

public class Input implements KeyListener,MouseListener,ChangeListener{
	public static int[] cooldowns = new int[30];
	public static BitSet keySet = new BitSet(256);
	public static final int maxTimer = 60;
	public static void updateThemkeys(){
		for(int i = 0 ; i < cooldowns.length; i++){
			cooldowns[i]--;
			if(cooldowns[i] < 0)
				cooldowns[i] = 0;
		}
		if(keySet.get(KeyEvent.VK_UP))
			Properties.timeSpeed += 0.01;
		if(keySet.get(KeyEvent.VK_DOWN)){
			Properties.timeSpeed -= 0.01;
			if(Properties.timeSpeed < 0.01)
				Properties.timeSpeed = 0.01;
		}
		if(keySet.get(KeyEvent.VK_RIGHT))
			Properties.timeSpeed = 0.5;
		if(keySet.get(KeyEvent.VK_LEFT))
			Properties.timeSpeed = 0;
		if(keySet.get(KeyEvent.VK_W)){
			if(Properties.pullStrength <= 1)
				Properties.pullStrength += 0.005;
			else
				Properties.pullStrength += Math.abs(Properties.pullStrength) / 50D;
			if(Properties.pullStrength > Properties.pulls[2])
				Properties.pullStrength = Properties.pulls[2];

		}
		if(keySet.get(KeyEvent.VK_R)){
			Properties.pullStrength = Properties.pulls[1];
		}
		if(keySet.get(KeyEvent.VK_F)){
			Properties.frictionStrength = Properties.frictions[1];
		}

		if(keySet.get(KeyEvent.VK_S)){
			if(Properties.pullStrength <= 1)
				Properties.pullStrength -= 0.005;
			else
				Properties.pullStrength -= Math.abs(Properties.pullStrength) / 50D;
			if(Properties.pullStrength < Properties.pulls[0])
				Properties.pullStrength = Properties.pulls[0];

		}
		if(keySet.get(KeyEvent.VK_A)){
			if(Properties.frictionStrength < 0.003)
				Properties.frictionStrength -= 0.00003;
			Properties.frictionStrength -= Properties.frictionStrength / 50D;
			if(Properties.frictionStrength < Properties.frictions[0])
				Properties.frictionStrength = Properties.frictions[0];
		}
		if(keySet.get(KeyEvent.VK_D)){
			if(Properties.frictionStrength < 0.003)
				Properties.frictionStrength += 0.00008;
			else
				Properties.frictionStrength += Properties.frictionStrength / 50D;
			if(Properties.frictionStrength > Properties.frictions[2])
				Properties.frictionStrength = Properties.frictions[2];
		}
		if(keySet.get(KeyEvent.VK_COMMA))
			if(cooldowns[0] == 0){
				cooldowns[0] = maxTimer / 2;
				Properties.size--;
				if(Properties.size < 1)
					Properties.size = 1;
			}
		if(keySet.get(KeyEvent.VK_PERIOD))
			if(cooldowns[1] == 0){
				cooldowns[1] = maxTimer / 2;
				Properties.size++;
				if(Properties.size > Properties.maxSize)
					Properties.size = Properties.maxSize;
			}
		if(keySet.get(KeyEvent.VK_G))
			if(cooldowns[2] == 0){
				cooldowns[2] = maxTimer;
				Properties.glow = !Properties.glow;
			}
		if(keySet.get(KeyEvent.VK_J))
			if(cooldowns[3] == 0){
				cooldowns[3] = maxTimer;
				Properties.compound = !Properties.compound;
			}
		if(keySet.get(KeyEvent.VK_L))
			if(cooldowns[4] == 0){
				cooldowns[4] = maxTimer / 8;
				Properties.glowStrength -= 2;
				if(Properties.glowStrength < 2)
					Properties.glowStrength = 2;
			}
		if(keySet.get(KeyEvent.VK_O))
			if(cooldowns[5] == 0){
				cooldowns[5] = maxTimer / 8;
				Properties.glowStrength += 2;
				if(Properties.glowStrength > 255)
					Properties.glowStrength = 255;
			}
		if(keySet.get(KeyEvent.VK_U))
			if(cooldowns[6] == 0){
				cooldowns[6] = maxTimer;
				Properties.pixelized= !Properties.pixelized;
			}
		if(keySet.get(KeyEvent.VK_Y))
			if(cooldowns[7] == 0){
				cooldowns[7] = maxTimer;
				Properties.LSD = !Properties.LSD;
			}
		if(keySet.get(KeyEvent.VK_P))
			if(cooldowns[8] == 0){
				cooldowns[8] = maxTimer;
				Properties.paint = !Properties.paint;
			}
		if(keySet.get(KeyEvent.VK_E))
			Properties.shiftColor();
		if(keySet.get(KeyEvent.VK_H)){
			Properties.RGB[0] = 255;
			Properties.RGB[1] = 70;
			Properties.RGB[2] = 0;
		}
		if(keySet.get(KeyEvent.VK_V))
			if(cooldowns[9] == 0){
				cooldowns[9] = maxTimer;
				Properties.showStats = !Properties.showStats;
			}
		if(keySet.get(KeyEvent.VK_Z))
			if(cooldowns[12] == 0){
				cooldowns[12] = maxTimer / 4;
				Properties.glowPaintValue -= 5;
				if(Properties.glowPaintValue < 5)
					Properties.glowPaintValue = 5;
			}
		if(keySet.get(KeyEvent.VK_X))
			if(cooldowns[13] == 0){
				cooldowns[13] = maxTimer / 4;
				Properties.glowPaintValue += 5;
				if(Properties.glowPaintValue > 100)
					Properties.glowPaintValue = 100;
			}
		if(keySet.get(KeyEvent.VK_N))
			if(cooldowns[14] == 0){
				cooldowns[14] = maxTimer / 2;
				Properties.imageFlag = true;
			}
		if(keySet.get(KeyEvent.VK_B))
			if(cooldowns[15] == 0){
				cooldowns[15] = maxTimer / 2;
				Properties.diamondGlow = !Properties.diamondGlow;
			}
		if(keySet.get(KeyEvent.VK_M))
			if(cooldowns[16] == 0){
				cooldowns[16] = maxTimer / 2;
				Properties.fall = !Properties.fall;
			}
		if(keySet.get(KeyEvent.VK_Q)){
			if(cooldowns[17] == 0){
				cooldowns[17] = maxTimer / 2;
				Properties.planetMode = !Properties.planetMode;
			}
		}
		if(keySet.get(KeyEvent.VK_CLOSE_BRACKET)){
			if(Properties.trueGravity < 1){
				Properties.trueGravity += 0.003;
			}
			Properties.trueGravity += Properties.trueGravity / 50D;
			if(Properties.trueGravity > Properties.gravities[2]){
				Properties.trueGravity = Properties.gravities[2];
			}
		}
		if(keySet.get(KeyEvent.VK_OPEN_BRACKET)){
			if(Properties.trueGravity < 1){
				Properties.trueGravity -= 0.003;
			}
			Properties.trueGravity -= Properties.trueGravity / 50D;
			if(Properties.trueGravity < Properties.gravities[0]){
				Properties.trueGravity = Properties.gravities[0];
			}
		}
		if(keySet.get(KeyEvent.VK_1)){
			if(cooldowns[17] == 0){
				cooldowns[17] = maxTimer / 2;
				Properties.singleColor = !Properties.singleColor;
			}

		}
		if(keySet.get(KeyEvent.VK_2)){
			if(cooldowns[18] == 0){
				cooldowns[18] = maxTimer / 2;
				Properties.rainbowColor = !Properties.rainbowColor;

			}
		}
		if(keySet.get(KeyEvent.VK_3)){
			if(cooldowns[19] == 0){
				cooldowns[19] = maxTimer / 2;
				Properties.gridColor = !Properties.gridColor;

			}
		}
		if(keySet.get(KeyEvent.VK_4)){
			if(cooldowns[20] == 0){
				cooldowns[20] = maxTimer / 2;
				Properties.velocityColor = !Properties.velocityColor;

			}
		}
		if(keySet.get(KeyEvent.VK_5)){
			if(cooldowns[21] == 0){
				cooldowns[21] = maxTimer / 2;
				Properties.directionalColor = !Properties.directionalColor;

			}
		}
		if(keySet.get(KeyEvent.VK_SLASH)){
			if(cooldowns[21] == 0){
				cooldowns[21] = maxTimer / 2;
				Properties.captureFlag = true;
			}
		}

	}

	@Override
	public void keyPressed(final KeyEvent e) {
		keySet.set(e.getKeyCode(),true);
	}


	@Override
	public void keyReleased(final KeyEvent e) {
		keySet.set(e.getKeyCode(),false);
	}

	@Override
	public void keyTyped(final KeyEvent e) {}

	@Override
	public void mouseClicked(final MouseEvent e) {}

	@Override
	public void mouseEntered(final MouseEvent e) {}

	@Override
	public void mouseExited(final MouseEvent e) {
		Properties.lmbHeld = false;
		Properties.rmbHeld = false;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			if(keySet.get(KeyEvent.VK_SPACE)){
				final Point p = e.getPoint();
				if(Properties.lastClick == null)
					Properties.lastClick = p;
				else{
					Properties.walls.add(new Wall(Properties.lastClick.x,Properties.lastClick.y,p.x,p.y));
					Properties.lastClick = null;
				}
			} else
				Properties.lmbHeld = true;
		if(e.getButton() == MouseEvent.BUTTON3)
			if(keySet.get(KeyEvent.VK_SPACE)){
				final Point p = e.getPoint();
				if(Properties.lastClick == null)
					Properties.lastClick = p;
				else{
					Properties.walls.add(new Wall(Properties.lastClick.x,Properties.lastClick.y,p.x,p.y));
					Properties.lastClick = null;
				}
			} else
				Properties.rmbHeld = true;
		if(e.getButton() == MouseEvent.BUTTON2){
			final Point p = e.getPoint();
			if(Properties.lastClick == null)
				Properties.lastClick = p;
			else{
				Properties.walls.add(new Wall(Properties.lastClick.x,Properties.lastClick.y,p.x,p.y));
				Properties.lastClick = null;
			}
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		Properties.lmbHeld = false;
		Properties.rmbHeld = false;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}
}
