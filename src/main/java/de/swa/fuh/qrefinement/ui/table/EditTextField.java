package de.swa.fuh.qrefinement.ui.table;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import de.swa.fuh.qrefinement.ui.panels.TextEditor;
import de.swa.fuh.qrefinement.ui.panels.TextRefinementTable;

/**
 * Implements an editable cell of a JTable
 * 
 * @author Nicolas Boch
 *
 */
public class EditTextField extends JTextField implements KeyListener, FocusListener, MouseListener{

	private int row;
	
	public EditTextField ()
	{
		addKeyListener(this);
		setEditable(false);
		getCaret().setVisible(true);
	}
	
	public void setRow (int rowe)
	{
		row = rowe;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int position = getCaretPosition();
		getCaret().setVisible(true);
		Highlight [] highlights = TextEditor.getInstance().getHighlighter().getHighlights();
		Highlighter highlighter = TextEditor.getInstance().getHighlighter();
		
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			
			
			if (position == getDocument().getLength())
			{
				try
				{
					getDocument().remove(position - 1, 1);
					highlighter.changeHighlight(highlights[row], highlights[row].getStartOffset(), highlights[row].getEndOffset() - 1);
				}
				catch (BadLocationException ble)
				{
					ble.printStackTrace();
				}
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
				if(position == 0 && getDocument().getLength() > 0)
				{
					try
					{
						getDocument().remove(position, 1);
						highlighter.changeHighlight(highlights[row], highlights[row].getStartOffset() + 1, highlights[row].getEndOffset());
					}
					catch (BadLocationException ble)
					{
						ble.printStackTrace();
					}
				}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		getCaret().setVisible(true);
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		TextRefinementTable.getInstance().getColumn("Text").getCellEditor().stopCellEditing();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		getCaret().setVisible(true);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		getCaret().setVisible(true);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
