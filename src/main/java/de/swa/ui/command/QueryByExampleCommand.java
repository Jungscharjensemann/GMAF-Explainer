package de.swa.ui.command;

import java.util.UUID;

import de.swa.fuh.explanation.SetValues;
import de.swa.gc.GraphCode;
import de.swa.mmfg.MMFG;
import de.swa.ui.Configuration;
import de.swa.ui.MMFGCollection;
import de.swa.ui.panels.AssetDetailPanel;
import de.swa.ui.panels.LogPanel;
import de.swa.ui.panels.QueryPanel;

/** class to encapsulate the Query By Example Command **/

public class QueryByExampleCommand extends AbstractCommand {
	private GraphCode qbe;
	private String session_id;
	
	public QueryByExampleCommand() {
		qbe = AssetDetailPanel.getCurrentInstance().getGraphCode();
	}
	
	public QueryByExampleCommand(String mmfg_id, String session_id) {
		MMFG mmfg = MMFGCollection.getInstance(session_id).getMMFGForId(UUID.fromString(mmfg_id));
		qbe = MMFGCollection.getInstance(session_id).getOrGenerateGraphCode(mmfg);
	}

	public void execute() {
//		Vector<String> dict = qbe.getDictionary();
//		String keywords = "";
//		for (String s : dict) keywords += s + ",";
//		
//		QueryByKeywordCommand qbk = new QueryByKeywordCommand(keywords);
//		qbk.execute();
		
		MMFGCollection.isQuery = true;
		SetValues.setValuesForQuery(Configuration.getInstance().getSelectedAsset().getName());
		LogPanel.getCurrentInstance().addToLog("executing query by example GC");
		QueryPanel.getCurrentInstance().setText("Query by Example: GraphCode " + Configuration.getInstance().getSelectedAsset().getName());
		if (session_id == null) MMFGCollection.getInstance().query(qbe);
		else MMFGCollection.getInstance(session_id).query(qbe);
		LogPanel.getCurrentInstance().addToLog("returning ranked result list.");
	}
}
