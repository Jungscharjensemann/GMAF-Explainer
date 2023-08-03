package de.swa.fuh.qrefinement.ui.datamodel;

import java.io.File;
import java.util.Vector;

import de.swa.fuh.qrefinement.observer.ImageObservers;

/**
 * class containing a number of references to rectangles that represent a relevance mark for an image file each. Exactly one instance of this class should exist for each image file in the set of assets.
 * @author Nicolas Boch
 *
 */
public class AssetRectangles {

	
	/**
	 * Vector of rectangles representing all relevance marks for one image asset file
	 */
	private Vector<Rectangle> rectangles;
	/**
	 * the image asset file for which an instance of the class represents relevance marks
	 */
	private File fileRef;
	
	public AssetRectangles (Vector<Rectangle> r)
	{
		this.rectangles = r;
	}
	
	/**
	 * constructs a new instance of the class setting the file reference to the image file the new instance is representing relevance marks for
	 * @param f a File object of an image file
	 */
	public AssetRectangles (File f)
	{
		rectangles = new Vector<Rectangle>();
		this.fileRef = f;
	}
	
	/**
	 * returns the Vector of relevance marks
	 */
	public Vector<Rectangle> getRectangles ()
	{
		return rectangles;
	}
	
	/**
	 * adds a relevance marks to the vector of relevance marks and informs the object that manages observers about this change
	 * @param rect the rectangle to be added to the vector
	 */
	public void addRectangle (Rectangle rect)
	{
		rectangles.add(rect);
		ImageObservers.getInstance().notifyAdd(fileRef, rect);
	}
	
	/**
	 * deletes a relevance marks from the vector of relevance marks and informs the object that manages observers about this change
	 * @param rect the rectangle to be removed from the vector
	 */
	public void deleteRectangle (Rectangle rect)
	{
		if (rectangles.contains(rect))
		{
			rectangles.remove(rect);
			ImageObservers.getInstance().notifyChange(fileRef);
		}
	}
	
	/**
	 * deletes a relevance marks from the vector of relevance marks and informs the object that manages observers about this change
	 * @param index the index of the rectangle to be removed in the vector of rectangles
	 */
	public void deleteRectangle (int index)
	{
		rectangles.remove(index);
		ImageObservers.getInstance().notifyChange(fileRef);
	}
	
	
	/**
	 *  deletes all relevance marks from the vector of relevance marks and informs the object that manages observers about this change
	 */
	public void clear ()
	{
		rectangles.clear();
		ImageObservers.getInstance().notifyChange(fileRef);
	}
	
	
	/**
	 * emphasizes one rectangle in the Vector of rectangles by increasing the size of its border
	 * @param rect the rectangle to be emphasized
	 */
	public void setThick (Rectangle rect)
	{
		if (rectangles.contains(rect))
		{
			for (Rectangle r : rectangles)
			{
				if (r == rect)
				{
					r.setThickness(3);
				}
				else
				{
					r.setThickness(1);
				}
			}
			
			ImageObservers.getInstance().notifyChange(fileRef);
		}
	}
	
	/**
	 * emphasizes one rectangle in the Vector of rectangles by increasing the size of its border
	 * @param index the index of the rectangle to be emphasized in the Vector of all rectangles
	 */
	public void setThick (int index)
	{
		for (int i = 0; i < rectangles.size(); i++)
		{
			if (i == index)
			{
				rectangles.get(i).setThickness(3);
			}
			else
			{
				rectangles.get(i).setThickness(1);
			}
		}
		ImageObservers.getInstance().notifyChange(fileRef);
	}
}
