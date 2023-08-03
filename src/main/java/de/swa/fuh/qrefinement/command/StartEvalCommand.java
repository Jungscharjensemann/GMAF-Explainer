package de.swa.fuh.qrefinement.command;

import javax.swing.JOptionPane;

import de.swa.fuh.qrefinement.eval.EvalModel;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;

/**
 * 
 * 
 * This is a command class implementing command pattern. This command starts the asynchronous interactive evaluation tool.
 * 
 * @author Nicolas Boch
 *
 */
public class StartEvalCommand implements Command {

	@Override
	public void execute() {
		try
		{
		EvalModel.getInstance().starteval();
		}
		catch (IllegalStateException ex)
		{
			if (ex.getMessage().equals("Eval currently already running"))
			{
				SelectQueryCommandHistory.getInstance().add(new RequeryCommand());
			}
			else
			{
				JOptionPane.showMessageDialog(Refinement_UI.getInstance(), ex.getMessage());
			}
		}
	}

}
