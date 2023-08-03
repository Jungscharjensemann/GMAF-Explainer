package de.swa.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/** progress frame indicating the processing status at startup of the GMAF **/
public class ProgressFrame extends JFrame {
	private JProgressBar progress = new JProgressBar();
	private JLabel message = new JLabel("starting GMAF application");
	
	public ProgressFrame() {
		instance = this;
		setSize(500, 250);
		setUndecorated(true);
		progress.setBackground(Color.white);
		progress.setForeground(new Color(2, 75, 148));
		
		setTitle("GMAF Framework loading...");
		setLayout(new BorderLayout());
		add(message, "South");
		add(new JLabel(new ImageIcon("resources/splash.png")), "North");
		add(progress, "Center");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 500) / 2);
	    int y = (int) ((dimension.getHeight() - 250) / 2);
	    setLocation(x, y);
	    
	    setVisible(true);
	}
	
	public void setProgress(int value, String s) {
		progress.setValue(value);
		message.setText(s);
	}
	
	public void close() {
		setVisible(false);
	}
	
	private static ProgressFrame instance;
	public static ProgressFrame getInstance() {
		return instance;
	}
}
