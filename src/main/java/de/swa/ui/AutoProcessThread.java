package de.swa.ui;

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
import de.swa.ui.command.ReloadCommand;
import de.swa.ui.panels.AssetListPanel;
import de.swa.ui.panels.LogPanel;

/** this thread is used as background processing of GMAF assets **/
public class AutoProcessThread extends Thread {
	public void run() {
		while (true) {
			try {
				LogPanel.getCurrentInstance().addToLog("auto processing started");
				
				for (MMFG m : MMFGCollection.getInstance().getCollection()) {
					String fileName = m.getGeneralMetadata().getFileName();
					File existingMMFG = new File(Configuration.getInstance().getMMFGRepo() + File.separatorChar + fileName + ".mmfg");
					if (!existingMMFG.exists()) {
						GMAF gmaf = new GMAF();
						try {
							File f = m.getGeneralMetadata().getFileReference();
							FileInputStream fs = new FileInputStream(f);
							byte[] bytes = fs.readAllBytes();
							MMFG fv = gmaf.processAsset(bytes, f.getName(), "system", Configuration.getInstance().getMaxRecursions(),	Configuration.getInstance().getMaxNodes(), f.getName(), f);
							LogPanel.getCurrentInstance().addToLog("MMFG created");
							
							String xml = FeatureVectorBuilder.flatten(fv, new XMLEncodeDecode());
							RandomAccessFile rf = new RandomAccessFile(Configuration.getInstance().getMMFGRepo() + File.separatorChar + f.getName() + ".mmfg", "rw");
							rf.setLength(0);
							rf.writeBytes(xml);
							rf.close();
							
							LogPanel.getCurrentInstance().addToLog("MMFG exported to " + Configuration.getInstance().getMMFGRepo());
							
							GraphCode gc = GraphCodeGenerator.generate(fv);
							GraphCodeIO.write(gc, new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + f.getName() + ".gc"));
							
							LogPanel.getCurrentInstance().addToLog("GraphCode exported to " + Configuration.getInstance().getGraphCodeRepository());
							MMFGCollection.getInstance().addToCollection(fv);
							new ReloadCommand();
						}
						catch (Exception x) {
							x.printStackTrace();
							LogPanel.getCurrentInstance().addToLog("error " + x.getMessage());
						}
						AssetListPanel.getCurrentInstance().refresh();
					}
				}
				
				LogPanel.getCurrentInstance().addToLog("auto processing finished");
				LogPanel.getCurrentInstance().addToLog("next run in 5 minutes");
				Thread.sleep(1000 * 60 * 2);
			}
			catch (Exception x) {}
		}
	}
}
