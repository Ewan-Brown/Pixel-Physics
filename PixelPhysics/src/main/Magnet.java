package main;

public class Magnet
{
	int x;
	int y;
	int life = 1000;
	double pull = 0;
	public Magnet(int x, int y){
		pull = Properties.doubles[Properties.PULL][1];
	}

}
