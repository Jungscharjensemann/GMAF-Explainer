package de.swa.fuh.qrefinement.ui.listeners;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;

import de.swa.fuh.qrefinement.command.AddRectangleCommand;
import de.swa.fuh.qrefinement.command.RelevanceMarkCommandHistory;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.qrm.Tools;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.panels.ImageRefinementLabel;

/**
 * 
 * 
 * Class implementing mouse listeners for mouse events in image relevance mark ui
 * 
 * @author Nicolas Boch
 *
 */
public class ImageMouseListener implements MouseListener, MouseMotionListener{

	/**
	 * Params of rectangle to be constructed, e.g. position and size.
	 */
	private double x, y, width, height, widthframe;
	
	/**
	 * color of the rectangle to be constructed
	 */
	private Color color;
	
	/**
	 * number of relevance marks for an image asset at the moment of the occurance of the first mouse event in the sequence of mouse events for the drawing of a rectangle
	 */
	private int sizebefore;
	
	/**
	 * container of rectangle objects containing the relevance marks for an image asset
	 */
	Vector <Rectangle> v;
	boolean pressed = false;
	@Override
	public void mouseDragged(MouseEvent e) {
		pressed = true;
		widthframe = ImageRefinementLabel.getInstance().getLabel().getWidth();
		
		width = e.getX() - x - ((widthframe - 1100)/ 2);
		height = e.getY() - y;

		File f = RefinementUIModel.getInstance().getSelectedAsset();
		
		if (v.size() > sizebefore)
		{
			v.removeElementAt(v.size()- 1);
		}
		
		v.add(new Rectangle((int)x,(int)y,(int)width,(int)height, color));
		
		try
		{
		//Image i = de.swa.ui.Tools.getScaledInstance(f.getName(), ImageIO.read(f), 1100, false);
			Image i = de.swa.ui.Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 1100, false);
		
		i = Tools.drawRectsOnImage(i, v);
		ImageRefinementLabel.getInstance().setImage(i);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		widthframe = ImageRefinementLabel.getInstance().getLabel().getWidth();
		
		x = e.getX() - ((widthframe - 1100)/ 2);
		y = e.getY();
		
		v = (Vector<Rectangle>)RefinementCollection.getInstance().getRelevanceMarks().get(RefinementUIModel.getInstance().getSelectedAsset()).getRectangles().clone();
		
		sizebefore = v.size();
		
		
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			color = Color.green;
		}
		else
		{
			color = Color.red;
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		boolean added = false;
		
		if (pressed)
		{
		
		if (x >= 0 && (x + width) >= 0.0 && (x+width) <= 1100.0 && (y+height) >= 0.0 && (y+height) <= 1100.0)
		{
			RelevanceMarkCommandHistory.getInstance().add(new AddRectangleCommand(RefinementUIModel.getInstance().getSelectedAsset(),new Rectangle((int)x,(int)y,(int)width,(int)height, color)));
			added = true;
		}
		
		if (!added)
		{
			ImageRefinementLabel.getInstance().refresh();
		}
		
		pressed = !pressed;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
