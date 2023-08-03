package de.swa.gc;

import java.util.Hashtable;
import java.util.Vector;

import de.swa.gmaf.extensions.SemanticExtensionFactory;

/** Semantic Graph Code Extension - WORK IN PROGRESS 
 * 
 * @author stefan_wagenpfeil
 */

public class SemanticGraphCode extends GraphCode {
	private static final long serialVersionUID = 2L;
	private GraphCode originalGC;
	private Vector<String> semanticDictionary = new Vector<String>();
	private Hashtable<Integer, Integer> mapping = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> reverseMapping = new Hashtable<Integer, Integer>();
	
	public SemanticGraphCode(GraphCode gc) {
		super();
		originalGC = gc;
		
		if (originalGC != null) transform();
	}
	
	private void transform() {
		for (int i = 0; i < originalGC.getDictionary().size(); i++) {
			String term = originalGC.getDictionary().get(i);
			String semId = SemanticExtensionFactory.getInstance().getCollectionIdForConcept(term);
			// not found in ontology
			if (semId.equals(term) ) {
				if (semId.length() < 5) {
					continue;
				}
			}

			if (!semanticDictionary.contains(semId)) {
				semanticDictionary.add(semId);
			}
			int newIndex = semanticDictionary.indexOf(semId);
			reverseMapping.put(newIndex, i);
			mapping.put(i, newIndex);
		}
	}
	
	public Vector<String> getDictionary() {
		return semanticDictionary;
	}
	
	public int getValue(int x, int y) {
		if (mapping.get(x) != null && mapping.get(y) != null)
			return originalGC.getValue(mapping.get(x), mapping.get(y));
		else return 0;
	}
	
	public int getEdgeValueForTerms(String term1, String term2) {
		int x = getNormalizedIndexForTerm(term1);
		int y = getNormalizedIndexForTerm(term2);
		
		int originalX = reverseMapping.get(x);
		int originalY = reverseMapping.get(y);
		
		return originalGC.getValue(originalX, originalY);
	}
	
	private int getNormalizedIndexForTerm(String s) {
		String id = SemanticExtensionFactory.getInstance().getCollectionIdForConcept(s);
		return semanticDictionary.indexOf(id);
	}
}
