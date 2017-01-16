package workers;

import main.Input;

public class InputThread implements Runnable{

	@Override
	public void run() {
		while(true){
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Input.updateThemkeys();
		}
	}

}
