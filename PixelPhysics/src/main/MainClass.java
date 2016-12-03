package main;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass {
	static int width = 1920;
	static int height = 1080;
	static JSlider sliderPull = new JSlider();
	static JSlider sliderFriction = new JSlider();
	static final int sWidth = 200;
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
		FileDialog fd = new FileDialog((java.awt.Frame) null);
		fd.setVisible(true);
		File f = new File(fd.getDirectory() + fd.getFile());
		if(fd.getDirectory() == null || fd.getFile() == null){
			System.exit(0);
		}
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
			//XXX Test to see if is a readable image
			img.getType();
		} catch (IOException | NullPointerException e) {
			String extension = "";

			int i = f.getName().lastIndexOf('.');
			if (i > 0) {
			    extension = f.getName().substring(i+1);
			}
			JOptionPane.showMessageDialog(new JFrame(),
					"The file chosen was not a readable image!\n" 
				+ 	"File type '."+extension +"' is not an image you goof");
			System.exit(0);
		}
		Properties.paintImage = img;
		System.setProperty("sun.java2d.opengl","True");
		JOptionPane infoPane = new JOptionPane();
		Object[] list = null;
		try {
			list = TextFileReader.readFile().toArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		infoPane.setMessage(list);
		infoPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		infoPane.setOptionType(JOptionPane.PLAIN_MESSAGE);
		JDialog infoDialog = infoPane.createDialog(new JFrame(), "Info");
		infoDialog.setModal(false);
		JOptionPane optionPane = new JOptionPane();
		JSlider slider = getSlider(optionPane,1,1000000,10000);
		JSlider slider2 = getSlider(optionPane,1,10,1);
		optionPane.setMessage(new Object[] { "Select a value: ", slider,slider2 });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		Object[] options = { "OK", "CANCEL","INFO" };
		optionPane.setOptions(options);
		JDialog dialog2 = optionPane.createDialog(new JFrame(), "Pixel Physics");
		dialog2.setVisible(true);
		dialog2.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if(optionPane.getValue() == "CANCEL" || optionPane.getValue() == null){
			System.exit(0);
		}
		JFrame frame = new JFrame("Pixel Physics");
		GamePanel gamePanel = new GamePanel(width,height,slider.getValue(),slider2.getValue());
		gamePanel.setSize(width, height);
		gamePanel.setLayout(null);
		gamePanel.add(sliderPull);
		sliderPull.setBackground(Color.BLACK);
		sliderPull.setMinimum((int) (Properties.pulls[0] * 100));
		sliderPull.setMaximum((int) (Properties.pulls[2] * 100));
		sliderPull.setValue((int) (Properties.pulls[1] * 100));
		gamePanel.add(sliderPull);
		Input in = new Input();
		sliderPull.addChangeListener(in);
		sliderPull.addKeyListener(in);
		sliderPull.setBounds(200, 10, width - 500, 20);
		gamePanel.add(sliderFriction);
		sliderFriction.setBackground(Color.BLACK);
		sliderFriction.setMinimum((int) (Properties.frictions[0] * 100000));
		sliderFriction.setMaximum((int) (Properties.frictions[2] * 100000));
		sliderFriction.setValue((int) (Properties.frictions[1] * 100000));
		gamePanel.add(sliderFriction);
		sliderFriction.addChangeListener(in);
		sliderFriction.addKeyListener(in);
		sliderFriction.setBounds(200, 30, width - 500, 20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(gamePanel);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
		gamePanel.init();
		if(optionPane.getValue() == "INFO"){
			infoDialog.setVisible(true);
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
