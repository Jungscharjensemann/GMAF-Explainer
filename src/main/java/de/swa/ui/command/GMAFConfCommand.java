package de.swa.ui.command;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.swa.ui.panels.LogPanel;

/** class to encapsulate the Configuration Command **/

public class GMAFConfCommand extends AbstractCommand {
	public void execute() {
		try {
			Desktop.getDesktop().open(new File("conf" + File.separatorChar + "gmaf.config"));
			LogPanel.getCurrentInstance().addToLog("opening gmaf configuration");

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "File cannot be opened. Please make sure that you have the correct permissions and set a default application for .conf files");
			LogPanel.getCurrentInstance().addToLog("error: " + e1);
			e1.printStackTrace();
		}
	}
}
