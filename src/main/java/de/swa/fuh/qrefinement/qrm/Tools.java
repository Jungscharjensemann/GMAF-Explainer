package de.swa.fuh.qrefinement.qrm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.swing.text.DefaultHighlighter;

import org.apache.commons.io.FilenameUtils;

import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;


/**
 * Class containing implementation of algorithms that are used by objects of different classes.
 * @author Nicolas Boch
 *
 */
public class Tools {
	/**
	 * HighlightPainter for the painting of red text highlights
	 */
	private static DefaultHighlighter.DefaultHighlightPainter redhighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
	/**
	 * HighlightPainter for the painting of green text highlights
	 */
	private static DefaultHighlighter.DefaultHighlightPainter greenhighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);

	/**
	 * returns a HighlightPainter for the painting of text highlights in the desired color
	 * 
	 * @param c color in which text highlights shall be painted. Supports red and green color.
	 */
	public static DefaultHighlighter.DefaultHighlightPainter getHighlighter (Color c)
	{
		if (c == Color.RED)
		{
			return redhighlightPainter;
		}
		if (c == Color.GREEN)
		{
			return greenhighlightPainter;
		}
		return null;
	}
	
	/**
	 * draws colored rectangles in an Image
	 * @param srcImg the image in which rectangles are to be painted
	 * @param rectangles a vector with references to the rectangles that are to be painted
	 * @return Image containing the painted rectangles
	 */
	public static Image drawRectsOnImage(Image srcImg, Vector<Rectangle> rectangles)
	{
		BufferedImage temp = toBufferedImage(srcImg);
		
		Graphics2D g2 = temp.createGraphics();
		
		for (Rectangle r : rectangles)
		{
			drawRect(g2, r.getX(), r.getY(), r.getWidth(), r.getHeight(), r.getColor(), r.getThickness());
		}
		
		g2.dispose();
		
		return temp;
	}
	
	/**
	 * converting an Image to an BufferedImage by redrawing the Image to an new object.
	 * @param img Image object to be converted
	 * 
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	/** 
	 * draw a rectangle in a Graphics2D object
	 * @param g the Graphics2D object in that the rectangle is to be drawn
	 * @param x the x position of the starting edge of the rectangle
	 * @param y the y position of the starting edge of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the color of the rectangle
	 * @param thickness the thickness of the rectangles border
	 */
	public static void drawRect(Graphics2D g, int x, int y, int width, int height, Color color, int thickness)
	{
		if (width < 0)
		{
			x = x + width;
			width = Math.abs(width);
		}
		if (height < 0)
		{
			y = y + height;
			height = Math.abs(height);
		}
		
		g.setColor(color);
		g.setStroke(new BasicStroke(thickness));
		g.drawRect(x,y,width,height);
	}
	
	/**
	 * returns whether a File is considered to be an image file or not
	 * @param f the file for which it is to be desided whether it is an image file
	 *
	 */
	public static boolean isImageFile (File f)
	{
		if (FilenameUtils.getExtension(f.getName()).equals("jpg"))
		{
			return true;
		}
		if (FilenameUtils.getExtension(f.getName()).equals("jpeg"))
		{
			return true;
		}
		if (FilenameUtils.getExtension(f.getName()).equals("png"))
		{
			return true;
		}
		if (FilenameUtils.getExtension(f.getName()).equals("tiff"))
		{
			return true;
		}
		if (FilenameUtils.getExtension(f.getName()).equals("gif"))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * returns the file extension / file type of a file using Apache Commons IO API
	 * @param f the file for which the extension is to be determined
	 * @return a String containing the file extension
	 */
	public static String getFileExtension(File f)
	{
		return FilenameUtils.getExtension(f.getName());
	}
	
}
