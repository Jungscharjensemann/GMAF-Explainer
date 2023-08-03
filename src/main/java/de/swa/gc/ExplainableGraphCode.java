package de.swa.gc;

import de.swa.gmaf.extensions.Explainable;
import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.mmfg.extension.esmmfg.Phrase;

public class ExplainableGraphCode extends GraphCode implements Explainable {
	GeneralDictionary dict = GeneralDictionary.getInstance();
	private Phrase root;
	
	public ExplainableGraphCode(GraphCode gc) {
		root = new Phrase(new Phrase(dict.getFirstWord("the"), Phrase.DET), 
				new Phrase(dict.getFirstWord("selection"), Phrase.N), 
				new Phrase(dict.getFirstWord("contains"), Phrase.V), 
				new Phrase(dict.getFirstWord("the"), Phrase.DET),
				new Phrase(dict.getFirstWord("following"), Phrase.PP),
				new Phrase(dict.getFirstWord("elements"), Phrase.N));

		Phrase enumeration = new Phrase();
		root.addPhrase(enumeration);
		
		for (String d : gc.getDictionary()) {
			enumeration.addPhrase(new Phrase(dict.getFirstWord(d), Phrase.N));
			
			Phrase the = new Phrase(dict.getFirstWord("the"), Phrase.DET);
			Phrase noun = new Phrase(dict.getFirstWord(d), Phrase.N);
			Phrase np = new Phrase(the, noun);

			for (String d2 : gc.getDictionary()) {
				if (!d2.equals(d)) {
					Phrase otherNoun = new Phrase(dict.getWord(d2).get(0), Phrase.N);
					Phrase otherNp = new Phrase(the, otherNoun);
					int val = gc.getEdgeValueForTerms(d, d2);
					
					if (val != 0) {
						Phrase relationship = Phrase.getPhraseForRelationship(val);
						relationship.addPhrase(np);
						relationship.addPhrase(otherNp);
						root.addPhrase(relationship);
					}
				}
			}
		}
	}
	
	public Phrase getPSTree() {
		return root;
	}
}
