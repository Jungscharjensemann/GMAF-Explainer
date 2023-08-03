package de.swa.fuh.qrefinement.qrm;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import de.swa.fuh.qrefinement.ui.datamodel.AssetRectangles;
import de.swa.fuh.qrefinement.ui.datamodel.AssetTextMarks;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.GraphCodeIO;
import de.swa.gc.GraphCodeMetric;
import de.swa.mmfg.MMFG;
import de.swa.ui.Configuration;
import de.swa.ui.MMFGCollection;


/**
 * Class representing the collection of all assets
 * @author Nicolas Boch
 *
 */
public class RefinementCollection {
	
	/**
	 * Vector of references to MMFG objects that each represent an indexed asset
	 */
	private Vector<MMFG> collection = new Vector<MMFG>();
	private String name = "";
	
	/**
	 * static instance variable for implementation of Singleton pattern
	 */
	private static RefinementCollection instance;
	/** singleton pattern access **/
	public static synchronized RefinementCollection getInstance() {
		if (instance == null) return null;
		return instance;
	}
	
	/**
	 * Hashtable to map files to relevance marks in image assets
	 */
	private Hashtable<File,AssetRectangles> relevanceMarks = new Hashtable<File,AssetRectangles>();
	
	/**
	 * Hashtable to map files to relevance marks in text assets
	 */
	private Hashtable<File,AssetTextMarks> textRelevanceMarks = new Hashtable<File,AssetTextMarks>();
	
	/**
	 * initialize the single instance of this class by cloning an existing collection of assets
	 * @param coll
	 */
	public static synchronized void setInstance(MMFGCollection coll)
	{
		if (instance == null)
		{
			instance = new RefinementCollection();
			//instance.collection = new Vector<MMFG>(coll.getCollection());
			instance.collection = (Vector<MMFG>)coll.getCollection().clone();
		}
	}
	
	/**
	 * set the single instance of this class to null. Should be called when closing the refinement application but not exiting the whole GMAF application.
	 */
	public static synchronized void clear()
	{
		instance = null;
	}
	
	/** session facade pattern access **/
	private static Hashtable<String, RefinementCollection> sessions = new Hashtable<String, RefinementCollection>();

	/**
	 * constructor not visible because only one instance of this class must exist at the same time (singleton)
	 */
	private RefinementCollection() {}

	/**
	 * Initializes the mapping of asset files to relevance marks
	 */
	public void init ()
	{
		for (MMFG m : collection)
		{
			File f = m.getGeneralMetadata().getFileReference();
			if (Tools.isImageFile(f))
			{
				relevanceMarks.put(f, new AssetRectangles(f));
			}
			else
			{
				textRelevanceMarks.put(f, new AssetTextMarks(f));
			}
		}
	}
	

	
	/**
	 * Returns the mapping of image assets to relevance marks  
	 */
	public Hashtable<File,AssetRectangles> getRelevanceMarks ()
	{
		return relevanceMarks;
	}
	
	/**
	 * Returns the mapping of text assets to relevance marks
	 */
	public Hashtable<File,AssetTextMarks> getTextRelevanceMarks()
	{
		return textRelevanceMarks;
	}
	
	/** returns the collection of MMFGs **/
	public Vector<MMFG> getCollection() {
		return collection;
	}
	

	
	/** returns the collection name **/
	public String getName() {
		return name;
	}

	
	/**
	 * represents the last executed query
	 */
	private GraphCode currentQuery;
	
	/**
	 * returns the last executed query
	 */
	public GraphCode getCurrentQuery() {
		return currentQuery;
	}
	
	/** returns a vector of all collection's Graph Codes **/
	public Vector<GraphCode> getCollectionGraphCodes() {
		Vector<GraphCode> v = new Vector<GraphCode>();
		for (MMFG m : RefinementCollection.getInstance().getCollection()) {
			GraphCode gc = RefinementCollection.getInstance().getOrGenerateGraphCode(m);
			v.add(gc);
		}
		return v;
	}
	
	/** returns or generates a Graph Code for a given MMFG **/
	public GraphCode getOrGenerateGraphCode(MMFG mmfg) {
		if (graphCodeCache.containsKey(mmfg)) return graphCodeCache.get(mmfg);
		GraphCode gc = GraphCodeGenerator.generate(mmfg);
		graphCodeCache.put(mmfg, gc);
		return gc;
	}
	
