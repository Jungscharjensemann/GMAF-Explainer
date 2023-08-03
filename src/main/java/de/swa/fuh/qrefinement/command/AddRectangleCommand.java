package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;

/**
 * 
 * 
 * This is a command class implementing command pattern. This command adds an rectangular relevance mark to an image
 * 
 * @author Nicolas Boch
 */
public class AddRectangleCommand implements UndoableCommand {

	
	/**
	 * Reference to object of the rectangle that is added to an image
	 */
	private Rectangle addedRectangle;
	
	/**
	 * File reference to file of the image where the rectangle is added
	 */
	private File fileRef;
	
	public AddRectangleCommand (File f, Rectangle r)
	{
		this.fileRef = f;
		this.addedRectangle = r;
	}
	@Override
	public void execute() {
		RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).addRectangle(addedRectangle);
	}

	@Override
	public void undo() {
		if (!fileRef.getAbsolutePath().equals(RefinementUIModel.getInstance().getSelectedAsset().getAbsolutePath()))
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).deleteRectangle(addedRectangle);
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
