package de.swa.fuh.qrefinement.command;

import javax.swing.JOptionPane;

import de.swa.fuh.qrefinement.eval.EvalModel;
import de.swa.fuh.qrefinement.qrm.Refinement_UI;

/**
 * 
 * 
 * This is a command class implementing command pattern. This command stops the asynchronous interactive evaluation tool.
 * 
 * @author Nicolas Boch
 *
 */
public class StopEvalCommand implements Command {

	@Override
	public void execute() {
		try
		{
		EvalModel.getInstance().stopeval();
		JOptionPane.showMessageDialog(Refinement_UI.getInstance(), "Evaluation Mode stopped");
		}
		catch (IllegalStateException ex)
		{
			JOptionPane.showMessageDialog(Refinement_UI.getInstance(), ex.getMessage());
		}

	}

}
