package de.swa.fuh.qrefinement.ui.listeners;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.swa.fuh.qrefinement.command.AddTextMarkCommand;
import de.swa.fuh.qrefinement.command.RelevanceMarkCommandHistory;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.fuh.qrefinement.ui.panels.RadioButtonTextMarkPanel;
import de.swa.fuh.qrefinement.ui.panels.TextEditor;

/**
 * 
 * 
 * Class implementing listeners for ui events in text relevance mark ui. The type of events that this listener reacts to are mouse events and state change events concerning the caret in the text editor.
 * 
 * @author Nicolas Boch
 *
 *
 */
public class TextMouseListener implements MouseListener, ChangeListener{
	
	/**
	 * color of the text relevance mark to be constructed
	 */
	private Color color;
	//private int count;
	private boolean pressed = false;

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		String s = RadioButtonTextMarkPanel.getInstance().getGroup().getSelection().getActionCommand();
		
		if (s.equals("REL"))
		{
			color = Color.green;
			
		}
		if (s.equals("NREL"))
		{
			color = Color.red;
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (pressed)
		{
			addTextMark();
			pressed = !pressed;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void addTextMark ()
	{
		TextEditor ed = TextEditor.getInstance();
			if (ed.getSelectionEnd() > ed.getSelectionStart())
			{
				RelevanceMarkCommandHistory.getInstance().add(new AddTextMarkCommand(RefinementUIModel.getInstance().getSelectedAsset(), new TextMark(ed.getSelectionStart(), ed.getSelectionEnd(), color)));
			}
	}



	@Override
	public void stateChanged(ChangeEvent e) {
		pressed = true;
	
	}


	
}
