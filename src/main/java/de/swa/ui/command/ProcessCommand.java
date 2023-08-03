package de.swa.ui.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.GraphCodeIO;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.builder.FeatureVectorBuilder;
import de.swa.mmfg.builder.XMLEncodeDecode;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;
import de.swa.ui.panels.AssetListPanel;
import de.swa.ui.panels.LogPanel;

/** class to encapsulate the Process Command **/

public class ProcessCommand extends AbstractCommand implements Runnable {
	private File f;
	public ProcessCommand(File f) {
		this.f = f;
	}
	public void execute() {
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		LogPanel.getCurrentInstance().addToLog("processing " + f.getAbsolutePath());
		
		GMAF gmaf = new GMAF();
		try {
			FileInputStream fs = new FileInputStream(f);
			byte[] bytes = fs.readAllBytes();
			MMFG fv = gmaf.processAsset(bytes, f.getName(), "system", Configuration.getInstance().getMaxRecursions(),	Configuration.getInstance().getMaxNodes(), f.getName(), f);
			System.out.println("ProcessCommand: " + f.getName());
			LogPanel.getCurrentInstance().addToLog("MMFG created");
			
			String xml = FeatureVectorBuilder.flatten(fv, new XMLEncodeDecode());
			RandomAccessFile rf = new RandomAccessFile(Configuration.getInstance().getMMFGRepo() + File.separatorChar + f.getName() + ".mmfg", "rw");
			rf.setLength(0);
			rf.writeBytes(xml);
			rf.close();
			
			LogPanel.getCurrentInstance().addToLog("MMFG exported to " + Configuration.getInstance().getMMFGRepo());
			
			GraphCode gc = GraphCodeGenerator.generate(fv);
			GraphCodeIO.write(gc, new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + f.getName() + ".gc"));
			MMFGCollection.getInstance().replaceMMFGInCollection(fv, f);
			
			LogPanel.getCurrentInstance().addToLog("GraphCode exported to " + Configuration.getInstance().getGraphCodeRepository());
		}
		catch (Exception x) {
			x.printStackTrace();
			LogPanel.getCurrentInstance().addToLog("error " + x.getMessage());
		}
		AssetListPanel.getCurrentInstance().refresh();
	}
}
