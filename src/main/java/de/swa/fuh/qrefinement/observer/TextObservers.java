package de.swa.fuh.qrefinement.observer;

import java.io.File;
import java.util.Vector;

import de.swa.fuh.qrefinement.ui.datamodel.TextMark;

/**
 * 
 * 
 * Concrete observer container class to manage observing objects interested in changes of text assets (e.g. addition or deletion of relevance marks). The class is responsible to inform all registered observing objects. Part of the implementation of the observer pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class TextObservers extends AbstractObserver{

	/**
	 * Class variable for single instance of this class. Implementation of singleton pattern.
	 */
	private static TextObservers instance;
	
	/**
	 * Container for all registered observing objects
	 */
	private Vector <UpdateTextMarks> observerObjects = new Vector <UpdateTextMarks> ();
	
	private TextObservers () {}
	
	public static TextObservers getInstance ()
	{
		if (instance == null)
		{
			instance = new TextObservers();
		}
		return instance;
	}
	
	public void subscribe (UpdateMarks subscriber)
	{
		if (!(subscriber instanceof UpdateTextMarks))
		{
			throw new IllegalArgumentException ("Only objects implementing text update interface are allowed here.");
		}
		if (!observerObjects.contains(subscriber))
		{
			observerObjects.add((UpdateTextMarks)subscriber);
		}
	}
	
	public void unsubscribe (UpdateMarks subscriber)
	{
		if (observerObjects.contains(subscriber))
		{
			observerObjects.remove(subscriber);
		}
	}
	
	public void notifyAdd (File f, TextMark mark)
	{
		for (UpdateTextMarks u : observerObjects)
		{
			u.addMark(f, mark);
		}
	}
	
	public void notifyChange (File f)
	{
		for (UpdateTextMarks u : observerObjects)
		{
			u.updateTextMark(f);
		}
	}
	
}
