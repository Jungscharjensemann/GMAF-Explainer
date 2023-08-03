package de.swa.fuh.qrefinement.ui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledEditorKit;

import de.swa.fuh.qrefinement.observer.TextObservers;
import de.swa.fuh.qrefinement.observer.UpdateTextMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.fuh.qrefinement.ui.listeners.TextMouseListener;

/**
 * Text editor for showing text asset in relevance mark UI
 * 
 * @author Nicolas Boch
 *
 */
public class TextEditor extends JEditorPane implements FocusListener, UpdateTextMarks{

	private StyledEditorKit.BoldAction boldAction;
	private static TextEditor instance;
	private File f;
	
	public static TextEditor getInstance()
	{
		return instance;
	}
	
	public TextEditor ()
	{
		instance = this;
		TextObservers.getInstance().subscribe(this);
		TextMouseListener textmouse = new TextMouseListener ();
		setEditorKit(new StyledEditorKit());
		setEditable(false);
		getCaret().setVisible(true);
		getCaret().addChangeListener(textmouse);
		boldAction = new StyledEditorKit.BoldAction();
		addMouseListener(textmouse);
		addFocusListener(this);
	}
	
	public void setBold(int start, int end)
	{
		if (getDocument().getLength() == 0)
		{
			return;
		}
		if (end >= getDocument().getLength())
		{
			return;
		}
		if (start < 0)
		{
			return;
		}
		if (end <= start)
		{
			return;
		}
		
		setSelectionStart(start);
		setSelectionEnd(end);
		
		boldAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
		setSelectionStart(0);
		setSelectionEnd(0);
	}
	
	public void setTextColor (int start, int end, Color color)
	{
		if (getDocument().getLength() == 0)
		{
			return;
		}
		if (end >= getDocument().getLength())
		{
			return;
		}
		if (start < 0)
		{
			return;
		}
		if (end <= start)
		{
			return;
		}
		
		try
		{
			getHighlighter().addHighlight(start, end, de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(color));
		}
		catch (BadLocationException ble)
		{
			ble.printStackTrace();
		}
		
	}

	public void refresh ()
	{
		if (!de.swa.fuh.qrefinement.qrm.Tools.isImageFile(RefinementUIModel.getInstance().getSelectedAsset()))
		{
			f = RefinementUIModel.getInstance().getSelectedAsset();
			try
			{
			RandomAccessFile rf = new RandomAccessFile(f,"r");
			if (de.swa.fuh.qrefinement.qrm.Tools.getFileExtension(f).equals("json"))
			{
			setContentType("text/javascript");
			}
			else
			{
				setContentType("text/plain");
			}
			
			setText("");
			String line = "";
			String content = "";
			while ((line = rf.readLine()) != null) {
				content += line + "\n";
			}
			setText(content);
			rf.close();
			
			getHighlighter().removeAllHighlights();
			
			for (TextMark m : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
			{
				setTextColor(m.getStart(), m.getEnd(), m.getColor());
				
				if (m.getBold())
				{
					setBold(m.getStart(), m.getEnd());
				}
			}
			}
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		getCaret().setVisible(true);
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMark(File fileRef, TextMark mark) {
		if (f != null)
		{
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			setTextColor(mark.getStart(), mark.getEnd(), mark.getColor());
			setSelectionStart(0);
			setSelectionEnd(0);
		}
		}
		
	}

	@Override
	public void updateTextMark(File fileRef) {
		if (f != null)
		{
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			getHighlighter().removeAllHighlights();
			for (TextMark mark : RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks())
			{
				setTextColor(mark.getStart(), mark.getEnd(), mark.getColor());
			}
		}
		}
		
	}
}
