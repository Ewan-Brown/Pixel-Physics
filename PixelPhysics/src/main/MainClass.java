package main;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
	public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if(sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}
	public static void main(String[] args){
		//		System.setProperty("sun.java2d.opengl","True");
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			BufferedImage img = null;
			File selectedFile = fileChooser.getSelectedFile();
			try {
				img = ImageIO.read(selectedFile);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(img.getWidth() > 1500){
				img = scale(img, img.getType(), img.getWidth() / 2, img.getHeight() / 2, 0.5, 0.5);
			}
			Properties.paintImage = img;
		}
		else{
			return;
		}
		JOptionPane infoPane = new JOptionPane();
		Object[] list = null;
		try {
			list = TextFileReader.readFile("text.text").toArray();
		} catch (IOException e) {
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
		if(optionPane.getValue() == "INFO"){
			dialog.setVisible(true);
		}
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
