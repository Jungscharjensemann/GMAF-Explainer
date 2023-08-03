package de.swa.fuh.qrefinement.command;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;
import de.swa.ui.MMFGCollection;

/**
 * 
 *
 *	This is a command class implementing command pattern. This command closes the UI of the query refinement part of the application and lets the user return to GMAF-UI
 *
 * @author Nicolas Boch
 */
public class BackToGMAFCommand implements Command {

	@Override
	public void execute() {
		MMFGCollection.getInstance().query(RefinementCollection.getInstance().getCurrentQuery());
		//AssetListPanel.getCurrentInstance().refresh();
		
		RefinementCollection.clear();
		Refinement_UI.getInstance().dispose();
		Refinement_UI.clear();

	}

}
