package de.swa.fuh.qrefinement.qrm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Set;

import de.swa.fuh.qrefinement.logic.Querying;
import de.swa.fuh.qrefinement.logic.RelevanceFeedback;


/**
 * class representing data relevant for all UI components. Only one instance of this class is allowed to exist at the same time (Singleton)
 * @author Nicolas Boch
 *
 */
public class RefinementUIModel {

	/**
	 * static instance variable representing the single instance of this class
	 */
	private static RefinementUIModel instance;
	/**
	 * File reference of the currently selected asset
	 */
	private File selection;
	
	private RelevanceFeedback algorithm;
	private double a;
	private double b;
	private double c;
	private Querying queryMetric;
	
	private int[] weightofrelations = {0,1,1,1,1,1,1,1,1,1};
	
	private Hashtable<String,Class<?>> algorithms = new Hashtable <String,Class<?>>();
	
	private Hashtable <Class<?>, Hashtable <String, Integer>> configpriority = new Hashtable <Class<?>, Hashtable <String, Integer>> ();
	
	/**
	 * replace the single instance by a new instance or create an instance of this class for the first time.
	 */
	public RefinementUIModel ()
	{
		instance = this;
	}
	
	/**
	 * returns a File reference to the currently selected asset
	 */
	public File getSelectedAsset ()
	{
		return selection;
	}
	
	/**
	 * Resets the currently selected asset.
	 * @param f File reference to the new selection
	 */
	public void setSelectedAsset(File f)
	{
		selection = f;
	}
	
	/**
	 * Implementation of Singleton pattern. Returns the single instance of this class.
	 */
	public static RefinementUIModel getInstance ()
	{
		return instance;
	}
	
	public RelevanceFeedback getAlgorithm()
	{
		return algorithm;
	}
	
	public void setAlgorithm (String name)
	{
		Class<?> classobj = algorithms.get(name);
		
		if (classobj == null)
		{
			throw new IllegalArgumentException ("No such algorithm found");
		}
		
		
			try {
				algorithm = (RelevanceFeedback)classobj.getConstructors()[0].newInstance();
			} catch (IllegalAccessException| InstantiationException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
	}
	
	public void setAlgorithm (RelevanceFeedback rf)
	{
		algorithm = rf;
	}
	
	public Set<String> getAlgorithmNames ()
	{
		return algorithms.keySet();
	}
	
	public Querying getQueryMetric ()
	{
		return queryMetric;
	}
	
	public void setQueryMetric (Querying q)
	{
		queryMetric = q;
	}
	
	public double getA ()
	{
		return a;
	}
	
	public void setA (double input)
	{
		a = input;
	}
	
	public double getB ()
	{
		return b;
	}
	
	public void setB (double input)
	{
		b = input;
	}
	
	public double getC ()
	{
		return c;
	}
	
	public void setC (double input)
	{
		c = input;
	}
	
	public Hashtable<String,Class<?>> getAlgorithms()
	{
		return algorithms;
	}
	
	public void registerAlgorithm (String classname, ClassLoader loader)
	{
		try
		{
			Class<?> classobj;
			if (loader != null)
			{
				classobj = loader.loadClass(classname);
			}
			else
			{
				classobj = Class.forName(classname);
			}
		
		if (classobj.getInterfaces()[0] != RelevanceFeedback.class)
		{
			throw new IllegalArgumentException ("Class not implementing RelevanceFeedback interface");
		}
		
		Method m = classobj.getMethod("getName");
		String name = (String) m.invoke(null);
		
		if (name == null)
		{
			System.out.println("No Name found");
		}
		
		algorithms.put(name, classobj);
		
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException m)
		{
			m.printStackTrace();
		} 
		
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void addPriorityToConfig (Class<?> c, String relationship, int priority)
	{
		if (configpriority.get(c) == null)
		{
			Hashtable<String, Integer> value = new Hashtable <String,Integer>();
			value.put(relationship, priority);
			configpriority.put(c, value);
		}
		else
		{
			configpriority.get(c).put(relationship, priority);
		}
	}
	
	public int[] getRelationshipWeights ()
	{
		return weightofrelations;
	}
}
