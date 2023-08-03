package de.swa.fuh.explanation;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Setting the values for query by example and query by keyword
 *
 */
public class SetValues {
	/**
	 * 
	 * @param queryName (name of query asset, is null for query by keywords)
	 */
	public static void setValuesForQuery(String queryName) {
		NamesHelper.setQueryName(queryName);
		BooleanHelper.setRefineButton(true);
		BooleanHelper.setSelectedOk(false);
		BooleanHelper.setIsRefined(false);
	}
}
