package de.swa.fuh.qrefinement.command;

import java.awt.CardLayout;

import de.swa.fuh.qrefinement.qrm.Refinement_UI;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command closes UI elements to specify relevance marks and lets the user return to the main part of the UI
 *
 *
 * @author Nicolas Boch
 */
public class BackToMainCardCommand implements Command {

	@Override
	public void execute() {
		CardLayout c = (CardLayout)Refinement_UI.getInstance().getCards().getLayout();
		//RefinementListTable.getInstance().refresh();
		//RefinementDetailPanel.getCurrentInstance().refresh();
		c.show(Refinement_UI.getInstance().getCards(), Refinement_UI.CONTENTPANEL);

	}

}
 