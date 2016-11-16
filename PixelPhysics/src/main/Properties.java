package main;

public class Properties {

	public static double gravityStrength = 0.007;
	public static final double[] frictions = {0.0001,0.01,0.1};
	public static final double[] pulls = {.01,1,5};
	public static double frictionStrength = frictions[1];
	public static double pullStrength = pulls[1];
	public static int cores = 1;
	public static boolean LSD = false;
	public static boolean pixelized = false;
	public static double timeSpeed = 1;
	public static long updateDelay = 10;
	public static int[] RGB = new int[3];
	public static boolean compound = true;
	public static int shiftAmount = 1;
	public static double glowRadius = 10;
	public static double glowStrength = 100;
	public static boolean glow = false;
	public static int size = 1;
	public static int maxSize = 10;
	public static int maxPixels = 0;

}
