package de.swa.ui;

import java.io.File;
import java.util.Vector;

import de.swa.gmaf.api.GMAF_SessionFactory;
import de.swa.mmfg.MMFG;
import de.swa.ui.command.ReloadCommand;
import de.swa.ui.panels.LogPanel;

/** data structure representing the config files of the GMAF **/
public class Configuration {
	private Configuration() {}
	
	private static Configuration instance;
	private Vector<String> collectionPaths;
	private String collectionName;
	private String graphCodeRepo;
	private String exportFolder;
	private String mmfgRepository;
	private String thumbNailFolder;
	private Vector<String> fileExtensions;
	private String uimode;
	private int maxNodes = 50, maxRecursions = 2;
	private String autoProcess;
	private String launchServer;
	private String semanticFactoryClass;
	private String collectionProcessor = "de.swa.gc.processing.DefaultCollectionProcessor";
	private String collectionConfig;
	private String queryInformationUI;
	private String rdfRepository;
	private String flowFolder;
	private int serverPort;
	private int restServicePort;
	private String serverName;
	private String context;
	private boolean showBoundingBox = true;
	
	public static synchronized Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
			ReloadCommand rc = new ReloadCommand();
			rc.execute();
		}
		return instance;
	}

	private Vector<MMFG> selection = new Vector<MMFG>();
	public Vector<MMFG> getSelectedItems() {
		return selection;
	}
	
	public void select(MMFG m) {
		selection.add(m);
	}
	
	public void unselect(MMFG m) {
		selection.remove(m);
	}
	
	public void setConfig(String name, Vector<String> paths, String gcRepo, String export, Vector<String> fileEx, String mmfgRepo, String ui, String nodes, String recursions, String thumbNail, String auto, String semFact, String launch, String collectionProc, String collectionConf, String queryUI, String rdf, String serverPort, String flows, String serverName, String context, String password, String restServicePort) {
		try {
			collectionName = name;
			collectionPaths = paths;
			graphCodeRepo = gcRepo;
			exportFolder = export;
			fileExtensions = fileEx;
			mmfgRepository = mmfgRepo;
			uimode = ui;
			maxNodes = Integer.parseInt(nodes);
			maxRecursions = Integer.parseInt(recursions);
			thumbNailFolder = thumbNail;
			autoProcess = auto;
			semanticFactoryClass = semFact;
			launchServer = launch;
			collectionProcessor = collectionProc;
			collectionConfig = collectionConf;
			queryInformationUI = queryUI;
			rdfRepository = rdf;
			flowFolder = flows;
			this.serverPort = Integer.parseInt(serverPort);
			this.serverName = serverName;
			this.context = context;
			this.restServicePort = Integer.parseInt(restServicePort);
			GMAF_SessionFactory.API_KEY = password;
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public Vector<String> getCollectionPaths() {
		return collectionPaths;
	}
	
	public boolean showBoundingBox() {
		return showBoundingBox;
	}
	
	public void showBoundingBox(boolean b) {
		showBoundingBox = b;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public String getGraphCodeRepository() {
		return graphCodeRepo;
	}
	
	public String getExportFolder() {
		return exportFolder;
	}
	
	public Vector<String> getFileExtensions() {
		return fileExtensions;
	}
	
	private File selectedAsset;
	private MMFG selectedMMFG;
	public void setSelectedAsset(File f) {
		selectedAsset = f;
		selection.clear();
		selectedMMFG = MMFGCollection.getInstance().getMMFGForFile(f);
		selection.add(selectedMMFG);
	}
	
	public MMFG getSelectedMMFG() {
		return selectedMMFG;
	}
	
	public File getSelectedAsset() {
		return selectedAsset;
	}
	
	public String getMMFGRepo() {
		return mmfgRepository;
	}
	
	public String getUIMode() {
		return uimode;
	}
	
	public int getMaxNodes() {
		return maxNodes;
	}
	
	public int getMaxRecursions() {
		return maxRecursions;
	}
	
	public String getThumbnailPath() {
		return thumbNailFolder;
	}
	
	public String getSemanticExtension() {
		return semanticFactoryClass;
	}
	
	public boolean isAutoProcess() {
		return autoProcess.equals("true");
	}
	
	public boolean launchServer() {
		return launchServer.equals("true");
	}
	
	public String getCollectionProcessorClass() {
		return collectionProcessor;
	}
	
	public String getCollectionProcessorConfigClass() {
		return collectionConfig;
	}
	
	public String getQueryUI() {
		return queryInformationUI;
	}
	
	public String getRDFRepo() {
		return rdfRepository;
	}

	public int getServerPort() {
		return serverPort;
	}
	
	public int getRestServicePort() {
		return restServicePort;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public String getProcessingFlowFolder() {
		return flowFolder;
	}
	
	public String getContext() {
		return context;
	}
}
