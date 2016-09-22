package main;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	static int width = 1000;
	static int height = 1000;
	public static void main(String[] args){
		JPanel outer = new JPanel();
		JFrame frame = new JFrame();
//		OptionPanel p = new OptionPanel();
		outer.setSize(width, height);
		GamePanel gamePanel = new GamePanel(width,height,null);
//		GamePanel gamePanel = new GamePanel(width,height,p);

		gamePanel.setSize(width, height);
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
		outer.add(gamePanel);
//		outer.add(p);
		gamePanel.init();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(outer);
		frame.setVisible(true);
		frame.pack();
//		p.addChangeLister(gamePanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		while(true){
			try {
				Thread.sleep(gamePanel.updateDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gamePanel.update();

		}

		
	}
	
	
}
