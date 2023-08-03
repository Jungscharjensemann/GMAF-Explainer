package de.swa.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import de.swa.gmaf.Service;
import de.swa.ui.command.CommandHistory;
import de.swa.ui.command.ReloadCommand;

/** main class of GMAF **/
public class Main {
	public static String basedir = ".";
	
	public static void main(String[] args) {
		if (args.length == 1) basedir = args[0];
		new Main();
	}
	
	public Main() {
		if (Configuration.getInstance().launchServer()) {
			Service.start();
		}
		
		FlatLightLaf.install();
		//new LoginDialog();
		// load configuration first
		new ProgressFrame();
		ProgressFrame.getInstance().setProgress(1, "initializing configuration");
		CommandHistory.getInstance().addCommand(new ReloadCommand());
		ProgressFrame.getInstance().setProgress(2, "loading collection");
		MMFGCollection collection = MMFGCollection.getInstance();
		
		if (Configuration.getInstance().getUIMode().equals("light")) FlatLightLaf.install();
		else FlatDarkLaf.install();
		FlatDarkLaf.install();
		// start UI
		new GMAF_UI();
	}
}
