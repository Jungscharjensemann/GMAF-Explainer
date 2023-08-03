package de.swa.fuh.qrefinement.command;

import de.swa.fuh.qrefinement.logic.AdaptedRocchioGraphCodeMetricQuerying;
import de.swa.fuh.qrefinement.logic.NormalGraphCodeMetricQuerying;
import de.swa.fuh.qrefinement.logic.Querying;
import de.swa.fuh.qrefinement.logic.RelevanceFeedback;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;

/**
 * 
 *
 * This is a command class implementing command pattern. The algorithm used to form a new query can be changed with this command.
 * 
 * @author Nicolas Boch
 */
public class ChangeAlgorithmConfig implements UndoableCommand {

	/**
	 * weights of adapted rocchio algorithm
	 */
	private double oldA, oldB, oldC, newA, newB, newC;
	
	/**
	 * name of the new algorithm
	 */
	private String newalg;
	
	/**
	 * object instance of old algorithm
	 */
	private RelevanceFeedback oldalg;
	
	/**
	 * objects representing old and new query metrics
	 */
	private Querying oldmetric, newmetric;
	
	public ChangeAlgorithmConfig (String newalg)
	{
		this.newalg = newalg;
		newA = RefinementUIModel.getInstance().getA();
		newB = RefinementUIModel.getInstance().getB();
		newC = RefinementUIModel.getInstance().getC();
	}
	
	public ChangeAlgorithmConfig (String newalg, double a, double b, double c)
	{
		if (a < 0.0 || a > 1.0 || b < 0.0 || b > 1.0 || c < 0.0 || c > 1.0)
		{
			throw new IllegalArgumentException ("Parameters out of range");
		}
		this.newalg = newalg;
		newA = a;
		newB = b;
		newC = c;
	}
	
	@Override
	public void execute() {
		oldalg = RefinementUIModel.getInstance().getAlgorithm();
		oldA = RefinementUIModel.getInstance().getA();
		oldB = RefinementUIModel.getInstance().getB();
		oldC = RefinementUIModel.getInstance().getC();
		oldmetric = RefinementUIModel.getInstance().getQueryMetric();
		
		if (newalg.equals("Adapted Rocchio Algorithm"))
		{
			newmetric = new AdaptedRocchioGraphCodeMetricQuerying ();
		}
		else
		{
			newmetric = new NormalGraphCodeMetricQuerying ();
		}
		
		RefinementUIModel.getInstance().setAlgorithm(newalg);
		RefinementUIModel.getInstance().setA(newA);
		RefinementUIModel.getInstance().setB(newB);
		RefinementUIModel.getInstance().setC(newC);
		RefinementUIModel.getInstance().setQueryMetric(newmetric);
		
		
		
	}

	@Override
	public void undo() {
		RefinementUIModel.getInstance().setAlgorithm(oldalg);
		RefinementUIModel.getInstance().setQueryMetric(oldmetric);
		RefinementUIModel.getInstance().setA(oldA);
		RefinementUIModel.getInstance().setB(oldB);
		RefinementUIModel.getInstance().setC(oldC);

	}

	@Override
	public void redo() {
		RefinementUIModel.getInstance().setAlgorithm(newalg);
		RefinementUIModel.getInstance().setQueryMetric(newmetric);
		RefinementUIModel.getInstance().setA(newA);
		RefinementUIModel.getInstance().setB(newB);
		RefinementUIModel.getInstance().setC(newC);

	}

}
