package de.swa.fuh.qrefinement.ui.panels;

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.swa.fuh.qrefinement.observer.ImageObservers;
import de.swa.fuh.qrefinement.observer.UpdateImageMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.listeners.ImageMouseListener;
import de.swa.ui.Tools;

/**
 * 
 * 
 * This class implements a label to show an image in the ui for providing relevance input. 
 * 
 * @author Nicolas Boch
 *
 */
public class ImageRefinementLabel implements UpdateImageMarks{

	/**
	 * class variable for single instance of this class. Implements singleton pattern.
	 */
	private static ImageRefinementLabel instance;
	
	/**
	 * Image object instance of the image to be shown in ui
	 */
	private Image i;
	private JLabel l;
	
	/**
	 * Reference to image file of the image that is displayed
	 */
	private File f;
	
	private ImageRefinementLabel () {}
	
	public static ImageRefinementLabel getInstance ()
	{
		if (instance == null)
		{
			instance = new ImageRefinementLabel ();
			instance.init ();
		}
		return instance;
	}
	
	private void init ()
	{
		ImageMouseListener mouseListener = new ImageMouseListener ();
		ImageObservers.getInstance().subscribe(this);
		
		f = RefinementUIModel.getInstance().getSelectedAsset();
		i = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 1100, false);
		
		l = new JLabel (new ImageIcon(i));
		l.addMouseListener(mouseListener);
		l.addMouseMotionListener(mouseListener);
		l.setBackground(Color.WHITE);
		
	}
	
	public JLabel getLabel ()
	{
		return l;
	}
	
	public void setImage (Image i)
	{
		this.i = i;
		l.setIcon(new ImageIcon(i));
	}
	
	public void refresh ()
	{
		if (de.swa.fuh.qrefinement.qrm.Tools.isImageFile(RefinementUIModel.getInstance().getSelectedAsset()))
		{
			f = RefinementUIModel.getInstance().getSelectedAsset();
			
			Image temp = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 1100, false);
			
			temp = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(temp, RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles());
			
			setImage(temp);
		}
	}

	/**
	 * Observer pattern. Reaction to notification about new relevance mark added to an image
	 */
	@Override
	public void addMark(File fileRef, Rectangle mark) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			Image temp = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 1100, false);
			
			temp = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(temp, RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles());
			
			setImage (temp);
		}
		
		
	}

	/**
	 * Observer pattern. Reaction to notification about state change in relevance marks for an image, e.g. relevance mark removed.
	 */
	@Override
	public void updateImageMark(File fileRef) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			Image temp = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 1100, false);
			
			temp = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(temp, RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles());
			
			setImage (temp);
		}
		
	}
	
}
