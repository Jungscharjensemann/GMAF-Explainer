package de.swa.ui.query;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeIO;
import de.swa.ui.MMFGCollection;

public class DefaultQueryExplainer {
	public void explain(GraphCode gcQuery) {
		String json = GraphCodeIO.asJson(gcQuery);
		System.out.println(json);
	}
}
