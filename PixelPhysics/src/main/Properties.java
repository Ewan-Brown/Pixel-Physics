package main;

import java.awt.Point;
import java.util.Random;

public class Properties {

	public static double gravityStrength = 0.007;
	public static final double[] frictions = {0.0001,0.01,0.1};
	public static final double[] pulls = {.01,1,5};
	public static double frictionStrength = frictions[1];
	public static double pullStrength = pulls[1];
	public static boolean lmbHeld = false;
	public static boolean rmbHeld = false;
	public static Point lastClick = null;
	public static boolean showStats = true;
	public static int RGB_switch = 1;
	public static int cores = 1;
	public static boolean LSD = false;
	public static boolean pixelized = false;
	public static double timeSpeed = 1;
	public static long updateDelay = 10;
	public static int[] RGB = new int[3];
	public static boolean compound = true;
	public static int shiftAmount = 1;
	public static double glowStrength = 100;
	public static boolean glow = false;
	public static int size = 1;
	public static int maxSize = 10;
	public static int maxPixels = 0;
	public static boolean paint = false;
	public static boolean rainbow = false;
	public static Random rand = new Random();


	public enum paintValue{
		paint1,
		paint2,
		paint3;

	}
	public static void switchShift(){
		RGB_switch = rand.nextInt(3);
		shiftAmount = -1;
		if(getPositive(RGB_switch)){
			shiftAmount = 1;
		}
	}
	public static boolean getPositive(int index){
		if(RGB[index] < 127){
			return true;
		}
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
		else if(rand.nextInt(100) < 1){
			switchShift();
		}
	}

}

