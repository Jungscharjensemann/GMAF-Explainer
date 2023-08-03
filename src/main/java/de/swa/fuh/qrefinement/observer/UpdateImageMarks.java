package de.swa.fuh.qrefinement.observer;

import java.io.File;

import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;

/**
 * 
 * 
 * Interface to be implemented by observing objects interested in changes of image assets. Part of the implementation of the observer pattern.
 * 
 * @author Nicolas Boch
 *
 */
public interface UpdateImageMarks extends UpdateMarks{

	public void addMark (File fileRef, Rectangle mark);
	
	public void updateImageMark (File fileRef);
	
}
