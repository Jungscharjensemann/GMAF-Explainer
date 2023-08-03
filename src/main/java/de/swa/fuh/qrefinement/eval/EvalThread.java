package de.swa.fuh.qrefinement.eval;

import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;
import de.swa.gc.GraphCode;

/**
 * 
 * 
 * Implementation of interacive eval mode. Executes queries in experiment description and calculates eval results. Code must be run parallel in a new thread.
 * 
 * @author Nicolas Boch
 *
 */
public class EvalThread extends Thread {

	/**
	 * Queries to be executed in experiment
	 */
	private Vector<String> queries;
	
	/**
	 * number of iterations that the test user should perform for each query in experiment description
	 */
	private int numiter;
	
	/**
	 * qrel-File with ground truth for current experiment
	 */
	private final File qrel = new File ("eval/qrel.txt");
	private int i = 0;
	private File outOfSyncRes = null;
	
	public EvalThread (Vector<String> queries, int numiter)
	{
		this.queries = queries;
		this.numiter = numiter;
	}
	
	public void run ()
	{
		
		synchronized (this)
		{
		int j = 1;	
		File resultsdir = TrecEval.createResultsDir();
		
		File[] resultfiles =  new File[numiter + 1];
		
		//Create empty files for query search results (ranking of assets)
		for (int i = 0; i < (numiter + 1); i++)
		{
			resultfiles[i] = TrecEval.createResultsFile(resultsdir, i);
		}
		
		for (String s : queries)
		{
		if (!isInterrupted() && EvalModel.getInstance().shouldrun())
		{
			String[] stringarr = s.split(" ");
			
			//construct graph code for query key words from experiment definition
			Vector<String> vocTerms = new Vector<String>();
			for (String t : stringarr) {
				if (!vocTerms.contains(t)) {
					if (!t.equals(""))
						vocTerms.add(s.trim());
				}
			}
			
			GraphCode gcQuery = new GraphCode();
			gcQuery.setDictionary(vocTerms);
			
			// Execute the query on the collection of MMFGs
			//SwingUtilities.invokeLater(new Runnable () 
					//{
				//public void run ()
				//{
					RefinementCollection.getInstance().query(gcQuery);
				//}
					//});
			//RefinementCollection.getInstance().query(gcQuery);
			
			//Save query results in temp file
			TrecEval.appendErgToFile(resultfiles[0], j);
			
			for (i = 0; i < numiter; i++)
			{
				
				if (!isInterrupted() && EvalModel.getInstance().shouldrun())
				{
					try
					{
						SwingUtilities.invokeLater(new Runnable () 
						{
					public void run ()
					{
						//Inform user about current state of execution
						JOptionPane.showMessageDialog(Refinement_UI.getInstance(), "Iteration: " + (i+1) + " for Query " + s);	
					}
						});
					//JOptionPane.showMessageDialog(Refinement_UI.getInstance(), "Iteration: " + (i+1) + " for Query " + s);	
					
					// Wait until thread is notified about user finishing input of relevance marks
					wait();
					
					//Save query results in temp file and construct residual query results
					TrecEval.appendErgToFile(resultfiles[i+1], j);
					TrecEval.createresidualerg(resultfiles[0],resultfiles[i+1],j,(i+1));
					
					if (isInterrupted() || !EvalModel.getInstance().shouldrun())
					{
						return;
					}
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
				else
				{
					return;
				}
			}
			j++;
		}
		else
		{
			return;
		}
	}
		
		// Run trec eval, construct trec results file with eval results
		for (i = 0; i < (numiter +1); i++)
		{
			File resultsfile = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_results.txt");
			File trec_results = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_trec_results.txt");
			
			File resultsfile_1 = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_results_1.txt");
			File trec_results_1 = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_trec_results_1.txt");
			
			TrecEval.run_trec_eval(qrel, resultsfile, trec_results);
			TrecEval.run_trec_eval(qrel, resultsfile_1, trec_results_1);
			
			if (i == 0)
			{
				for (int k = 1; k < (numiter +1); k++)
				{
					File resultsfile_2 = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_results_"+ k +".txt");
					File trec_results_2 = new File(resultsdir.getAbsolutePath() + "/iter_" + i + "_trec_results"+ k + ".txt");
					
					TrecEval.run_trec_eval(qrel, resultsfile_2, trec_results_2);
				}
			}
			
		}
		
		outOfSyncRes = resultsdir;
	}
		SwingUtilities.invokeLater(new Runnable () 
		{
	public void run ()
	{
		JOptionPane.showMessageDialog(Refinement_UI.getInstance(), "Evaluation finished");
		new EvalResultsFrame(new File(outOfSyncRes.getAbsolutePath() + "/iter_0" + "_trec_results" + (numiter)+ ".txt"),new File(outOfSyncRes.getAbsolutePath() + "/iter_" + (numiter) + "_trec_results_1.txt") );
	}
		});
		
		EvalModel.getInstance().ThreadTerminates();
	}
}
