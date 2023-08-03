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
 * Container for relevance mark UI for text documents
 * 
 * @author Nicolas Boch
 *
 */
public class TextRefinementPanel extends JPanel implements ActionListener{

	private static TextRefinementPanel instance;
	private TextRefinementPanel () {}
	
	public static TextRefinementPanel getInstance ()
	{
		if (instance == null)
		{
			instance = new TextRefinementPanel ();
			instance.init();
		}
		return instance;
	}
	
	private void init ()
	{
		setLayout(new BorderLayout(5,5));
		setName(Refinement_UI.TEXTPANEL);
		
		JButton b = new JButton ("Back to all assets");
		b.setActionCommand("B");
		b.addActionListener(this);
		
		JToolBar tb = new JToolBar ();
		tb.add(createToolBarItem("Undo", "UND", "resources/arrow_u_turn.png"));
		tb.add(createToolBarItem("Redo","RED", "resources/arrow_right.png"));
		
		add(tb, "North");
		add(new JScrollPane(new TextEditor()),"Center");
		add(new TextRefinementTable().getPane(),"East");
		add(RadioButtonTextMarkPanel.getInstance(), "West");
		add(b,"South");
		
		
	}
	
	private JComponent createToolBarItem(String text, String action, String icon) {
		JButton b = new JButton(text, new ImageIcon(icon));
		b.setActionCommand(action);
		b.setToolTipText(text);
		b.addActionListener(this);
		return b;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("B"))
		{
			new BackToMainCardCommand().execute();
		}
		
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
