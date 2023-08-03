package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command deletes an rectangular relevance mark in an image.
 * 
 * @author Nicolas Boch
 *
 */
public class DeleteRectangleCommand implements UndoableCommand {

	/**
	 * File reference to image file where the rectangle is deleted
	 */
	private File fileRef;
	
	/**
	 * Instance of rectangle that is deleted
	 */
	private Rectangle deletedRectangle;
	
	public DeleteRectangleCommand (File f, int index)
	{
		this.fileRef = f;
		
		if (index < 0 || index >= RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().size())
		{
			throw new IllegalArgumentException();
		}
		
		this.deletedRectangle = RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().get(index);
	}
	
	@Override
	public void execute() {
		RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).deleteRectangle(deletedRectangle);

	}

	@Override
	public void undo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).addRectangle(deletedRectangle);
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
