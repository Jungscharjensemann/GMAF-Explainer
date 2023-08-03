package de.swa.fuh.qrefinement.command;

/**
 * 
 * 
 * Interface for concrete commands. Interface is implemented by commands that can be redone / undone.
 * 
 * @author Nicolas Boch
 *
 */
public interface UndoableCommand {
	public void execute ();
	
	public void undo ();
	
	public void redo ();
}
