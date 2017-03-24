package main;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Logger implements Runnable{
	long birthTime = 0; 
	public ArrayList<String> logLines = new ArrayList<String>();
	public DecimalFormat df = new DecimalFormat("00");

	public Logger(){
		birthTime = System.currentTimeMillis();
	}

	public void writeLine(String string){
		long n = System.currentTimeMillis() - birthTime;
		double sec = ((double)n / 1000D);
		double min = (sec / 60D);
		String m = df.format(min);
		String s = df.format(sec % 60);
		String line = "["+m+":"+s+"] - "+string;
		logLines.add(line);
	}

	@Override
	public void run() {
		while(true){
			if(!logLines.isEmpty()){
				System.out.println(logLines.get(0));
				logLines.remove(0);
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
