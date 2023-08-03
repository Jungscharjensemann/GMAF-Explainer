package de.swa.fuh.qrefinement.observer;

import java.io.File;

import de.swa.fuh.qrefinement.ui.datamodel.TextMark;

/**
 * 
 * 
 * Interface to be implemented by observing objects interested in changes of text assets. Part of the implementation of the observer pattern.
 * 
 * @author Nicolas Boch
 *
 */
public interface UpdateTextMarks extends UpdateMarks{

	public void addMark (File fileRef, TextMark mark);
	
	public void updateTextMark (File fileRef);
	
}