	/** returns similar assets based on a Graph Code query **/
	public Vector<MMFG> getSimilarAssets(GraphCode gcQuery) {
		Vector<MMFG> tempCollection = new Vector<MMFG>();
		for (MMFG m : collection) {
			try {
				GraphCode gc = null;
				File f = new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + m.getGeneralMetadata().getFileName() + ".gc");
				if (graphCodeCache.containsKey(m)) gc = graphCodeCache.get(m);
				else if (f.exists()) {
					gc = GraphCodeIO.read(f);
					graphCodeCache.put(m, gc);
				}
				if (gc == null) {
					gc = GraphCodeGenerator.generate(m);
					if (gc.getDictionary().size() > 1) {
						GraphCodeIO.write(gc, f);
						graphCodeCache.put(m, gc);
					}
				}
	
				// similarity value is stored in the MMFGs
				float[] sim = GraphCodeMetric.calculateSimilarity(gcQuery, gc);
				m.setTempSimilarity(sim);
			}
			catch (Exception x) {
				x.printStackTrace();
				// no Graph Code found or generated
				System.out.println("no graph code found");
				m.setTempSimilarity(new float[] {0f,0f,0f});
			}
			tempCollection.add(m);
		}

		// sort collection according to the calculated similarity
		Collections.sort(tempCollection, new Comparator<MMFG>() {
			public int compare(MMFG mmfg1, MMFG mmfg2) {
				float[] metric_a = mmfg1.getTempSimilarity();
				float[] metric_b = mmfg2.getTempSimilarity();
				
				// calculate numeric values to support java-compatible comparison
				float a = metric_a[0] * 100000 + metric_a[1] * 100 + metric_a[2];
				float b = metric_b[0] * 100000 + metric_b[1] * 100 + metric_b[2];
				
				return (int)(b - a);
			};
		});
		
