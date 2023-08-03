package de.swa.fuh.qrefinement.command;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import de.swa.fuh.qrefinement.eval.EvalModel;
import de.swa.fuh.qrefinement.logic.Querying;
import de.swa.fuh.qrefinement.logic.RefinementGCGeneratorThread;
import de.swa.fuh.qrefinement.logic.exp.DuplicateRelevanceMarkException;
import de.swa.fuh.qrefinement.logic.exp.NoRelevanceMarkException;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;
import de.swa.fuh.qrefinement.ui.datamodel.AssetRectangles;
import de.swa.fuh.qrefinement.ui.datamodel.AssetTextMarks;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementPanel;
import de.swa.gc.GraphCode;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command generates and executes a new query based on relevance marks from the user
 * 
 * @author Nicolas Boch
 *
 */
public class RequeryCommand implements UndoableCommand {

	/**
	 * graph code of most recently executed query before generating the new query
	 */
	private GraphCode oldQuery;
	
	/**
	 * graph code of the query that was generated
	 */
	private GraphCode newQuery;
	
	/**
	 * Metric to be used to calculate similarity between asset and query
	 */
	private Querying metric;
	
	
	@Override
	public void execute() {
		oldQuery = RefinementCollection.getInstance().getCurrentQuery();
		
		try
		{
		newQuery = RefinementGCGeneratorThread.erg();
		
		
		System.out.println("Method returned 2");
		
		metric = RefinementUIModel.getInstance().getQueryMetric();
		//RefinementCollection.getInstance().query(newQuery);
		metric.query(newQuery);
		
		System.out.println("Queried");
		
		// clear list of assets with relevance marks
		Vector<String> markedassets = EvalModel.getInstance().getMarkedFiles();
		markedassets.clear();
		
		Enumeration<File> textmarks = RefinementCollection.getInstance().getTextRelevanceMarks().keys();
		
		//construct list of all text files with relevance marks for evaluation purpose and clear relevance marks in UI
		while (textmarks.hasMoreElements())
		{
			File f = (File) textmarks.nextElement();
			AssetTextMarks m = RefinementCollection.getInstance().getTextRelevanceMarks().get(f);
			if (!m.getTextMarks().isEmpty())
			{
				if (!markedassets.contains(FilenameUtils.removeExtension(f.getName())))
				{
					markedassets.add(FilenameUtils.removeExtension(f.getName()));
				}
			}
			m.clear();
		}
		
		Enumeration<File> rectangles = RefinementCollection.getInstance().getRelevanceMarks().keys();
		
		//construct list of all image files with relevance marks for evaluation purpose and clear relevance marks in UI
		while (rectangles.hasMoreElements())
		{
			File f = (File) rectangles.nextElement();
			AssetRectangles r = RefinementCollection.getInstance().getRelevanceMarks().get(f);
			if (!r.getRectangles().isEmpty())
			{
				if (!markedassets.contains(FilenameUtils.removeExtension(f.getName())))
				{
					markedassets.add(FilenameUtils.removeExtension(f.getName()));
				}
			}
			r.clear();
		}		
		
		//construct list of all image files with relevance marks for the whole asset for evaluation purpose and clear relevance marks in UI
		for (RefinementPanel p : RefinementAssetListPanel.getCurrentInstance().getButtons())
		{
			if (p.getButtonGroup().getSelection() != null)
			{
			if (p.getButtonGroup().getSelection().getActionCommand().equals("rel") || p.getButtonGroup().getSelection().getActionCommand().equals("nrel"))
			{
				if (!markedassets.contains(FilenameUtils.removeExtension(p.getFile().getName())))
				{
					markedassets.add(FilenameUtils.removeExtension(p.getFile().getName()));
				}
			}
			}
			p.getButtonGroup().clearSelection();
		}
		
		RelevanceMarkCommandHistory.getInstance().clearHistory();
		
		EvalModel.getInstance().notifyThread();
		
		}
		catch (NoRelevanceMarkException exc)
		{
			JOptionPane.showMessageDialog(Refinement_UI.getInstance(), exc.getMessage());
			throw new IllegalStateException ();
		}
		catch (DuplicateRelevanceMarkException ex)
		{
			String s;
			s = ex.getMessage() + "\n";
			s = s + "This was found in asset file " + ex.getDuplicateFile().getName() + ".";
			
			JOptionPane.showMessageDialog(Refinement_UI.getInstance(), s);
			
			throw new IllegalStateException();
		}
	}

	public Querying getMetric()
	{
		return metric;
	}
	
	@Override
	public void undo() {
		if (SelectQueryCommandHistory.getInstance().getLastQueryMetricUndo() != null)
		{
			SelectQueryCommandHistory.getInstance().getLastQueryMetricUndo().query(oldQuery);
		}
		else
		{
		RefinementCollection.getInstance().query(oldQuery);
		}

	}

	@Override
	public void redo() {
		metric.query(newQuery);

	}

}
