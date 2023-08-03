package de.swa.fuh.qrefinement.command;

/**
 * 
 *
 * This is a command class implementing command pattern. This command terminates the program.
 * 
 * @author Nicolas Boch
 */
public class ExitCommand implements Command {

	@Override
	public void execute() {
		System.exit(0);

	}

}
