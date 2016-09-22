package main;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass {
	static int width = 1000;
	static int height = 1000;
	public static void main(String[] args){
		JOptionPane optionPane = new JOptionPane();
		JSlider slider = getSlider(optionPane);
		optionPane.setMessage(new Object[] { "Select a value: ", slider });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = optionPane.createDialog(new JFrame(), "My Slider");
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel outer = new JPanel();
		JFrame frame = new JFrame();
		outer.setSize(width, height);
		GamePanel gamePanel = new GamePanel(width,height,slider.getValue());

		gamePanel.setSize(width, height);
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
		outer.add(gamePanel);
		gamePanel.init();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(outer);
		frame.setVisible(true);
		frame.pack();
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
	static JSlider getSlider(final JOptionPane optionPane) {
		JSlider slider = new JSlider(0,300000,50000);
		slider.setMajorTickSpacing((int)(300000D / 3D));
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting()) {
					optionPane.setInputValue(new Integer(theSlider.getValue()));
				}
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}


}
