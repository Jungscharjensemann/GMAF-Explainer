package de.swa.ui.command;

import java.util.Vector;

/** Command History based on the Command Pattern **/

public class CommandHistory {
	private CommandHistory() {}
	private static CommandHistory instance = null;
	public static synchronized CommandHistory getInstance() {
		if (instance == null) instance = new CommandHistory();
		return instance;
	}
	
	private Vector<AbstractCommand> commands = new Vector<AbstractCommand>();
	public void addCommand(AbstractCommand c) {
		commands.add(c);
		c.execute();
	}
}
