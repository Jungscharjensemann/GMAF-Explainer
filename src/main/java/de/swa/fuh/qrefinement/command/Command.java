package de.swa.fuh.qrefinement.command;

/**
 * 
 * 
 * Interface for concrete commands. Interface is implemented by commands that cannot be redone / undone.
 * 
 * @author Nicolas Boch
 *
 */
public interface Command {

	public void execute ();
}
