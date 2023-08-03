package de.swa.ui.command;

import javax.swing.JOptionPane;

import de.swa.fuh.qrefinement.qrm.RefinementController;
import de.swa.ui.MMFGCollection;


public class QueryRefinementCommand extends AbstractCommand {
	
	
	@Override
	public void execute() {
		
		if (MMFGCollection.getInstance().getCurrentQuery() == null)
		{
			JOptionPane.showMessageDialog(null, "No Query recently executed!");
			return;
		}
		RefinementController.main(MMFGCollection.getInstance().getCurrentQuery(),MMFGCollection.getInstance());
	}
 
}