		return tempCollection;
	}
	
	/** returns recommended assets based on a Graph Code query **/
	public Vector<MMFG> getRecommendedAssets(GraphCode gcQuery) {
		Vector<MMFG> tempCollection = new Vector<MMFG>();
		for (MMFG m : collection) {
			try {
				GraphCode gc = null;
				File f = new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + m.getGeneralMetadata().getFileName() + ".gc");
				if (graphCodeCache.containsKey(m)) gc = graphCodeCache.get(m);
				else if (f.exists()) {
					gc = GraphCodeIO.read(f);
					graphCodeCache.put(m, gc);
				}
				if (gc == null) {
					gc = GraphCodeGenerator.generate(m);
					if (gc.getDictionary().size() > 1) {
						GraphCodeIO.write(gc, f);
						graphCodeCache.put(m, gc);
					}
				}
	
				// similarity value is stored in the MMFGs
				float[] sim = GraphCodeMetric.calculateSimilarity(gcQuery, gc);
				m.setTempSimilarity(sim);
			}
			catch (Exception x) {
				x.printStackTrace();
				// no Graph Code found or generated
				System.out.println("no graph code found");
				m.setTempSimilarity(new float[] {0f,0f,0f});
			}
			tempCollection.add(m);
		}

		// sort collection according to the calculated similarity
		Collections.sort(tempCollection, new Comparator<MMFG>() {
			public int compare(MMFG mmfg1, MMFG mmfg2) {
				float[] metric_a = mmfg1.getTempSimilarity();
				float[] metric_b = mmfg2.getTempSimilarity();
				
				// calculate numeric values to support java-compatible comparison
				float a = metric_a[1] * 100000 + metric_a[0] * 100 + metric_a[2];
				float b = metric_b[1] * 100000 + metric_b[0] * 100 + metric_b[2];
				
				return (int)(b - a);
			};
		});
		
		return tempCollection;
	}
	
	public void setCurrentQuery (GraphCode gc)
	{
		currentQuery = gc;
	}
	
	/**
	 * contains a mapping of MMFGs to their corresponding Graph Codes to speed up query execution by reducing the number of File IO accesses
	 */
	private Hashtable<MMFG, GraphCode> graphCodeCache = new Hashtable<MMFG, GraphCode>();
	
	
	public Hashtable<MMFG, GraphCode> getCache ()
	{
		return graphCodeCache;
	}
	
	/** executes a query **/
	public void query(GraphCode gcQuery) {
		
		currentQuery = gcQuery;
		for (MMFG m : collection) {
			try {
				GraphCode gc = null;
				File f = new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + m.getGeneralMetadata().getFileName() + ".gc");
				if (graphCodeCache.containsKey(m)) gc = graphCodeCache.get(m);
				else if (f.exists()) {
					gc = GraphCodeIO.read(f);
					graphCodeCache.put(m, gc);
				}
				if (gc == null) {
					gc = GraphCodeGenerator.generate(m);
					if (gc.getDictionary().size() > 1) {
						GraphCodeIO.write(gc, f);
						graphCodeCache.put(m, gc);
					}
				}
	
				// similarity value is stored in the MMFGs
				float[] sim = GraphCodeMetric.calculateSimilarity(gcQuery, gc);
				m.setSimilarity(sim);
			}
			catch (Exception x) {
				x.printStackTrace();
				// no Graph Code found or generated
				System.out.println("no graph code found");
				m.setSimilarity(new float[] {0f,0f,0f});
			}
		}
		
		
		System.out.println ("Similarities calculated, now sorting");

		// sort collection according to the calculated similarity
		Collections.sort(collection, new Comparator<MMFG>() {
			public int compare(MMFG mmfg1, MMFG mmfg2) {
				float[] metric_a = mmfg1.getSimilarity();
				float[] metric_b = mmfg2.getSimilarity();
				
				// calculate numeric values to support java-compatible comparison
				float a = metric_a[0] * 100000 + metric_a[1] * 100 + metric_a[2];
				float b = metric_b[0] * 100000 + metric_b[1] * 100 + metric_b[2];
				
				return (int)(b - a);
			};
		});
		System.out.println("sorted, now refreshing Panel");
		
		//RefinementAssetListPanel.getCurrentInstance().refresh();
		RefinementAssetListPanel.getCurrentInstance().resort();
	}
	
	public void queryVRM (GraphCode gcQuery)
	{
		currentQuery = gcQuery;
		Thread[] assets = new Thread [collection.size()];
		int z = 0;
		
		for (MMFG m : collection) {

			assets[z] = new Thread (new Runnable () {
				public void run ()
				{
			try {
				float [] sim = new float[3];
				GraphCode gc = null;
				File f = new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + m.getGeneralMetadata().getFileName() + ".gc");
				if (graphCodeCache.containsKey(m)) gc = graphCodeCache.get(m);
				else if (f.exists()) {
					gc = GraphCodeIO.read(f);
					graphCodeCache.put(m, gc);
				}
				if (gc == null) {
					gc = GraphCodeGenerator.generate(m);
					if (gc.getDictionary().size() > 1) {
						GraphCodeIO.write(gc, f);
						graphCodeCache.put(m, gc);
					}
				}
				
				System.out.println ("GraphCode generated for MMFG: " + m.getGeneralMetadata().getFileName());
	
				// similarity value is stored in the MMFGs
				//float[] sim = GraphCodeMetric.calculateSimilarity(gcQuery, gc);
				
				Vector<String> dictionary = new Vector<String> ();
				
				for (String s : gcQuery.getDictionary())
				{
					if (!dictionary.contains(s))
					{
						dictionary.add(s);
					}
				}
				
				for (String s : gc.getDictionary())
				{
					if (!dictionary.contains(s))
					{
						dictionary.add(s);
					}
				}
				
				System.out.println ("Dictionary generated for MMFG: " + m.getGeneralMetadata().getFileName());
				System.out.println ("Size of Dictionary: " + dictionary.size());
																	
				System.out.println ("GraphCodes transformed to new dictionary for MMFG: " + m.getGeneralMetadata().getFileName());
				
				float node_metric = 0f;
				float scalar = 0f;
				int quadsum_gcQuery = 0;
				int quadsum_gc = 0;
				float edge_metric = 0f;
				float edge_type_metric = 0f;
				float k = 0f;
				float sumdifnondiagonal = 0f;
				float l = 0f;

				int max = 0;
				int edge_count = 0;
				int num_non_diagonal = dictionary.size() * dictionary.size() - dictionary.size();
				
				for (String s : gcQuery.getDictionary())
				{
					for (String t : gcQuery.getDictionary())
					{
						if (!s.equals(t))
						{
							if (gcQuery.getEdgeValueForTerms(s, t) > max)
							{
								max = gcQuery.getEdgeValueForTerms(s,t);
							}
						}
					}
				}
				
				System.out.println("Max value of nondiagonal query fields calculated for MMFG: " + m.getGeneralMetadata().getFileName());
				
				for (String s : gcQuery.getDictionary())
				{
					for (String t : gcQuery.getDictionary())
					{
						int i = 0;
						try
						{
							i = gc.getEdgeValueForTerms(s, t);
						}
						catch (Exception e)
						{
							
						}
						if (s.equals(t))
						{
							scalar += gcQuery.getEdgeValueForTerms(s, t) * i;
							quadsum_gcQuery += gcQuery.getEdgeValueForTerms(s, t) * gcQuery.getEdgeValueForTerms(s, t);
							quadsum_gc += i * i;
						}
						else
						{
							l = (float)gcQuery.getEdgeValueForTerms(s, t)/ (float)max;
							
							if (l >= 0.5)
							{
								
								
								if (i >= 1)
								{
									edge_count ++;
								}
							}
							
							k = (float) gcQuery.getEdgeValueForTerms(s, t) / 10f;
							sumdifnondiagonal += Math.abs((float)RefinementUIModel.getInstance().getRelationshipWeights()[i] - k);	
							
						}
					}
				}
				
				
				node_metric = scalar / ((float)Math.sqrt(quadsum_gcQuery) * (float)Math.sqrt(quadsum_gc));
												
				edge_metric = (float)edge_count / (float) num_non_diagonal;
				edge_type_metric = (float)num_non_diagonal / sumdifnondiagonal;
				
				if (edge_type_metric == Float.POSITIVE_INFINITY)
				{
					edge_type_metric = 1000f;
				}
				if (edge_type_metric == Float.NEGATIVE_INFINITY)
				{
					edge_type_metric = 0f;
				}
				
				sim[0] = node_metric;
				sim[1] = edge_metric;
				sim[2] = edge_type_metric;
				
				System.out.println("Similarities for MMFG " + m.getGeneralMetadata().getFileName());
				System.out.println("Node metric " + sim[0]);
				System.out.println("Edge metric " + sim[1]);
				System.out.println("Edge type metric " + sim[2]);
				m.setSimilarity(sim);

			}
			catch (Exception x) {
				x.printStackTrace();
				// no Graph Code found or generated
				System.out.println("no graph code found");
				m.setSimilarity(new float[] {0f,0f,0f});
			}
		}
			});
			
			assets[z].start();
			z++;
		}
		
		
		for (int i = 0; i < collection.size(); i++)
		{
			try
			{
			assets[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		
		System.out.println ("Similarities calculated, now sorting");

		// sort collection according to the calculated similarity
		Collections.sort(collection, new Comparator<MMFG>() {
			public int compare(MMFG mmfg1, MMFG mmfg2) {
				float[] metric_a = mmfg1.getSimilarity();
				float[] metric_b = mmfg2.getSimilarity();
				
				// calculate numeric values to support java-compatible comparison
				float a = metric_a[0] * 100000 + metric_a[1] * 100 + metric_a[2];
				float b = metric_b[0] * 100000 + metric_b[1] * 100 + metric_b[2];
				
				return (int)(b - a);
			};
		});
		System.out.println("sorted, now refreshing Panel");
		
		System.out.println ("new Sorting");
		for (MMFG m : collection)
		{
			
			System.out.println(m.getGeneralMetadata().getFileName());
			System.out.println("Metric: " + (m.getSimilarity()[0] * 100000 + m.getSimilarity()[1] * 100 + m.getSimilarity()[2]));
			System.out.println("Metric1: " + m.getSimilarity()[0]);
			System.out.println("Metric2: " + m.getSimilarity()[1]);
			System.out.println("Metric3: " + m.getSimilarity()[2]);
		}
		
		//RefinementAssetListPanel.getCurrentInstance().refresh();
		RefinementAssetListPanel.getCurrentInstance().resort();
	}
}

