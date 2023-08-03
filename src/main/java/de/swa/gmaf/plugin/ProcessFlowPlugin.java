package de.swa.gmaf.plugin;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import de.swa.gmaf.processingflow.ProcessFlow;
import de.swa.gmaf.processingflow.ProcessFlowFactory;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class ProcessFlowPlugin implements GMAF_Plugin {
	private Vector<ProcessFlow> flows = new Vector<ProcessFlow>();
	private ProcessFlow selectedFlow = null;
	
	public ProcessFlowPlugin() {
		flows = ProcessFlowFactory.getProcessingFlows();
	}
	
	public boolean canProcess(String extension) {
		for (ProcessFlow pf : flows) {
			if (pf.getFileExtension().equals(extension)) {
				selectedFlow = pf;
				return true;
			}
		}
		return false;
	}

	private Vector<Node> nodes = null;
	public Vector<Node> getDetectedNodes() {
		return nodes;
	}

	public boolean isGeneralPlugin() {
		return selectedFlow.isGeneral();
	}

	public void process(URL url, File f, byte[] bytes, MMFG fv) {
		nodes = selectedFlow.process(f, fv);
	}

	public boolean providesRecoursiveData() {
		return false;
	}
}
