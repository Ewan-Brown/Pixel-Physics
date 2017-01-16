package main;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
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

import workers.InputThread;

public class MainClass {
	static int height = 1080;
	static final int sWidth = 200;
	static int width = 1920;
	static JSlider getSlider(final JOptionPane optionPane,final int min, final int max, final int def) {
		final JSlider slider = new JSlider(0,max,def);
		slider.setMajorTickSpacing((int)(max / 2D));
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		final ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent changeEvent) {
				final JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting())
					optionPane.setInputValue(new Integer(theSlider.getValue()));
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}
	public static void main(final String[] args){
		System.setProperty("sun.java2d.opengl","True");
		final FileDialog fd = new FileDialog((java.awt.Frame) null);
		fd.setTitle("Choose an image");
		fd.setVisible(true);
		final File f = new File(fd.getDirectory() + fd.getFile());
		if(fd.getDirectory() == null || fd.getFile() == null)
			System.exit(0);
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
			img.getType();
		} catch (IOException | NullPointerException e) {
			String extension = "";

			final int i = f.getName().lastIndexOf('.');
			if (i > 0)
				extension = f.getName().substring(i+1);
			JOptionPane.showMessageDialog(new JFrame(),
					"The file chosen was not a readable image!\n"
							+ 	"File type '."+extension +"' is not an image you goof");
			System.exit(0);
		}
		Properties.paintImage = img;
		final JOptionPane infoPane = new JOptionPane();
		Object[] list = null;
		try {
			list = TextFileReader.readFile().toArray();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		infoPane.setMessage(list);
		infoPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		infoPane.setOptionType(JOptionPane.PLAIN_MESSAGE);
		final JDialog infoDialog = infoPane.createDialog(new JFrame(), "Info");
		infoDialog.setResizable(true);
		infoDialog.setModal(false);
		final JOptionPane optionPane = new JOptionPane();
		final JSlider slider = getSlider(optionPane,1,3000,2000);
		final JSlider slider2 = getSlider(optionPane,1,10,1);
		optionPane.setMessage(new Object[] { "Select a value: ", slider,slider2 });
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		final Object[] options = { "OK", "CANCEL","INFO" };
		optionPane.setOptions(options);
		final JDialog dialog2 = optionPane.createDialog(new JFrame(), "Pixel Physics");
		dialog2.setVisible(true);
		dialog2.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if(optionPane.getValue() == "CANCEL" || optionPane.getValue() == null)
			System.exit(0);
		final JFrame frame = new JFrame("Pixel Physics");
		final GamePanel gamePanel = new GamePanel(width,height,slider.getValue(),slider2.getValue());
		gamePanel.setSize(width, height);
		gamePanel.setLayout(null);
		//TODO dear god this is disgusting please fix it
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(gamePanel);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		gamePanel.init();
		Thread t1 = new Thread(new InputThread());
		t1.start();
		if(optionPane.getValue() == "INFO")
			infoDialog.setVisible(true);
		long l1 = System.nanoTime();
		while(true){
			long l2 = System.nanoTime();
			if(l2 - l1 > Properties.getValueOfDouble("delay")){
				gamePanel.update();
				l1 = l2;
			}

		}
	}
	public static BufferedImage scale(final BufferedImage sbi, final int imageType, final int dWidth, final int dHeight, final double fWidth, final double fHeight) {
		BufferedImage dbi = null;
		if(sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			final Graphics2D g = dbi.createGraphics();
			final AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}


}
