package de.swa.fuh.qrefinement.logic;

import java.util.Vector;

import de.swa.gc.GraphCode;

/**
 * 
 * 
 * Interface to be implemented by concrete RF algorithms to calculate new query's GC. This is an implementation of Strategy Pattern.
 * 
 *  @author Nicolas Boch
 *
 */
public interface RelevanceFeedback {

	public GraphCode generateQuery (GraphCode rel, GraphCode nrel, Vector<GraphCode> vrel, Vector<GraphCode> vnrel);
	
}
