package de.swa.fuh.qrefinement.eval;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import de.swa.fuh.qrefinement.logic.AdaptedRocchioGraphCodeMetricQuerying;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;


/**
 * 
 *
 *	Class representing config of asynchronous interactive evaluation mode and controlling execution of interactive evaluation mode.
 *
 * @author Nicolas Boch
 */
public class EvalModel {

	/**
	 * Singleton Pattern. This class variable is a reference to the single instance.
	 */
	private static EvalModel instance;
	
	/**
	 * thread where evaluation is executed including automatic execution of test queries
	 */
	private Thread evalthread;
	
	/**
	 * states whether eval mode should currently be active
	 */
	private boolean shouldrun;
	
	/**
	 * The test queries to be used in evaluation
	 */
	private Vector<String> queries = new Vector<String>();
	
	/**
	 * Number of iterations of relevance feedback in eval mode
	 */
	private int numiter;
	
	/**
	 * Weight param of adapted rocchio algorithm
	 */
	private float a;
	
	/**
	 * Weight param of adapted rocchio algorithm
	 */
	private float b;
	
	/**
	 * Weight param of adapted rocchio algorithm
	 */
	private float c;
	
	/**
	 * name of the algorithm to be used to construct new queries by processing relevance marks
	 */
	private String algorithm;
	
	/**
	 * Assets that were marked with relevance marks by a user in the last iteration of relevance feedback.
	 */
	private Vector<String> markedfiles = new Vector <String>();
	
	private EvalModel ()
	{
		//loadConfig();
	}
	
	public static EvalModel getInstance ()
	{
		if (instance == null)
		{
			instance = new EvalModel();
		}
		
		return instance;
	}
	
	public void loadConfig ()
	{
		if (shouldrun == true)
		{
			throw new IllegalStateException ("Change of config not allowed while eval is running");
		}
		try
		{
			InputStream stream = new FileInputStream ("eval/eval.properties");
			Properties prop = new Properties();
			prop.load(stream);
			
			a = Float.parseFloat(prop.getProperty("a"));
			b = Float.parseFloat(prop.getProperty("b"));
			c = Float.parseFloat(prop.getProperty("c"));
			
			
			algorithm = prop.getProperty("algorithm");
			numiter = Integer.parseInt(prop.getProperty("numiter"));
			
			stream.close();
			
			Reader in = new FileReader ("eval/queries.csv");
			CSVFormat.Builder builder = CSVFormat.EXCEL.builder();
			builder.setHeader().setSkipHeaderRecord(true);
			builder.setDelimiter(';');
			CSVFormat csvf = builder.build();
			Iterable<CSVRecord> records = csvf.parse(in);
			
			for (CSVRecord record : records)
			{
				queries.add(record.get("Query"));
			}
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void starteval ()
	{
		System.out.println("Eval Starting 1...");
		if (shouldrun == true)
		{
			throw new IllegalStateException("Eval currently already running");
		}
		queries.clear();
		loadConfig();
		
		
		
		if (evalthread != null)
		{
			if (evalthread.isAlive())
			{
				evalthread.interrupt();
				evalthread = null;
			}
		}
		
		RefinementUIModel.getInstance().setA(a);
		RefinementUIModel.getInstance().setB(b);
		RefinementUIModel.getInstance().setC(c);
		RefinementUIModel.getInstance().setAlgorithm(algorithm);
		if (algorithm.equals("Adapted Rocchio Algorithm"))
		{
			RefinementUIModel.getInstance().setQueryMetric(new AdaptedRocchioGraphCodeMetricQuerying());
		}
		
		shouldrun = true;
		evalthread = new EvalThread (queries, numiter);
		evalthread.start();
		
	}
	
	public boolean shouldrun ()
	{
		return shouldrun;
	}
	
	public void stopeval ()
	{
		if (evalthread == null)
		{
			throw new IllegalStateException ("Eval not running!");
		}
		
		
		shouldrun = false;
		if (evalthread.getState() == Thread.State.WAITING)
		{
			synchronized (evalthread)
			{
			evalthread.notify();
			}
		}
	}
	
	public void ThreadTerminates ()
	{
		shouldrun = false;
		evalthread = null;
	}
	
	public void notifyThread ()
	{
		if (evalthread != null)
		{
			synchronized (evalthread)
			{
				if (evalthread.getState() == Thread.State.WAITING)
				{
			evalthread.notify();
				}
			}
		}
	}
	
	public Vector<String> getMarkedFiles ()
	{
		return markedfiles;
	}
}
