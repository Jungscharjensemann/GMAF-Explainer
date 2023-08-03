package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;


/**
 * 
 *  
 *	This is a command class implementing command pattern. This command changes an existing relevance mark in a text.
 *
 *  @author Nicolas Boch
 */
public class ChangeTextMarkCommand implements UndoableCommand {

	/**
	 * Reference to text file of the text where a relevance text mark is changed
	 *
	 */
	private File fileRef;
	
	/**
	 * instance of text mark to be changed
	 */
	private TextMark changedMark;
	
	/**
	 * old start index of text relevance mark
	 */
	private int oldStart;
	
	/**
	 * new start index of text relevance mark
	 */
	private int newStart;
	
	/**
	 * old end index of text relevance mark
	 */
	private int oldEnd;
	
	/**
	 * new end index of text relevance mark
	 */
	private int newEnd;
	
	public ChangeTextMarkCommand (File f, int index, int newStart, int newEnd)
	{
		this.fileRef = f;
		
		if (index < 0 || index >= RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().size())
		{
			throw new IllegalArgumentException();
		}
		
		if (newEnd <= newStart)
		{
			throw new IllegalArgumentException();
		}
		
		this.changedMark = RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().get(index);
		this.oldStart = changedMark.getStart();
		this.oldEnd = changedMark.getEnd();
		this.newStart = newStart;
		this.newEnd = newEnd;
		
	}
	@Override
	public void execute() {
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).changePosition(changedMark, newStart, newEnd);

	}

	@Override
	public void undo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).changePosition(changedMark, oldStart, oldEnd);

	}

	@Override
	public void redo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be redone");
		}
		execute();

	}

}
