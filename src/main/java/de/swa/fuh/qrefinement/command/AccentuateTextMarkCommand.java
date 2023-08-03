package de.swa.fuh.qrefinement.command;

import java.io.File;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;


/**
 * 
 * 
 * This is a command class implementing command pattern. This command points out a specific text mark in a text by printing it bold.
 *
 * @author Nicolas Boch
 */
public class AccentuateTextMarkCommand implements Command{
	
	
	/**
	 * Index of the text mark to be printed bold
	 */
	private int index;
	
	/**
	 * Reference to text mark object that is to be printed bold
	 */
	private TextMark mark;
	
	/**
	 * File reference to the text file that contains the text mark that is to be printed bold.
	 */
	private File fileRef;
	
	public AccentuateTextMarkCommand (int index, File f)
	{
		this.fileRef = f;
		this.index = index;
		
		if (this.index < 0 || this.index >= RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().size())
		{
			throw new IllegalArgumentException ();
		}
		
		this.mark = RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().get(this.index);
	}
	
	@Override
	public void execute() {
		RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).changeBold(mark);
	}
	

}
