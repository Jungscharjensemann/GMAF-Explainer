package de.swa.fuh.qrefinement.ui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import de.swa.fuh.qrefinement.command.BackToMainCardCommand;
import de.swa.fuh.qrefinement.command.RelevanceMarkCommandHistory;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;

/**
 * 
 * 
 * Panel realizing the ui element for the input of relevance marks for an image asset.
 * 
 * @author Nicolas Boch
 *
 */
public class ImageRefinementPanel extends JPanel implements ActionListener{

	/**
	 * class variable for the single instance of this class. Implements singleton pattern.
	 */
	private static ImageRefinementPanel instance;
	
	private ImageRefinementPanel () {}
	
	
	public static ImageRefinementPanel getInstance ()
	{
		if (instance == null)
		{
			instance = new ImageRefinementPanel ();
			instance.init();
		}
		return instance;
	}
	
	private void init ()
	{
		//f = RefinementUIModel.getInstance().getSelectedAsset();
		setLayout(new BorderLayout(5,5));
		setName (Refinement_UI.IMAGEPANEL);
		
		// Button to return to main part of ui 
		JButton b = new JButton("Back to all assets");
		b.setActionCommand("B");
		b.addActionListener(this);
		
		// Toolbar for undo / redo actions
		JToolBar tb = new JToolBar ();
		tb.add(createToolBarItem("Undo", "UND", "resources/arrow_u_turn.png"));
		tb.add(createToolBarItem("Redo","RED", "resources/arrow_right.png"));
		
		add(tb, "North");
		add(new ImageRefinementTable().getPane(), "East");
		add(new JScrollPane(ImageRefinementLabel.getInstance().getLabel()),"Center");
		add (b, "South");
	}
	
	private JComponent createToolBarItem(String text, String action, String icon) {
		JButton b = new JButton(text, new ImageIcon(icon));
		b.setActionCommand(action);
		b.setToolTipText(text);
		b.addActionListener(this);
		return b;
	}
	
	public void actionPerformed (ActionEvent e)
	{
		
		// Back to main part of ui button
		if (e.getActionCommand().equals("B"))
		{
			new BackToMainCardCommand().execute();
		}
		
		// Undo
		if (e.getActionCommand().equals("UND"))
		{
			try
			{
			RelevanceMarkCommandHistory.getInstance().undo();
			}
			catch (IllegalStateException ex)
			{
				JOptionPane.showMessageDialog(Refinement_UI.getInstance(), ex.getMessage());
			}
		}
		
		// redo
		if (e.getActionCommand().equals("RED"))
		{
			try
			{
			RelevanceMarkCommandHistory.getInstance().redo();
			}
			catch (IllegalStateException ex)
			{
				JOptionPane.showMessageDialog(Refinement_UI.getInstance(), ex.getMessage());
			}
		}
	}
}
