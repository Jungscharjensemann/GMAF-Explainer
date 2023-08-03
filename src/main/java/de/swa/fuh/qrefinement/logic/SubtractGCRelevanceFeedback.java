package de.swa.fuh.qrefinement.logic;

import java.util.Vector;

import de.swa.gc.GraphCode;

/**
 * 
 * 
 * Class implementing contruction of new query based on subtraction API for graph codes. This class implements Strategy Pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class SubtractGCRelevanceFeedback implements RelevanceFeedback 
{

	private static final String name = "Subtract GCs";
	
	@Override
	public GraphCode generateQuery(GraphCode rel, GraphCode nrel, Vector<GraphCode> vrel, Vector<GraphCode> vnrel) {
		return RefinementGCGenerator.subtractGCs(rel, nrel);
	}
	
	public static String getName()
	{
		return name;
	}

}
