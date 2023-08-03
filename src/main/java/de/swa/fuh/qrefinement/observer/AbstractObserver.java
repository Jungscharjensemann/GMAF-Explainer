package de.swa.fuh.qrefinement.observer;

import java.io.File;

/**
 * 
 * 
 * Class defining an interface for concrete observer container classes. Part of the implementation of the observer pattern.
 * 
 * @author Nicolas Boch
 *
 */
public abstract class AbstractObserver {

	public abstract void subscribe (UpdateMarks subscriber);
	
	public abstract void unsubscribe (UpdateMarks subscriber);
	
	public abstract void notifyChange (File fileRef);
	
}
