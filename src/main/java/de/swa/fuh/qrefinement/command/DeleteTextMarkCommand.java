package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command removes a relevance text mark for a text
 * 
 * @author Nicolas Boch
 *
 */
public class DeleteTextMarkCommand implements UndoableCommand{

	/**
	 * File reference to text file where the text mark is removed
	 */
	private File fileRef;
	
	/**
	 * Instance of text mark to be removed
	 */
	private TextMark removedMark;
	
	public DeleteTextMarkCommand (File f, int index)
	{
		this.fileRef = f;
		
		if (index < 0 || index >= RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().size())
		{
			throw new IllegalArgumentException();
		}
		
		this.removedMark = RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().get(index);
		
	}
	
	@Override
	public void execute() {
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).deleteTextMark(removedMark);
		
	}

	@Override
	public void undo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).addTextMark(removedMark);
		
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
