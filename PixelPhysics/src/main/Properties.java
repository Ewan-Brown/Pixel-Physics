package main;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import stuff.Wall;

public class Properties {

	public static boolean colorGrid = false;
	public static boolean planetMode = false;
	public static boolean compound = true;
	public static int cores = 1;
	public static boolean diamondGlow = false;
	public static final double[] frictions = {0,0.01,0.1};
	public static double frictionStrength = frictions[1];
	public static boolean glow = false;
	public static int glowPaintValue = 10;
	public static double glowStrength = 100;
	public static boolean gravity = false;
	public static double gravityStrength = 0.007;
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
	public static final double[] pulls = {.01,1,5};
	public static double pullStrength = pulls[1];
	public static boolean rainbow = false;
	public static Random rand = new Random();
	public static int[] RGB = new int[3];
	public static int RGB_switch = 1;
	public static boolean rmbHeld = false;
	public static int shiftAmount = 1;
	public static boolean showStats = true;
	public static int size = 1;
	public static double timeSpeed = 1;
	public static long updateDelay = 10;
	public static ArrayList<Wall> walls = new ArrayList<Wall>();
	public static double getValueOf(String s){
		s.toLowerCase();
		double a = 0;
		switch(s){
		case "pull":
			a = pullStrength;
			break;
		case "friction":
			a = pullStrength;
			break;
		default:
			System.out.println("Properties.getValueOf(s) s is not a valid Key, s = "+s);
		}
		return a;
	}
	public static void setValueOf(String s,double value){
		s.toLowerCase();
		switch(s){
		case "pull":
			pullStrength = value;
			break;
		case "friction":
			frictionStrength = value;
			break;
		default:
			System.out.println("Properties.getValueOf(s) s is not a valid Key, s = "+s);
		}
	}
	public static boolean getPositive(final int index){
		if(RGB[index] < 127)
			return true;
		return false;
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

