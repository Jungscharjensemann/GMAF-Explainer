package de.swa.gmaf.plugin;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.builder.Mpeg7IO;

public class Mpeg7Import implements GMAF_Plugin {
	public boolean canProcess(String extension) {
		if (extension.endsWith("xml")) return true;
		return false;
	}

	private Vector<Node> allNodes = new Vector<Node>();
	public Vector<Node> getDetectedNodes() {
		return null;
	}

	public boolean isGeneralPlugin() {
		return false;
	}

	public void process(URL url, File f, byte[] bytes, MMFG fv) {
		try {
			Mpeg7IO mpeg7 = new Mpeg7IO();
			MMFG mmfg = mpeg7.unflatten(f.getAbsolutePath());
			for (Node n : mmfg.allNodes) {
				fv.addNode(n);
			}
			allNodes = mmfg.allNodes;
			fv.setGeneralMetadata(mmfg.getGeneralMetadata());
			fv.setSecurity(mmfg.getSecurity());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean providesRecoursiveData() {
		return false;
	}
}
