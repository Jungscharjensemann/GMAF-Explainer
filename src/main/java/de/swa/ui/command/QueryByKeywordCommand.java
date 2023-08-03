package de.swa.ui.command;

import java.util.Vector;

import de.swa.fuh.explanation.SetValues;
import de.swa.gc.GraphCode;
import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.gmaf.extensions.defaults.Word;
import de.swa.gmaf.extensions.wikidata.SemWebExtension;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;
import de.swa.ui.panels.LogPanel;

/** class to encapsulate the Query By Keyword Command **/

public class QueryByKeywordCommand extends AbstractCommand {
	private String query;
	private String session_id;

	public QueryByKeywordCommand(String q) {
		query = q;
	}
	
	public void setSessionId(String session_id) {
		this.session_id = session_id;
	}

	public void execute() {
		LogPanel.getCurrentInstance().addToLog("executing query: " + query);
		MMFGCollection.isQuery = true;
		if (query.trim().equals("")) MMFGCollection.isQuery = false;
				
		// Comma Separated Query with Vocabulary Terms
		String[] str = query.split(",");
		Vector<String> vocTerms = new Vector<String>();
		for (String s : str) {
			if (!s.equals("")) {
				// Synonyms from Dictionary
				Vector<Word> words = GeneralDictionary.getInstance().getWord(s.trim());
				for (Word w : words) {
					if (!vocTerms.contains(s)) {
						vocTerms.add(w.getWord());
					}
				}
				if (words.size() == 0) vocTerms.add(s.trim());
				
				// Synonyms from Wikidata
				SemWebExtension se = new SemWebExtension();
				Vector<String> wiki = se.getSynonymNamesForConcept(s);
				for (String syn : wiki ) {
					if (!vocTerms.contains(syn)) {
						vocTerms.add(syn);
					}
				}
			}
		}

		// Generate a Query-GraphCode
		GraphCode gcQuery = new GraphCode();
		gcQuery.setDictionary(vocTerms);
		SetValues.setValuesForQuery(null);

		// Execute this query on the collection of MMFGs
		if (session_id != null) MMFGCollection.getInstance(session_id).query(gcQuery);
		else MMFGCollection.getInstance().query(gcQuery);

		LogPanel.getCurrentInstance().addToLog("returning ranked result list.");
	}	
}
