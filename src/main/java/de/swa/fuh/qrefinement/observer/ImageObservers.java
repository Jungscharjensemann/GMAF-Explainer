package de.swa.fuh.qrefinement.observer;

import java.io.File;
import java.util.Vector;

import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;


/**
 * 
 * 
 * Concrete observer container class to manage observing objects interested in changes of image assets (e.g. addition or deletion of relevance marks). The class is responsible to inform all registered observing objects. Part of the implementation of the observer pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class ImageObservers extends AbstractObserver{

	/**
	 * Class variable for single instance of this class. Implementation of singleton pattern.
	 */
	private static ImageObservers instance;
	
	/**
	 * Container for all registered observing objects
	 */
	private Vector<UpdateImageMarks> observerObjects = new Vector<UpdateImageMarks>();
	
	private ImageObservers () {}
	
	public static ImageObservers getInstance ()
	{
		if (instance == null)
		{
			instance = new ImageObservers();
		}
		return instance;
	}
	
	public void subscribe (UpdateMarks subscriber)
	{
		if (!(subscriber instanceof UpdateImageMarks))
		{
			throw new IllegalArgumentException ("Only objects implementing image update interface are allowed here.");
		}
		if (!observerObjects.contains(subscriber))
		{
			observerObjects.add((UpdateImageMarks)subscriber);
		}
	}
	
	public void unsubscribe (UpdateMarks subscriber)
	{
		if (observerObjects.contains(subscriber))
		{
			observerObjects.remove(subscriber);
		}
	}
	
	public void notifyAdd (File f, Rectangle mark)
	{
		for (UpdateImageMarks u : observerObjects)
		{
			u.addMark(f, mark);
		}
	}
	
	public void notifyChange (File f)
	{
		for (UpdateImageMarks u : observerObjects)
		{
			u.updateImageMark(f);
		}
	}
	
}
