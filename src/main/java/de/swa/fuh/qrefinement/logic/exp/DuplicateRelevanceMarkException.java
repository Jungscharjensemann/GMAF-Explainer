package de.swa.fuh.qrefinement.logic.exp;

import java.io.File;

/**
 * 
 * 
 * Exception stating non-technical problem with user's input of overlapping relevance marks for parts of an asset and the whole asset at the same time
 * 
 * @author Nicolas Boch
 *
 */
public class DuplicateRelevanceMarkException extends Exception {

	private File duplicate;
	
	public DuplicateRelevanceMarkException (String str, File f)
	{
		super(str);
		duplicate = f;
	}
	
	public File getDuplicateFile ()
	{
		return duplicate;
	}
}
