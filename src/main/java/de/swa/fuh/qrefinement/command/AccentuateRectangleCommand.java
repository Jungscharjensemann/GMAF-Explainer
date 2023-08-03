package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;

/**
 * 
 *
 *This is a command class implementing command pattern. This command points out a specific rectangle in an image by changing the thickness of its border.
 *
 *@author Nicolas Boch
 */
public class AccentuateRectangleCommand implements Command {

	/**
	 * File reference of image file where rectangle is to be drawn thicker
	 */
	private File fileRef;
	
	/**
	 * Index of the rectangle that is to be drawn thicker
	 */
	private int index;
	
	
	/**
	 * Reference to rectangle object whose border is to be drawn thicker
	 */
	private Rectangle rect;
	
	public AccentuateRectangleCommand (File f, int index)
	{
		this.fileRef = f;
		this.index = index;
		
		if (this.index < 0 || this.index >= RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().size())
		{
			throw new IllegalArgumentException ();
		}
		
		this.rect = RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().get(this.index);
	}
	
	@Override
	public void execute() {
		RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).setThick(rect);

	}

}
