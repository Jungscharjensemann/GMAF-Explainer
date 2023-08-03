package de.swa.ui.command;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.swa.ui.panels.LogPanel;

/** class to encapsulate the Plugin Configuration Command **/

public class PluginConfCommand extends AbstractCommand {
	public void execute() {
		try {
			String path = "conf" + File.separatorChar + "plugin.config";
			Desktop.getDesktop().open(new File(path));
			LogPanel.getCurrentInstance().addToLog("opening plugin configuration");
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "File cannot be opened. Please make sure that you have the correct permissions and set a default application for .config files");
			LogPanel.getCurrentInstance().addToLog("error: " + e1);
			e1.printStackTrace();
		}
	}
}
