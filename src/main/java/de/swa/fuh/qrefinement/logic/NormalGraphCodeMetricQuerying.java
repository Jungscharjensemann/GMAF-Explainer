package de.swa.fuh.qrefinement.logic;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.gc.GraphCode;

/**
 * 
 * 
 * This class implements an object representation of normal GC similarity metrics. The metric to be used can be configured during runtime. The class is constructed using Strategy Pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class NormalGraphCodeMetricQuerying implements Querying {

	@Override
	public void query(GraphCode gcQuery) {
		RefinementCollection.getInstance().query(gcQuery);

	}

}
