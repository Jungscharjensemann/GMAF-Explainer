package de.swa.fuh.xml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class ExternalPlugin extends DefaultHandler implements GMAF_Plugin {
	Vector<String> extensions = new Vector<String>();
	Properties prop = new Properties();
	
	public ExternalPlugin() {
		try {
			prop.load(new FileInputStream("conf/externalPlugin.config"));
			prop.keys();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String extension = "";
	public boolean canProcess(String extension) {
		if (extension.startsWith(".")) extension = extension.substring(1, extension.length());
		if (extensions.contains(extension)) {
			this.extension = extension;
			return true;
		}
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
			String call = "" + prop.get(extension);
			String tempFile = "tmp/ext_" + System.currentTimeMillis() + ".xml";
			Process p = Runtime.getRuntime().exec(call + " '" + f.getAbsolutePath() + "' '" + tempFile + "'");
			int i = p.waitFor();

			GenericXMLImporter importer = new GenericXMLImporter();
			importer.process(url, new File(tempFile), bytes, fv);
			allNodes = fv.allNodes;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean providesRecoursiveData() {
		return false;
	}
	

}

