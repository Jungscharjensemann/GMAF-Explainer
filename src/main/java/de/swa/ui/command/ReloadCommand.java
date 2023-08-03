package de.swa.ui.command;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import de.swa.ui.Configuration;
import de.swa.ui.Main;
import de.swa.ui.panels.AssetListPanel;

/** class to encapsulate the Reload Command **/

public class ReloadCommand extends AbstractCommand {
	public void execute() {
		try {
			RandomAccessFile rf = new RandomAccessFile(Main.basedir + File.separatorChar + "conf" + File.separatorChar + "gmaf.config", "r");
			String line = "";
			String collectionName = "";
			String graphCodeRepo = "";
			String exportFolder = "";
			String mmfgRepository = "";
			Vector<String> collectionPaths = new Vector<String>();
			Vector<String> fileEx = new Vector<String>();
			String uimode = "";
			String maxNodes = "";
			String maxRecursions = "";
			String thumbNails = "";
			String autoProcess = "";
			String semExt = "";
			String launchServer = "";
			String collectionProc = "";
			String collectionConf = "";
			String queryEx = "";
			String rdfRepo = "";
			String serverPort = "8142";
			String flows = "";
			String srv = "localhost";
			String ctx = "gmaf";
			String password = "";
			String restServicePort = "8242";
			
			while ((line = rf.readLine()) != null) {
				if (line.equals("")) continue;
				if (line.startsWith("#")) continue;
				try {
					if (line.startsWith("collectionName")) {
						collectionName = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					if (line.startsWith("exportFolder")) {
						exportFolder = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("collectionPath")) {
						String paths = line.substring(line.indexOf("=") + 1, line.length()).trim();
						String[] str = paths.split(",");
						for (String s : str) {
							collectionPaths.add(s.trim());
						}
					}
					else if (line.startsWith("graphCodeRepository")) {
						graphCodeRepo = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("mmfgRepository")) {
						mmfgRepository = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("fileExtensions")) {
						String exs = line.substring(line.indexOf("=") + 1, line.length()).trim();
						String[] str = exs.split(",");
						for (String s : str) {
							fileEx.add(s.trim());
						}
					}
					else if (line.startsWith("uiMode")) {
						uimode = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("maxNodes")) {
						maxNodes = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("maxRecursions")) {
						maxRecursions = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("thumbnailFolder")) {
						thumbNails = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("autoProcess")) {
						autoProcess = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("semanticExtension")) {
						semExt = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("launchServer")) {
						launchServer = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("collectionProcessorConfig")) {
						collectionConf = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("collectionProcessor")) {
						collectionProc = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("queryExplainerClass")) {
						queryEx = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("rdfRepository")) {
						rdfRepo = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("serverPort")) {
						serverPort = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("restServicePort")) {
						restServicePort = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("processingFlowFolder")) {
						flows = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("serverName")) {
						srv = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("serverContext")) {
						ctx = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
					else if (line.startsWith("api_key")) {
						password = line.substring(line.indexOf("=") + 1, line.length()).trim();
					}
 				}
				catch (Exception x) {}
			}
			
			Configuration.getInstance().setConfig(collectionName, collectionPaths, graphCodeRepo, exportFolder, fileEx, mmfgRepository, uimode, maxNodes, maxRecursions, thumbNails, autoProcess, semExt, launchServer, collectionProc, collectionConf, queryEx, rdfRepo, serverPort, flows, srv, ctx, password, restServicePort);
			if (AssetListPanel.getCurrentInstance() != null) AssetListPanel.getCurrentInstance().refresh();
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
}
