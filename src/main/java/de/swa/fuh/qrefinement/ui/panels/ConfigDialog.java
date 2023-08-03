package de.swa.fuh.qrefinement.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import de.swa.fuh.qrefinement.command.ChangeAlgorithmConfig;
import de.swa.fuh.qrefinement.command.SelectQueryCommandHistory;
import de.swa.fuh.qrefinement.logic.RelationshipWeightRocchioRelevanceFeedback;
import de.swa.fuh.qrefinement.logic.RelevanceFeedback;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;

/**
 * 
 * 
 * Class implementing a ui dialog to configure the rf algorithm to be used to construct new queries
 * 
 * @author Nicolas Boch
 *
 */
public class ConfigDialog extends JDialog implements ItemListener, ActionListener{
	
	/**
	 * Label to show the name of weight params in config dialog
	 */
	private JLabel a,b,c;
	
	/**
	 * Text field to input the value of weight params in the dialog
	 */
	private JTextField tfa, tfb, tfc;
	
	/**
	 * button group to control a bunch of related radio buttons. There will be a radio button for each available algorithm.
	 */
	private ButtonGroup group;
	
	/**
	 * class variable for the single instance of this class. Implementation of singleton pattern.
	 */
	private static ConfigDialog instance; 

	public ConfigDialog (JFrame frame)
	{
		super(frame);
		if (instance != null)
		{
			instance.dispose();
		}
		instance = this;
		int widthframe = frame.getWidth();
		int heightframe = frame.getHeight();
		setLayout(new BorderLayout(5,5));
		setTitle ("Config Algorithms");
		setSize(500,200);
		setLocation(widthframe/2 - getWidth()/2, heightframe/2 - getHeight()/2);
		setModal(true);
		group = new ButtonGroup();
		RelevanceFeedback rf = RefinementUIModel.getInstance().getAlgorithm();
		Class <?> classobj = rf.getClass();
		String selectedalg = "";
		
		JPanel algorithmradiobuttons = new JPanel();
		algorithmradiobuttons.setLayout(new BoxLayout(algorithmradiobuttons, BoxLayout.Y_AXIS));
		
		for (String s : RefinementUIModel.getInstance().getAlgorithmNames())
		{
			JRadioButton b = new JRadioButton(s);
			b.setActionCommand(s);
			if (s.equals("Adapted Rocchio Algorithm"))
			{
				b.addItemListener(this);
			}
			group.add(b);
			algorithmradiobuttons.add(Box.createRigidArea(new Dimension(0,10)));
			algorithmradiobuttons.add(b);
			algorithmradiobuttons.add(Box.createRigidArea(new Dimension(0,10)));
			
			if (RefinementUIModel.getInstance().getAlgorithms().get(s) == classobj)
			{
				selectedalg = s;
			}
		}
		
		
		Enumeration<AbstractButton> buttons = group.getElements();
		
		while (buttons.hasMoreElements())
		{
			AbstractButton button = buttons.nextElement();
			
			if (button.getActionCommand().equals(selectedalg))
			{
				button.setSelected(true);
			}
		}
		
		add(algorithmradiobuttons, "Center");
		
		JPanel parameter = new JPanel();
		parameter.setLayout(new GridLayout(3,2,5,5));
		
		
		 a = new JLabel ("Weight of old query");
		 b = new JLabel ("Weight of relevant parts");
		 c = new JLabel ("Weight of nonrelevant parts");
		
		 NumberFormat format = NumberFormat.getInstance();
		 format.setMaximumFractionDigits(2);
		 format.setMaximumIntegerDigits(1);
		 
		 tfa = new JTextField();
		 //tfa = new JFormattedTextField (format);
		 tfa.setText(String.valueOf(RefinementUIModel.getInstance().getA()));
		 
		 tfb = new JTextField();
		 //tfb = new JFormattedTextField (format);
		 tfb.setText(String.valueOf(RefinementUIModel.getInstance().getB()));
		 
		 tfc = new JTextField();
		 //tfc = new JFormattedTextField (format);
		 tfc.setText(String.valueOf(RefinementUIModel.getInstance().getC()));
		 
		 parameter.add(a);
		 a.setLabelFor(tfa);
		 parameter.add(tfa);
		 
		 parameter.add(b);
		 b.setLabelFor(tfb);
		 parameter.add(tfb);
		 
		 parameter.add(c);
		 c.setLabelFor(tfc);
		 parameter.add(tfc);
		 
		 add(parameter, "East");
		 
		 if (group.getSelection().getActionCommand().equals(RelationshipWeightRocchioRelevanceFeedback.getName()))
		 {
			 a.setVisible(true);
			 b.setVisible(true);
			 c.setVisible(true);
			 tfa.setVisible(true);
			 tfb.setVisible(true);
			 tfc.setVisible(true);
			 
		 }
		 else
		 {
			 a.setVisible(false);
			 b.setVisible(false);
			 c.setVisible(false);
			 tfa.setVisible(false);
			 tfb.setVisible(false);
			 tfc.setVisible(false);
		 }
		 
		 JPanel buttonpanel = new JPanel();
		 buttonpanel.setLayout(new BoxLayout(buttonpanel,BoxLayout.X_AXIS));
		 
		 JButton b1 = new JButton ("Discard changes");
		 b1.setActionCommand("ds");
		 b1.addActionListener(this);
		 JButton b2 = new JButton ("Save changes");
		 b2.setActionCommand("s");
		 b2.addActionListener(this);
		 
		 buttonpanel.add(b1);
		 buttonpanel.add(Box.createRigidArea(new Dimension(15,0)));
		 buttonpanel.add(b2);
		 
		 add(buttonpanel,"South");
		 
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		if (a != null && b != null && c != null && tfa != null && tfb != null && tfc != null)
		{
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			 a.setVisible(true);
			 b.setVisible(true);
			 c.setVisible(true);
			 tfa.setVisible(true);
			 tfb.setVisible(true);
			 tfc.setVisible(true);
		}
		else if (e.getStateChange() == ItemEvent.DESELECTED)
		{
			 a.setVisible(false);
			 b.setVisible(false);
			 c.setVisible(false);
			 tfa.setVisible(false);
			 tfb.setVisible(false);
			 tfc.setVisible(false);
		}
		}
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ds"))
		{
			dispose();
		}
		
		if (e.getActionCommand().equals("s"))
		{
			ChangeAlgorithmConfig command;
			try
			{
			
				if (group.getSelection().getActionCommand().equals(RelationshipWeightRocchioRelevanceFeedback.getName()))
				{
					double a = Double.parseDouble(tfa.getText());
					double b = Double.parseDouble(tfb.getText());
					double c = Double.parseDouble(tfc.getText());
					command = new ChangeAlgorithmConfig(group.getSelection().getActionCommand(), a, b,c);
				}
				else
				{
					command = new ChangeAlgorithmConfig(group.getSelection().getActionCommand());
				}
				
				SelectQueryCommandHistory.getInstance().add(command);
				dispose();
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(Refinement_UI.getInstance(), "Some not proper formatted numbers in input field found");
			}
			catch (IllegalArgumentException ex)
			{
				JOptionPane.showMessageDialog(Refinement_UI.getInstance(), ex.getMessage());
			}
		}
		
	}
}
