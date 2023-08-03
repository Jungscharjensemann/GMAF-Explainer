package de.swa.ui.command;

import de.swa.ui.panels.LogPanel;
import de.swa.ui.panels.QueryPanel;

/** class to encapsulate the Clear Queue Command **/
public class ClearQueryCommand extends AbstractCommand {
	public void execute() {
		QueryPanel.getCurrentInstance().setText("");
		LogPanel.getCurrentInstance().addToLog("query panel cleared");
	}
}
