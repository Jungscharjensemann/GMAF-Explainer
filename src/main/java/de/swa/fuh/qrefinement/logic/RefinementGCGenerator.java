package de.swa.fuh.qrefinement.logic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.swa.fuh.qrefinement.logic.exp.DuplicateRelevanceMarkException;
import de.swa.fuh.qrefinement.logic.exp.NoRelevanceMarkException;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementPanel;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Tools;

/**
 * 
 * 
 * Single thread implementation of the construction of GCs from user relevance marks
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementGCGenerator {
	
	private RefinementGCGenerator () {};
	
	public static Vector<GraphCode> getRelevantGCs () throws DuplicateRelevanceMarkException, NoRelevanceMarkException
	{
		Vector<RefinementPanel> uiassets = RefinementAssetListPanel.getCurrentInstance().getButtons();
		Vector <GraphCode> gc = new Vector <GraphCode> ();
		File f;
		File newimgf, newtextf;
		RandomAccessFile rf = null;
		MMFG m;
		BufferedImage original = null;
		BufferedImage newimg;
		String newcontent;
		String content = "";
		GMAF gmaf = new GMAF();
		
		if (!existsSelection(uiassets))
		{
			throw new NoRelevanceMarkException("No relevance mark input found");
		}
		
		if ((f = existsDuplicate (uiassets)) != null)
		{
			throw new DuplicateRelevanceMarkException ("Cannot set relevance marks for parts of asset and the whole asset at the same time", f);
		}
		
		for (RefinementPanel uiasset : uiassets)
		{
			if (uiasset.getButtonGroup().getSelection() != null)
			{
			if (uiasset.getButtonGroup().getSelection().getActionCommand().equals("rel"))
			{
				f = uiasset.getFile();
				m = MMFGCollection.getInstance().getMMFGForFile(f);
				gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
			}
			}
		}
		
		//Generate GraphCodes of relevant parts of Images
		for (File fi : RefinementCollection.getInstance().getRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles().size() > 0)
			{
			try
			{
				original = ImageIO.read(fi);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles())
			{
				if (r.getColor() == Color.GREEN)
				{
					original = (BufferedImage) Tools.getScaledInstance(fi.getName(), original, 1100, false);
					
					//Subimage specified by rectangle of relevant content
					newimg = original.getSubimage(r.getX(), r.getY(), r.getWidth(), r.getHeight());
					int width = newimg.getWidth();
					int height = newimg.getHeight();
					BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
					
					int px [] = new int[width * height];
					
					newimg.getRGB(0, 0, width, height, px, 0, width);
					out.setRGB(0, 0, width, height, px, 0, width);
					
					try
					{
						newimgf = File.createTempFile("newimg", ".jpg");
						boolean b = ImageIO.write(out, "jpg", newimgf);
						if (!b) return null;
						
						m = gmaf.processAsset(newimgf);
						gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
						
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		}
		
	// Generate GraphCodes of relevant parts of text	
	for (File fi: RefinementCollection.getInstance().getTextRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks().size() > 0)
			{
			try
			{
			rf = new RandomAccessFile(fi,"r");
			String line = "";
			while ((line = rf.readLine()) != null) {
				content += line + "\n";
			}
			rf.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			for (TextMark tm : RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks())
			{
				if (tm.getColor() == Color.GREEN)
				{
					newcontent = content.substring(tm.getStart(), tm.getEnd());
					
					try
					{
						newtextf = File.createTempFile("newtext", "." + FilenameUtils.getExtension(fi.getName()));
						
						FileUtils.writeStringToFile(newtextf, newcontent, (String)null);
						
						m = gmaf.processAsset(newtextf);
						gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			}

		}
		
		return gc;
	}
	
	public static Vector<GraphCode> getNonRelevantGCs () throws DuplicateRelevanceMarkException, NoRelevanceMarkException
	{
		Vector<RefinementPanel> uiassets = RefinementAssetListPanel.getCurrentInstance().getButtons();
		Vector <GraphCode> gc = new Vector <GraphCode> ();
		File f;
		File newimgf, newtextf;
		RandomAccessFile rf = null;
		MMFG m;
		BufferedImage original = null;
		BufferedImage newimg;
		String newcontent;
		String content = "";
		GMAF gmaf = new GMAF();
		
		if (!existsSelection(uiassets))
		{
			throw new NoRelevanceMarkException("No relevance mark input found");
		}
		
		if ((f = existsDuplicate (uiassets)) != null)
		{
			throw new DuplicateRelevanceMarkException ("Cannot set relevance marks for parts of asset and the whole asset at the same time", f);
		}
		
		for (RefinementPanel uiasset : uiassets)
		{
			if (uiasset.getButtonGroup().getSelection() != null)
			{
			if (uiasset.getButtonGroup().getSelection().getActionCommand().equals("nrel"))
			{
				f = uiasset.getFile();
				m = MMFGCollection.getInstance().getMMFGForFile(f);
				gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
			}
			}
		}
		
		//Generate GraphCodes of non-relevant parts of Images
		for (File fi : RefinementCollection.getInstance().getRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles().size() > 0)
			{
			try
			{
				original = ImageIO.read(fi);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles())
			{
				if (r.getColor() == Color.RED)
				{
					original = (BufferedImage) Tools.getScaledInstance(fi.getName(), original, 1100, false);
					
					//Subimage specified by rectangle of relevant content
					newimg = original.getSubimage(r.getX(), r.getY(), r.getWidth(), r.getHeight());
					
					int width = newimg.getWidth();
					int height = newimg.getHeight();
					BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
					
					int px [] = new int[width * height];
					
					newimg.getRGB(0, 0, width, height, px, 0, width);
					out.setRGB(0, 0, width, height, px, 0, width);
					
					try
					{
						newimgf = File.createTempFile("newimg", ".jpg");
						boolean b = ImageIO.write(out, "jpg", newimgf);
						if (!b) return null;
						
						m = gmaf.processAsset(newimgf);
						gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
						
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		}
		
	// Generate GraphCodes of non-relevant parts of text	
	for (File fi: RefinementCollection.getInstance().getTextRelevanceMarks().keySet())
		{
		if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks().size() > 0)
		{
			try
			{
			rf = new RandomAccessFile(fi,"r");
			String line = "";
			while ((line = rf.readLine()) != null) {
				content += line + "\n";
			}
			rf.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			for (TextMark tm : RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks())
			{
				if (tm.getColor() == Color.RED)
				{
					newcontent = content.substring(tm.getStart(), tm.getEnd());
					
					try
					{
						newtextf = File.createTempFile("newtext", "." + FilenameUtils.getExtension(fi.getName()));
						
						FileUtils.writeStringToFile(newtextf, newcontent, (String)null);
						
						m = gmaf.processAsset(newtextf);
						gc.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		}
		return gc;
	}
	
	public static GraphCode unionofGC (Vector<GraphCode> gc)
	{
		return GraphCodeCollection.getUnion(gc);
	}
	
	public static GraphCode subtractGCs (GraphCode gc1, GraphCode gc2)
	{
		return GraphCodeCollection.subtract(gc1, gc2);
	}
	
	public static File existsDuplicate (Vector<RefinementPanel> uiassets)
	{
		for (RefinementPanel uiasset : uiassets)
		{
			if (uiasset.getButtonGroup().getSelection() != null)
			{
				if (de.swa.fuh.qrefinement.qrm.Tools.isImageFile(uiasset.getFile()))
					{
						if (! RefinementCollection.getInstance().getRelevanceMarks().get(uiasset.getFile()).getRectangles().isEmpty())
							{
								return uiasset.getFile();
							}
					}
			else
				{
					if (! RefinementCollection.getInstance().getTextRelevanceMarks().get(uiasset.getFile()).getTextMarks().isEmpty())	
						{
							return uiasset.getFile();
						}
				}
			}
		}
		return null;
	}
	
	public static boolean existsSelection (Vector<RefinementPanel> uiassets)
	{
		
		for (RefinementPanel uiasset : uiassets)
		{
			if (uiasset.getButtonGroup().getSelection() != null)
			{
				return true;
			}
		}
		
		for (RefinementPanel uiasset: uiassets)
		{
		if (de.swa.fuh.qrefinement.qrm.Tools.isImageFile(uiasset.getFile()))
		{
			if (! RefinementCollection.getInstance().getRelevanceMarks().get(uiasset.getFile()).getRectangles().isEmpty())
				{
					return true;
				}
		}
else
	{
		if (! RefinementCollection.getInstance().getTextRelevanceMarks().get(uiasset.getFile()).getTextMarks().isEmpty())	
			{
				return true;
			}
	}
		}
		
		return false;
	}	
		

}
