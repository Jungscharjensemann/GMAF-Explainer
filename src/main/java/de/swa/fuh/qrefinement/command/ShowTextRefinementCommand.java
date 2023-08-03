package de.swa.fuh.qrefinement.command;

import java.awt.CardLayout;

import de.swa.fuh.qrefinement.qrm.Refinement_UI;

/**
 * 
 * 
 * This is a command class implementing command pattern. This command opens the frame of the UI where a user can set relevance marks for parts of a text.
 * 
 * @author Nicolas Boch
 *
 */
public class ShowTextRefinementCommand implements Command {

	@Override
	public void execute() {
		CardLayout c = (CardLayout)Refinement_UI.getInstance().getCards().getLayout();
		
		c.show(Refinement_UI.getInstance().getCards(), Refinement_UI.TEXTPANEL);

	}

}
