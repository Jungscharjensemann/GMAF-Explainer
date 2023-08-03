package de.swa.fuh.explanation;

import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;

import de.swa.gc.GraphCode;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Graph Code calculations for displaying the ranking information of
 *         results
 *
 */
public class ResultExplanation {
	private static JTextPane resultPane;

	/**
	 *
	 * @param query        (query Graph Code)
	 * @param result       (result Graph Code)
	 * @param isComparison (false for one result asset, true for comparison of two
	 *                     result assets)
	 *
	 */
	public ResultExplanation(GraphCode query, GraphCode result, boolean isComparison) {
		resultPane = new JTextPane();
		boolean isRefined = BooleanHelper.getIsRefined();

		GraphCode gcQ_result = new GraphCode();
		GraphCode gcR_result = new GraphCode();
		Vector<String> dictionary = new Vector<String>();
		// Creating new dirctionary which only contains the common features of query and
		// result
		for (String s : query.getDictionary()) {
			if (result.getDictionary().contains(s))
				dictionary.add(s);
		}
		gcQ_result.setDictionary(dictionary);
		gcR_result.setDictionary(dictionary);
		// Getting matrix values from both query and result asset to create two new
		// Graph Codes with same dictionary but different matrix values
		for (String s : dictionary) {
			for (String t : dictionary) {
				int valueQ = query.getValue(query.getDictionary().indexOf(s), query.getDictionary().indexOf(t));
				int valueR = result.getValue(result.getDictionary().indexOf(s), result.getDictionary().indexOf(t));
				gcQ_result.setValueForTerms(s, t, valueQ);
				gcR_result.setValueForTerms(s, t, valueR);
			}
		}
		CreateDocumentFromGC doc = new CreateDocumentFromGC(gcQ_result, gcR_result, isComparison, isRefined);
		DefaultStyledDocument document = doc.getDocument();

		resultPane = new JTextPane(document);
	}

	public JTextPane getResultPane() {
		return resultPane;
	}
}
