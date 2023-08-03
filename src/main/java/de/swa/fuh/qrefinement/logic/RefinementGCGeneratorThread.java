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
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementPanel;
import de.swa.gc.GraphCode;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Tools;

/**
 * 
 * 
 * Multi thread implementation of the construction of GCs from user relevance marks
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementGCGeneratorThread {

	private RefinementGCGeneratorThread () {};
	
	/**
	 * Construct a new query based on user's relevance marks using configured RF algorithm
	 * 
	 * @return GC of the new query
	 * @throws DuplicateRelevanceMarkException Illegal state of duplicate relevance marks for an asset
	 * @throws NoRelevanceMarkException No relevance marks are provided at all
	 */
	public static GraphCode erg () throws DuplicateRelevanceMarkException, NoRelevanceMarkException
	{
		Vector<RefinementPanel> uiassets = RefinementAssetListPanel.getCurrentInstance().getButtons();
		Vector <GraphCode> gcrel = new Vector <GraphCode> ();
		Vector <GraphCode> gcnrel = new Vector <GraphCode> ();
		int numtrelimg = 0;
		int numtnrelimg = 0;
		int curtrelimg = 0;
		int curtnrelimg = 0;
		int numtreltxt = 0;
		int numtnreltxt = 0;
		int curtreltxt = 0;
		int curtnreltxt = 0;
		Thread [] trelimg;
		Thread [] tnrelimg;
		Thread [] treltxt;
		Thread [] tnreltxt;
		File f;
		//File newtextf;
		//RandomAccessFile rf = null;
		//String newcontent;
		//String content = "";
		//GMAF gmaf = new GMAF();
		
		if (!RefinementGCGenerator.existsSelection(uiassets))
		{
			throw new NoRelevanceMarkException("No relevance mark input found");
		}
		
		if ((f = RefinementGCGenerator.existsDuplicate (uiassets)) != null)
		{
			throw new DuplicateRelevanceMarkException ("Cannot set relevance marks for parts of asset and the whole asset at the same time", f);
		}
		
		for (RefinementPanel uiasset : uiassets)
		{
			if (uiasset.getButtonGroup().getSelection() != null)
			{
				f = uiasset.getFile();
				MMFG m = MMFGCollection.getInstance().getMMFGForFile(f);
			if (uiasset.getButtonGroup().getSelection().getActionCommand().equals("rel"))
			{
				gcrel.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
			}
			if (uiasset.getButtonGroup().getSelection().getActionCommand().equals("nrel"))
			{
				gcnrel.add(MMFGCollection.getInstance().getOrGenerateGraphCode(m));
			}
			}
		}
		
		//count num of relevance marks for imgs
		for (File fi : RefinementCollection.getInstance().getRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles().size() > 0)
			{
			
			
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles())
			{
				if (r.getColor() == Color.GREEN)
				{
					numtrelimg++;
				}
				if (r.getColor() == Color.RED)
				{
					numtnrelimg++;
				}
				
			}
	}
		}
		
		//count num of relevance marks for text
		for (File fi: RefinementCollection.getInstance().getTextRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks().size() > 0)
			{
			
			for (TextMark tm : RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks())
			{
				if (tm.getColor() == Color.GREEN)
				{
					numtreltxt++;
				}
				if (tm.getColor() == Color.RED)
				{
					numtnreltxt++;
				}
			}
			}
		}
		
		
		// Generate arrays of threads. One thread for each relevance mark.
		trelimg = new Thread[numtrelimg];
		tnrelimg = new Thread[numtnrelimg];
		
		for (File fi : RefinementCollection.getInstance().getRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles().size() > 0)
			{
			
			
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(fi).getRectangles())
			{	
				if (r.getColor() == Color.GREEN)
				{
					trelimg[curtrelimg] = new Thread(new Runnable () {
						public void run ()
						{
							try
							{
								BufferedImage original = (BufferedImage) Tools.getScaledInstance(fi.getName(), ImageIO.read(fi), 1100, false);
								
								// generate sub image from rectangular relevance mark and save it in temp image file
								BufferedImage newimg = original.getSubimage(r.getX(), r.getY(), r.getWidth(), r.getHeight());
								int width = newimg.getWidth();
								int height = newimg.getHeight();
										
								BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
								
								int px [] = new int[width * height];
								
								newimg.getRGB(0, 0, width, height, px, 0, width);
								out.setRGB(0, 0, width, height, px, 0, width);
								
								File newimgf = File.createTempFile("newimg", ".jpg");
								boolean b = ImageIO.write(out, "jpg", newimgf);
								if (!b) return;
								
								// process sub image through GMAF
								MMFG m = new GMAF().processAsset(newimgf);
								
								//add generated GC to container object
								addtogcVector(gcrel, MMFGCollection.getInstance().getOrGenerateGraphCode(m));
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
					});
					
					trelimg[curtrelimg].start();
					curtrelimg ++;
				}
				if (r.getColor() == Color.RED)
				{
					tnrelimg[curtnrelimg] = new Thread(new Runnable () {
						public void run ()
						{
							try
							{
								BufferedImage original = (BufferedImage) Tools.getScaledInstance(fi.getName(), ImageIO.read(fi), 1100, false);
								
								BufferedImage newimg = original.getSubimage(r.getX(), r.getY(), r.getWidth(), r.getHeight());
								int width = newimg.getWidth();
								int height = newimg.getHeight();
										
								BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
								
								int px [] = new int[width * height];
								
								newimg.getRGB(0, 0, width, height, px, 0, width);
								out.setRGB(0, 0, width, height, px, 0, width);
								
								File newimgf = File.createTempFile("newimg", ".jpg");
								boolean b = ImageIO.write(out, "jpg", newimgf);
								if (!b) return;
								
								MMFG m = new GMAF().processAsset(newimgf);
								addtogcVector(gcnrel, MMFGCollection.getInstance().getOrGenerateGraphCode(m));
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
					});
					
					tnrelimg[curtnrelimg].start();
					curtnrelimg ++;
				}
		
}
			}
		}
		
		treltxt = new Thread[numtreltxt];
		tnreltxt = new Thread[numtnreltxt];

		for (File fi: RefinementCollection.getInstance().getTextRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks().size() > 0)
			{
			
			for (TextMark tm : RefinementCollection.getInstance().getTextRelevanceMarks().get(fi).getTextMarks())
			{
				if (tm.getColor() == Color.GREEN)
				{
					treltxt[curtreltxt] = new Thread (new Runnable () {
						public void run ()
						{
							try
							{
								
							// Generate sub text from text relevance mark and save it in temp file	
							RandomAccessFile rf = new RandomAccessFile(fi,"r");
							String content = "";
							String line = "";
							while ((line = rf.readLine()) != null) {
								content += line + "\n";
							}
							rf.close();
							
							String newcontent = content.substring(tm.getStart(), tm.getEnd());
							
							File newtextf = File.createTempFile("newtext", "." + FilenameUtils.getExtension(fi.getName()));
							
							FileUtils.writeStringToFile(newtextf, newcontent, (String)null);
							
							// Process temp asset through GMAF
							MMFG m = new GMAF().processAsset(newtextf);
							
							addtogcVector(gcrel, MMFGCollection.getInstance().getOrGenerateGraphCode(m));
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
					});
					treltxt[curtreltxt].start();
					curtreltxt++;
					
				}
				if (tm.getColor() == Color.RED)
				{
					tnreltxt[curtnreltxt] = new Thread (new Runnable () {
						public void run ()
						{
							try
							{
							RandomAccessFile rf = new RandomAccessFile(fi,"r");
							String content = "";
							String line = "";
							while ((line = rf.readLine()) != null) {
								content += line + "\n";
							}
							rf.close();
							
							String newcontent = content.substring(tm.getStart(), tm.getEnd());
							
							File newtextf = File.createTempFile("newtext", "." + FilenameUtils.getExtension(fi.getName()));
							
							FileUtils.writeStringToFile(newtextf, newcontent, (String)null);
							MMFG m = new GMAF().processAsset(newtextf);
							
							addtogcVector(gcnrel, MMFGCollection.getInstance().getOrGenerateGraphCode(m));
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
					});
					tnreltxt[curtnreltxt].start();
					curtnreltxt++;
				}
			}
			}
		}
		
//Waiting for Threads to process Asset
for (int i = 0; i < numtrelimg; i++)
{
	try
	{
	trelimg[i].join();
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}
for (int i = 0; i < numtnrelimg; i++)
{
	try
	{
	tnrelimg[i].join();
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}
for (int i = 0; i < numtreltxt; i++)
{
	try
	{
	treltxt[i].join();
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}

for (int i = 0; i < numtnreltxt; i++)
{
	try
	{
	tnreltxt[i].join();
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}

	// calculate union of GCs of relevant and non relevant assets or parts of assets
	GraphCode urel = RefinementGCGenerator.unionofGC(gcrel);
	GraphCode unrel = RefinementGCGenerator.unionofGC(gcnrel);
	
	System.out.println("Method returned");
	System.out.println("Algorithm to process: " + RefinementUIModel.getInstance().getAlgorithm().getClass().getName());
	//return RefinementGCGenerator.subtractGCs(urel, unrel);
	
	// Generate new query with configured algorithm for weighting relevant and non relevant parts
	return RefinementUIModel.getInstance().getAlgorithm().generateQuery(urel, unrel, gcrel, gcnrel);
	}

	private static synchronized void addtogcVector (Vector<GraphCode> gc, GraphCode g)
	{
		gc.add(g);
	}

}
