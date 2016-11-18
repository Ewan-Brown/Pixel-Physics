package main;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass {
	static int width = 1000;
	static int height = 1000;
	public static void main(String[] args){
//		System.setProperty("sun.java2d.opengl","True");
		JOptionPane infoPane = new JOptionPane();
		Object[] list = null;
		try {
			list = TextFileReader.readFile("text.text").toArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		infoPane.setMessage(list);
		infoPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		infoPane.setOptionType(JOptionPane.PLAIN_MESSAGE);
		JDialog dialog = infoPane.createDialog(new JFrame(), "Info");
		dialog.setModal(false);
		JOptionPane optionPane = new JOptionPane();
		JSlider slider = getSlider(optionPane,1,200000,10000);
		JSlider slider2 = getSlider(optionPane,1,10,1);
		optionPane.setMessage(new Object[] { "Select a value: ", slider,slider2 });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
//		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		Object[] options = { "OK", "CANCEL","INFO" };
		optionPane.setOptions(options);
		JDialog dialog2 = optionPane.createDialog(new JFrame(), "My Slider");
		dialog2.setVisible(true);
		dialog2.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if(optionPane.getValue() == "CANCEL" || optionPane.getValue() == null){
			return;
		}
		if(optionPane.getValue() == "INFO"){
			dialog.setVisible(true);
		}
		JPanel outer = new JPanel();
		JFrame frame = new JFrame("Pixel Physics v1.0");
		outer.setSize(width, height);
		GamePanel gamePanel = new GamePanel(width,height,slider.getValue(),slider2.getValue());
		gamePanel.setSize(width, height);
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
		outer.add(gamePanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(outer);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
		gamePanel.init();
		while(true){
			try {
				Thread.sleep(Properties.updateDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gamePanel.update();
		}
	}
	static JSlider getSlider(final JOptionPane optionPane,int min, int max, int def) {
		JSlider slider = new JSlider(0,max,def);
		slider.setMajorTickSpacing((int)(max / 2D));
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		ChangeListener changeListener = new ChangeListener() {
			@Override
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
