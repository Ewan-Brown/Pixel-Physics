package main;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import stuff.Wall;

public class Properties {

	public static boolean compound = true;
	public static int cores = 1;
	public static boolean diamondGlow = false;
	public static boolean directionalColor = false;
	public static boolean mouseColor = false;
	public static boolean fall = false;
	public static double fallStrength = 0.007;
	public static final double[] frictions = {0,0.01,0.1};
	public static double frictionStrength = frictions[1];
	public static boolean glow = false;
	public static boolean fastSqrt = false;
	public static int glowPaintValue = 10;
	public static double glowStrength = 100;
	public static final double[] gravities = {0,1,1,5};
	public static boolean gridColor = false;
	public static boolean imageFlag = false;
	public static boolean isWindows = false;
	public static Point lastClick = null;
	public static boolean lmbHeld = false;
	public static boolean LSD = false;
	public static int maxPixels = 0;
	public static int maxSize = 10;
	public static boolean paint = false;
	public static BufferedImage paintImage = null;
	//TODO Paused is not used yet, maybe use it for when tabbed out?
	public static boolean paused;
	public static boolean pixelized = false;
	public static boolean planetMode = false;
	public static final double[] pulls = {.01,1,5};
	public static double pullStrength = pulls[1];
	public static boolean rainbowColor = false;
	public static Random rand = new Random();
	public static int[] RGB = new int[3];
	public static int RGB_switch = 1;
	public static boolean rmbHeld = false;
	public static int shiftAmount = 1;
	public static boolean showStats = true;
	public static boolean singleColor = true;
	public static int size = 1;
	public static double timeSpeed = 1;
	public static double trueGravity = gravities[1];
	public static long updateDelay = 17000000;
	public static boolean velocityColor = false;
	public static ArrayList<Wall> walls = new ArrayList<Wall>();
	public static boolean captureFlag = false;
	public static boolean getPositive(final int index){
		if(RGB[index] < 127)
			return true;
		return false;
	}
	public static boolean getValueOfBool(String s){
		switch(s){
		case "gridcolor":
			return gridColor;
		case "singlecolor":
			return singleColor;
		case "velocitycolor":
			return velocityColor;
		case "rainbowcolor":
			return rainbowColor;
		case "directionalcolor":
			return directionalColor;
		case "diamondglow":
			return diamondGlow;
		case "mousecolor":
			return mouseColor;
		default: 
			return false;
		}
	}
	public static double getValueOfDouble(String s){
		s.toLowerCase();
		double a = 0;
		switch(s){
		case "pull":
			return pullStrength;
		case "pullMin":
			return pulls[0];
		case "pullMax":
			return pulls[2];
		case "friction":
			return frictionStrength;
		case "frictionMin":
			return frictions[0];
		case "frictionMax":
			return frictions[2];
		case "time":
			return timeSpeed;
		case "delay":
			if(planetMode){
				return updateDelay / 1D;
			}
			return updateDelay;
		case "gravity":
			return trueGravity;
		case "gravityMin":
			return gravities[0];
		case "gravityMax":
			return gravities[1];
		default:
			System.out.println("Properties.getValueOf(s) s is not a valid Key, s = "+s);
			System.exit(0);
		}
		return a;
	}
	public static void setValueOfBool(String s, boolean value){
		switch(s){
		case "gridcolor":
			gridColor = value;
			break;
		case "singlecolor":
			singleColor = value;
			break;
		case "velocitycolor":
			velocityColor = value;
			break;
		case "rainbowcolor":
			rainbowColor = value;
			break;
		case "directionalcolor":
			directionalColor = value;
		case "diamondglow":
			diamondGlow = value;
		case "mousecolor":
			mouseColor = value;
		default: 
			break;
		}
	}
	public static void setValueOfDouble(String s,double value){
		value /= 100;
		s.toLowerCase();
		switch(s){
		case "pull":
			pullStrength = ((pulls[2] - pulls[0]) * value) + pulls[0];
			break;
		case "friction":
			frictionStrength = frictions[2] * value;
			break;
		default:
			System.out.println("Properties.getValueOf(s) s is not a valid Key, s = "+s);
			System.exit(0);
		}
	}
	public static void shiftColor(){
		RGB[RGB_switch] += shiftAmount;
		if(RGB[RGB_switch] > 255){
			RGB[RGB_switch] = 255;
			switchShift();
		}
		else if(RGB[RGB_switch] < 1){
			RGB[RGB_switch] = 1;
			switchShift();
		}
		else if(rand.nextInt(100) < 1)
			switchShift();
	}
	public static void switchShift(){
		RGB_switch = rand.nextInt(3);
		shiftAmount = -1;
		if(getPositive(RGB_switch))
			shiftAmount = 1;
	}

}

