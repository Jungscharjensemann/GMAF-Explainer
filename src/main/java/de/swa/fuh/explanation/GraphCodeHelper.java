package de.swa.fuh.explanation;

import de.swa.gc.GraphCode;

/**
 * 
 * @author Annelie Lausch 6953476
 *
 *         Setter and getter for Graph Code
 */
public class GraphCodeHelper {
	private static GraphCode gc;

	public static void setGraphCode(GraphCode gc) {
		GraphCodeHelper.gc = gc;
	}

	public static GraphCode getGraphCode() {
		return gc;
	}
}
