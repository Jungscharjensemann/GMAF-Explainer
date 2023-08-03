package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command adds an relevance mark to a text.
 * 
 * @author Nicolas Boch
 *
 */
public class AddTextMarkCommand implements UndoableCommand {

	/**
	 * File reference to the text file where the relevance mark is added
	 */
	private File fileRef;
	
	/**
	 * text mark instance where the relevance mark is added
	 */
	private TextMark addedMark;
	
	public AddTextMarkCommand (File f, TextMark m)
	{
		this.fileRef = f;
		this.addedMark = m;
	}
	
	@Override
	public void execute() {
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).addTextMark(addedMark);

	}

	@Override
	public void undo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).deleteTextMark(addedMark);

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
