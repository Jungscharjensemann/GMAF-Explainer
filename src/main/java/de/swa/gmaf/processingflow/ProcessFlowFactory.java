package de.swa.gmaf.processingflow;

import java.io.File;
import java.util.Vector;

import de.swa.ui.Configuration;


public class ProcessFlowFactory {
	public static Vector<ProcessFlow> getProcessingFlows() {
		Vector<ProcessFlow> flows = new Vector<ProcessFlow>();
		File flowDirectory = new File(Configuration.getInstance().getProcessingFlowFolder());
		File[] fs = flowDirectory.listFiles();
		for (File fi : fs) {
			if (fi.getName().endsWith(".xml")) {
				ProcessFlow pf = new ProcessFlow(fi);
				flows.add(pf);
			}
		}
		return flows;
	}
}
