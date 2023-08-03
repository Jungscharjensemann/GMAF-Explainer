package de.swa.fuh.qrefinement.ui.datamodel;

import java.awt.Color;

/**
 * 
 * 
 * Implementation of the model of one rectangular relevance mark for an image asset
 * 
 * @author Nicolas Boch
 *
 */
public class Rectangle {

	/**
	 * Params of the rectangle, e.g. size and position
	 */
	private int x,y,width,height, thickness;
	
	
	/**
	 * Color of the rectangle
	 */
	private Color color;

	public Rectangle(int x, int y, int width, int height, Color color)
	{
		if (width < 0)
		{
			this.x = x + width;
			this.width = Math.abs(width);
		}
		else
		{
			this.x = x;
			this.width = width;
		}
		if (height < 0)
		{
			this.y = y + height;
			this.height = Math.abs(height);
		}
		else
		{
			this.y = y;
			this.height = height;
		}
		this.color = color;
		this.thickness = 1;
	}
	
	public Rectangle(int x, int y, int width, int height, Color color, int thickness)
	{
		if (width < 0)
		{
			this.x = x + width;
			this.width = Math.abs(width);
		}
		else
		{
			this.x = x;
			this.width = width;
		}
		if (height < 0)
		{
			this.y = y + height;
			this.height = Math.abs(height);
		}
		else
		{
			this.y = y;
			this.height = height;
		}
		
		this.color = color;
		
		if (thickness > 0 && thickness < 4)
		{
			this.thickness = thickness;
		}
		else
		{
			this.thickness = 1;
		}
	}
	
	public int getThickness()
	{
		return thickness;
	}
	
	public int getX ()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth ()
	{
		return width;
	}
	
	public int getHeight ()
	{
		return height;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setThickness(int thick)
	{
		if (thick > 0 && thick < 4)
		{
			thickness = thick;
		}
	}
	
}
