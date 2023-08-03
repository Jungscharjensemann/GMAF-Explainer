package de.swa.ui.command;

import javax.swing.JOptionPane;

import de.swa.fuh.explanation.BooleanHelper;
import de.swa.gc.GraphCode;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;
import de.swa.ui.query.DefaultQueryExplainer;

public class QueryInfoCommand extends AbstractCommand {
	public void execute() {
		String s = Configuration.getInstance().getQueryUI();
		if (s.equals("")) s = "de.swa.fuh.explanation.MainExplanation";
		try {
			Class c = Class.forName(s);
			Object o = c.newInstance();
			DefaultQueryExplainer qe = (DefaultQueryExplainer)o;
			
			GraphCode gcQuery = MMFGCollection.getInstance().getCurrentQuery();
			if (gcQuery != null && BooleanHelper.getRefineButton())
				qe.explain(gcQuery);
			else JOptionPane.showMessageDialog(null, "<html>Please enter new query or select button for <p> query information when on detail panel.</p></html>");
			qe.explain(gcQuery);
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "no QueryExplainer set in config file.");
		}
	}
}
