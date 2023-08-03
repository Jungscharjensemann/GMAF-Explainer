package de.swa.fuh.qrefinement.qrm;

import java.util.Vector;

import de.swa.fuh.qrefinement.logic.NormalGraphCodeMetricQuerying;
import de.swa.gc.GraphCode;
import de.swa.mmfg.MMFG;
import de.swa.ui.Configuration;
import de.swa.ui.MMFGCollection;

/**
 * RefinementController class containing static main method to be executed on startup.
 * 
 * @author Nicolas Boch
 *
 *
 */
public class RefinementController extends Thread{
	
	/**
	 * MMFGCollection containing references to all assets to be used in refinement application
	 */
	private MMFGCollection coll;
	/**
	 * Graph Code representing the query that is to be refined
	 */
	private GraphCode q;
	/**
	 * static instance variable for implementation of Singleton pattern
	 */
	public static RefinementController instance;

	/**
	 * main method to start the refinement application in the context of GMAF UI
	 * 
	 * @param query the query to be refined
	 * @param collection the collection containing references to all assets
	 */
	public static void main(GraphCode query, MMFGCollection collection) {
		createInstance(query, collection);
		
	}
	
	
	/**
	 * main method to start the application as a stand alone tool. Only used for test purpose to start the application from command line.
	 * @param args
	 */
	public static void main (String args[])
	{
		createInstance(null,null);
	}
	
	
	/**
	 * initializes the single instance of this class
	 * @param query the query to be refined
	 * @param collection the collection containing references to all assets
	 */
	public static synchronized void createInstance(GraphCode query, MMFGCollection collection)
	{
		instance = new RefinementController();
		instance.coll = collection;
		instance.q = query;
		instance.start();
	} 
	
	/**
	 * Overriding run method from Thread class. Initializes the UI of the application and deep copies all MMFG references in the collection
	 * to assure a handling of the collection that is independent from the calling GMAF implementation
	 */
	public void run()
	{
		if (RefinementCollection.getInstance() == null) {
			RefinementCollection.setInstance(coll);
			Vector<MMFG> v = RefinementCollection.getInstance().getCollection();
			for (int i = 0; i < RefinementCollection.getInstance().getCollection().size(); i++) {
				v.set(i, new MMFG(v.get(i)));
			}
			RefinementCollection.getInstance().init();
			new RefinementUIModel();
			RefinementUIModel.getInstance().setSelectedAsset(Configuration.getInstance().getSelectedAsset());
			RefinementUIModel.getInstance().registerAlgorithm("de.swa.fuh.qrefinement.logic.RelationshipWeightRocchioRelevanceFeedback", null);
			RefinementUIModel.getInstance().registerAlgorithm("de.swa.fuh.qrefinement.logic.SubtractGCRelevanceFeedback", null);
			RefinementUIModel.getInstance().setAlgorithm("Subtract GCs");
			RefinementUIModel.getInstance().setQueryMetric(new NormalGraphCodeMetricQuerying ());
			Refinement_UI.init();
			RefinementCollection.getInstance().query(q);
		}
	}

}
