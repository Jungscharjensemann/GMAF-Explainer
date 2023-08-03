package de.swa.fuh.qrefinement.logic;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.gc.GraphCode;

/**
 * 
 * 
 * This class implements adapted GC metrics based on the vector space model. Class is constructed using Strategy Pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class AdaptedRocchioGraphCodeMetricQuerying implements Querying {

	@Override
	public void query(GraphCode gcQuery) {
		RefinementCollection.getInstance().queryVRM(gcQuery);

	}

}
