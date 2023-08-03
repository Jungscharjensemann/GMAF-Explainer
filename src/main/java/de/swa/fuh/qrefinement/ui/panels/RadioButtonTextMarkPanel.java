package de.swa.fuh.qrefinement.ui.panels;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 * 
 * Class implementing a part of the text relevance mark ui, where the user sets, whether the next relevance mark is for a relevant or a non relevant part of the text
 * 
 * @author Nicolas Boch
 *
 */
public class RadioButtonTextMarkPanel extends JPanel {
	
	/**
	 * class variable containing the single instance of this class. Implements singleton pattern.
	 */
	private static RadioButtonTextMarkPanel instance;
	
	/**
	 * radio buttons for selection of relevance / non relevance.
	 */
	private JRadioButton rel, notrel;
	
	/**
	 * 
	 */
	private ButtonGroup group;
	private JLabel relimg, nrelimg;
	
	public static RadioButtonTextMarkPanel getInstance ()
	{
		if (instance == null)
		{
			new RadioButtonTextMarkPanel();
		}
		return instance;
	}
	
	public RadioButtonTextMarkPanel ()
	{
		if (instance == null)
		{
			instance = this;
			GridLayout layout = new GridLayout(2,2);
			layout.setVgap(2);
			layout.setHgap(5);
			setLayout(layout);
			group = new ButtonGroup();
			rel = new JRadioButton("relevant", true);
			rel.setBorder(BorderFactory.createEmptyBorder());
			rel.setActionCommand("REL");
			notrel = new JRadioButton("not relevant");
			notrel.setBorder(BorderFactory.createEmptyBorder());
			notrel.setActionCommand("NREL");
			relimg = new JLabel(new ImageIcon("resources/green.png"));
			nrelimg = new JLabel(new ImageIcon("resources/red.png"));
			group.add(rel);
			group.add(notrel);
			add(relimg);
			add(rel);
			add(nrelimg);
			add(notrel);
		}
	}

	public ButtonGroup getGroup ()
	{
		return group;
	}
}
