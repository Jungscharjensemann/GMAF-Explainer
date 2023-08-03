package de.swa.fuh.qrefinement.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.mmfg.MMFG;

/**
 * 
 * 
 * Utility class for executing the external tool trec eval
 * 
 * @author Nicolas Boch
 *
 */
public class TrecEval {
	
	/**
	 * directory of query result files
	 */
	private final static String resultsDir = "eval";
	private final static String trecparams = "-q -m official";

	private TrecEval () 
	{}
	
	/**
	 * Create directory for results of one experiment
	 * @return file reference of created directory
	 */
	public static File createResultsDir ()
	{
		long millis = System.currentTimeMillis();
		
		File dir = new File(resultsDir + "/" + millis);
		dir.mkdirs();
		
		return dir;
	}
	
	public static File createResultsFile (File dir, int iterno)
	{
		
		
		File resultsfile = new File(dir.getAbsolutePath() + "/iter_" + iterno + "_results.txt");
		try
		{
		resultsfile.createNewFile();
		return resultsfile;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void appendErgToFile (File file, int queryno)
	{
		try
		{
			FileWriter fw = new FileWriter(file,true);
			int lines = 0;
			for (MMFG m : RefinementCollection.getInstance().getCollection())
			{
				lines++;
				StringJoiner sj = new StringJoiner ("\t");
				sj.add(Integer.toString(queryno));
				sj.add("0");
				sj.add(FilenameUtils.removeExtension(m.getGeneralMetadata().getFileName()));
				sj.add(Integer.toString(lines));
				float [] metric_a = m.getSimilarity();
				float a = metric_a[0] * 100000 + metric_a[1] * 100 + metric_a[2];
				sj.add(Float.toString(a));
				sj.add("test");
				
				fw.write(sj.toString() + "\n");
				
			}
			
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void createresidualerg (File withoutrf, File currentiter, int queryno, int iterno)
	{
		try
		{
		BufferedReader frwo = new BufferedReader(new FileReader(withoutrf));
		BufferedReader frcurrentiter = new BufferedReader(new FileReader (currentiter));
		
		
		File withoutrfres = new File (FilenameUtils.removeExtension(withoutrf.getAbsolutePath()) + "_" + iterno + ".txt");
		File currentiterres = new File (FilenameUtils.removeExtension(currentiter.getAbsolutePath()) + "_1.txt");
		
		FileWriter fwwo = new FileWriter (withoutrfres, true);
		FileWriter fwcurrentiter = new FileWriter (currentiterres, true);
		
		String line = "";
		String[] linedata;
		Vector<String> marked = EvalModel.getInstance().getMarkedFiles();
		int numdel = 0;
		
		while ((line = frwo.readLine()) != null)
		{
			linedata = line.split("\t");
			
			if(Integer.parseInt(linedata[0]) == queryno)
			{
				if (marked.contains(linedata[2]))
				{
					numdel++;
				}
				else
				{
					StringJoiner sj = new StringJoiner("\t");
					sj.add(linedata[0]);
					sj.add(linedata[1]);
					sj.add(linedata[2]);
					sj.add(String.valueOf(Integer.parseInt(linedata[3])-numdel));
					sj.add(linedata[4]);
					sj.add(linedata[5]);
					fwwo.write(sj.toString() + "\n");
				}
			}
		}
		
		frwo.close();
		fwwo.close();
		
		numdel = 0;
		
		while ((line = frcurrentiter.readLine()) != null)
		{
			linedata = line.split("\t");
			
			if(Integer.parseInt(linedata[0]) == queryno)
			{
				if (marked.contains(linedata[2]))
				{
					numdel++;
				}
				else
				{
					StringJoiner sj = new StringJoiner("\t");
					sj.add(linedata[0]);
					sj.add(linedata[1]);
					sj.add(linedata[2]);
					sj.add(String.valueOf(Integer.parseInt(linedata[3])-numdel));
					sj.add(linedata[4]);
					sj.add(linedata[5]);
					fwcurrentiter.write(sj.toString() + "\n");
				}
			}
		}
		
		frcurrentiter.close();
		fwcurrentiter.close();
		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void run_trec_eval (File qrel, File resultfile, File trecresults)
	{
		
		try
		{
		String output = "";
		StringJoiner sj = new StringJoiner (" ");
		sj.add("trec_eval");
		sj.add(trecparams);
		sj.add(qrel.getAbsolutePath());
		sj.add(resultfile.getAbsolutePath());
		
		String command = sj.toString();
		
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec(command);
		
		java.io.BufferedReader stdOutput = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream())); // InputStream == stdout of the process
		java.io.BufferedReader stdError = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream())); // ErrorStream == stderr of the process
		
		while (p.isAlive() || stdOutput.ready() || stdError.ready()) {
			
			// Read Output Stream
			if (stdOutput.ready()) {
				output += stdOutput.readLine() + "\n";
			}
			
			// Read Error Stream
			if (stdError.ready()) {
				output += stdError.readLine() + "\n";
			}
			
		}
		
		p.destroy();
		
		FileWriter fw = new FileWriter(trecresults);
		fw.write(output);
		fw.close();
		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
}
