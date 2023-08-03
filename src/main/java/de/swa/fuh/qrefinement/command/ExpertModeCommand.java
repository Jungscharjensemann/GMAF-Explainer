package de.swa.fuh.qrefinement.command;

import javax.swing.JComponent;

import de.swa.fuh.qrefinement.qrm.Refinement_UI;




/**
 * 
 * This class implements the showing or hiding of an expert mode in UI.
 * 
 * @author Nicolas Boch
 *
 */
public class ExpertModeCommand implements Command {

	@Override
	public void execute() {
		
		if (Refinement_UI.getInstance().getExpertMode())
		{
			for (JComponent c : Refinement_UI.getInstance().getExpertComponents())
			{
				c.setVisible(false);
			}
			
			Refinement_UI.getInstance().setExpertMode(false);
		}
		else
		{
			for (JComponent c : Refinement_UI.getInstance().getExpertComponents())
			{
				c.setVisible(true);
			}
			
			Refinement_UI.getInstance().setExpertMode(true);
		}

	}

}
