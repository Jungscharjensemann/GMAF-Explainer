package de.swa.fuh.qrefinement.logic.exp;

/**
 * 
 * 
 * Exception stating problem in generation process of a new query due to no relevance input provided at all
 * 
 * @author Nicolas Boch
 *
 */
public class NoRelevanceMarkException extends Exception {

	public NoRelevanceMarkException (String message)
	{
		super(message);
	}
}
