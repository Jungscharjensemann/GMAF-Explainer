package de.swa.fuh.qrefinement.ui.datamodel;

import java.awt.Color;

/**
 * 
 * 
 * Class implementing the model of a relevance mark for a text asset. 
 * 
 * @author Nicolas Boch
 *
 */
public class TextMark {

	/**
	 * start position of relevance mark
	 */
	private int start;
	
	/**
	 * end position of relevance mark
	 */
	private int end;
	
	/**
	 * color of relevance mark
	 */
	private Color color;
	
	/**
	 * states whether relevance mark is to be printed bold
	 */
	private boolean bold;
	
	public TextMark (int start, int end, Color color)
	{
		if (start < 0)
		{
			throw new IllegalArgumentException ("Start out of range");
		}
		if (end <= start)
		{
			throw new IllegalArgumentException ("End value lower than start value");
		}
		this.start = start;
		this.end = end;
		this.color = color;
		this.bold = false;
	}
	
	public TextMark (int start, int end, Color color, boolean bold)
	{
		if (start < 0)
		{
			throw new IllegalArgumentException ("Start out of range");
		}
		if (end <= start)
		{
			throw new IllegalArgumentException ("End value lower than start value");
		}
		this.start = start;
		this.end = end;
		this.color = color;
		this.bold = bold;
	}
	
	public int getStart ()
	{
		return start;
	}
	
	public int getEnd ()
	{
		return end;
	}
	
	public void setStart (int startint)
	{
		if (startint > end)
		{
			return;
		}
		
		start = startint;
	}
	
	public void setEnd (int endint)
	{
		if (endint < start)
		{
			return;
		}
		
		end = endint;
	}
	
	
	public Color getColor ()
	{
		return color;
	}
	
	public boolean getBold ()
	{
		return bold;
	}
	
	public void setBold (boolean b)
	{
		bold = b;
	}
}
