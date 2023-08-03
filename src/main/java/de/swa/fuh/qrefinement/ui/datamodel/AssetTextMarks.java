package de.swa.fuh.qrefinement.ui.datamodel;

import java.io.File;
import java.util.Vector;

import de.swa.fuh.qrefinement.observer.TextObservers;

/**
 * class containing a number of references to text marks that represent a relevance mark for a text file each. Exactly one instance of this class should exist for each text file in the set of assets.
 * @author Nicolas Boch
 *
 */
public class AssetTextMarks {
	
	/**
	 * Vector of text marks representing all relevance marks for one text asset file
	 */
	private Vector<TextMark> textmarks;
	
	/**
	 * the text asset file for which an instance of the class represents relevance marks
	 */
	private File fileRef;
	
	public AssetTextMarks (Vector<TextMark> r)
	{
		this.textmarks = r;
	}
	
	/**
	 * constructs a new instance of the class setting the file reference to the text file the new instance is representing relevance marks for
	 * @param f a File object of a text file
	 */
	public AssetTextMarks (File f)
	{
		textmarks = new Vector<TextMark>();
		this.fileRef = f;
	}
	
	/**
	 * returns the Vector of relevance marks
	 */
	public Vector<TextMark> getTextMarks ()
	{
		return textmarks;
	}
	
	
	/**
	 * adds a relevance marks to the vector of relevance marks and informs the object that manages observers about this change
	 * @param mark the text mark to be added to the Vector
	 */
	public void addTextMark (TextMark mark)
	{
		textmarks.add(mark);
		TextObservers.getInstance().notifyAdd(fileRef, mark);
	}
	
	
	/**
	 * deletes a relevance marks from the vector of relevance marks and informs the object that manages observers about this change
	 * @param index the index of the text mark to be removed in the vector of rectangles
	 */
	public void deleteTextMark (int index)
	{
		textmarks.remove(index);
		TextObservers.getInstance().notifyChange(fileRef);
	}
	
	public void deleteTextMark (TextMark mark)
	{
		if (textmarks.contains(mark))
		{
			textmarks.remove(mark);
			TextObservers.getInstance().notifyChange(fileRef);
		}
	}
	
	public void changeBold (int index)
	{
		for (int i = 0; i < textmarks.size(); i++)
		{
			if (i == index)
			{
				textmarks.get(i).setBold(true);
			}
			else
			{
				textmarks.get(i).setBold(false);
			}
		}
		TextObservers.getInstance().notifyChange(fileRef);
	}
	
	public void changeBold (TextMark mark)
	{
		if (textmarks.contains(mark))
		{
		for (TextMark m : textmarks)
		{
			if (m == mark)
			{
				m.setBold(true);
			}
			else
			{
				m.setBold(false);
			}
		}
		TextObservers.getInstance().notifyChange(fileRef);
		}
	}
	
	public void changePosition (int index, int start, int end)
	{
		textmarks.get(index).setStart(start);
		textmarks.get(index).setEnd(end);
		TextObservers.getInstance().notifyChange(fileRef);
	}
	
	public void changePosition (TextMark mark, int start, int end)
	{
		if (textmarks.contains(mark))
		{
			mark.setStart(start);
			mark.setEnd(end);
			TextObservers.getInstance().notifyChange(fileRef);
		}
	}
	
	public void clear ()
	{
		textmarks.clear();
		TextObservers.getInstance().notifyChange(fileRef);
	}
	
}
