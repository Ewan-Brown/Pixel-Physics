package stuff;

public class OccupiedArray {

	boolean[][] occupiedArray = new boolean[1920][1080];
	public synchronized boolean get(int x, int y){
		return occupiedArray[x][y];
	}
	public synchronized void set(int x, int y,boolean flag){
		occupiedArray[x][y] = flag;
	}
	
}
