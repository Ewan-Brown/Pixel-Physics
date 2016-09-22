package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;

public class OptionPanel extends JPanel{

	/**
	 * Create the panel.
	 */
	JSlider slider_friction;
	JSlider slider_push;
	JSlider slider_Red;
	JSlider slider_Green;
	JSlider slider_Blue;
	JToggleButton tglRandColors;
	JButton reset_friction;
	JButton reset_pull;
	public final int frictionMAX = 2;
	public final int pushMAX = 100;
	public OptionPanel() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_2 = new JPanel();
		add(panel_2);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel = new JPanel();
		panel_2.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lblFriction = new JLabel("Friction");
		panel.add(lblFriction);
		
		slider_friction = new JSlider();
		panel.add(slider_friction);
		slider_friction.setMaximum(pushMAX);
		JLabel lblPush = new JLabel("Push");
		panel.add(lblPush);
		
		slider_push = new JSlider();
		panel.add(slider_push);
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		reset_friction = new JButton("Reset Friction");
		panel_3.add(reset_friction, BorderLayout.NORTH);
		
		reset_pull = new JButton("Reset Friction");
		panel_3.add(reset_pull, BorderLayout.SOUTH);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new GridLayout(4, 1, 0, 0));
		
		JLabel lblColor = new JLabel("RGB");
		panel_1.add(lblColor);
		
		slider_Red = new JSlider();
		panel_1.add(slider_Red);
		
		slider_Green = new JSlider();
		panel_1.add(slider_Green);
		
		slider_Blue = new JSlider();
		panel_1.add(slider_Blue);
		
		tglRandColors = new JToggleButton("Toggle Random Colors");
		add(tglRandColors);

	}
	public static void main(String[] args){
		JFrame f = new JFrame();
		f.getContentPane().add(new OptionPanel());
		f.setVisible(true);
		f.setSize(500, 500);
	}
	public void addChangeLister(ChangeListener e){
		slider_friction.addChangeListener(e);
	}

}
