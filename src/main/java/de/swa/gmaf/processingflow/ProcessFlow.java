package de.swa.gmaf.processingflow;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.swa.gmaf.plugin.FeatureFusionStrategy;
import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.builder.Flattener;

public class ProcessFlow extends DefaultHandler {
	private Vector<ProcessElement> flowElements = new Vector<ProcessElement>();
	
	public ProcessFlow(File xmlFile) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(xmlFile, this);
			prepareFlow();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// parse XML File
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		ProcessElement pe = new ProcessElement(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			pe.addAttribute(attributes.getQName(i), attributes.getValue(i));
		}
		flowElements.add(pe);
	}
	
	Hashtable<String, GMAF_Plugin> pluginDefinitions = new Hashtable<String, GMAF_Plugin>();
	Hashtable<String, FeatureFusionStrategy> fusionDefinitions = new Hashtable<String, FeatureFusionStrategy>();
	Hashtable<String, Flattener> exportDefinitions = new Hashtable<String, Flattener>();
	Hashtable<String, String> resourceTypes = new Hashtable<String, String>();
	Hashtable<String, String> resourceLocations = new Hashtable<String, String>();
	
	private void prepareFlow() {
		for (ProcessElement pe : flowElements) {
			if (pe.getName().equals("plugin-definition")) {
				try {
					String cls = pe.getAttributeValue("class");
					Class c = Class.forName(cls);
					Object o = c.newInstance();
					GMAF_Plugin plugin = (GMAF_Plugin)o;
					pluginDefinitions.put(pe.getAttributeValue("name"), plugin);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (pe.getName().equals("fusion-definition")) {
				try {
					String cls = pe.getAttributeValue("class");
					Class c = Class.forName(cls);
					Object o = c.newInstance();
					FeatureFusionStrategy ffs = (FeatureFusionStrategy)o;
					fusionDefinitions.put(pe.getAttributeValue("name"), ffs);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (pe.getName().equals("export-definition")) {
				try {
					String cls = pe.getAttributeValue("class");
					Class c = Class.forName(cls);
					Object o = c.newInstance();
					Flattener flattener = (Flattener)o;
					exportDefinitions.put(pe.getAttributeValue("name"), flattener);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (pe.getName().equals("resource-definition")) {
				resourceTypes.put(pe.getAttributeValue("name"), pe.getAttributeValue("type"));
				resourceLocations.put(pe.getAttributeValue("name"), pe.getAttributeValue("location"));
			}
		}
	}
	
	public Vector<Node> process(File f, MMFG fv) {
		Vector<Node> nodes = new Vector<Node>();
		Vector<MMFG> mmfgs = new Vector<MMFG>();
		for (ProcessElement pe : flowElements) {
			if (pe.getName().equals("param")) {
				String pluginOrFeature = pe.getAttributeValue("name");
				String value = pe.getAttributeValue("value");
				String[] str = pluginOrFeature.split(".");
				String plugin = str[0];
				String attribute = str[1];
				
				Object instance = null;
				if (pluginDefinitions.get(plugin) != null) instance = pluginDefinitions.get(plugin);
				else if (fusionDefinitions.get(plugin) != null) instance = fusionDefinitions.get(plugin);
				try {
					instance.getClass().getField(attribute).set(instance, value);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (pe.getName().equals("mmfg")) {
				String[] str = pe.getAttributeValue("processor").split(",");
				for (String s : str) {
					GMAF_Plugin gp = pluginDefinitions.get(s.trim());
					MMFG mmfg = new MMFG();
					gp.process(null, f, null, mmfg);
					mmfgs.add(mmfg);
				}
			}
			else if (pe.getName().equals("fusion")) {
				String s = pe.getAttributeValue("processor");
				FeatureFusionStrategy ffs = fusionDefinitions.get(s);
				if (mmfgs.size() == 0) mmfgs.add(fv);
				ffs.optimize(fv, mmfgs);
				mmfgs.clear();
			}
			else if (pe.getName().equals("export")) {
				String to = pe.getAttributeValue("target");
				String exp = pe.getAttributeValue("exporter");
				
				if (to.equals("collection")) {
					nodes = fv.allNodes;
				}
				if (exp != null) {
					Flattener fl = exportDefinitions.get(exp);
					String s = fl.flatten(fv);
					String toDir = resourceLocations.get(to);
					try {
						RandomAccessFile rf = new RandomAccessFile(toDir + "/" + f.getName() + "." + fl.getFileExtension(), "rw");
						rf.writeBytes(s);
						rf.close();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return nodes;
	}
	
	public String getName() {
		return flowElements.get(0).getAttributeValue("name");
	}

	public String getFileExtension() {
		return flowElements.get(0).getAttributeValue("extension");
	}
	
	public boolean isGeneral() {
		return Boolean.parseBoolean(flowElements.get(0).getAttributeValue("isGeneral"));
	} 
}

/*

 <process-flow name="ImageImport" extension="*.jpg" isGeneral="false">
	<plugin-definition name="plugin1" class="de.swa.bla.Blub"/>
	<plugin-definition name="plugin2" class="de.swa.bla.Blub"/>
	<plugin-definition name="plugin3" class="de.swa.bla.Blub"/>
	<plugin-definition name="plugin4" class="de.swa.bla.Blub"/>
	<plugin-definition name="plugin5" class="de.swa.bla.Blub"/>	

	<fusion-definition name="merge1" class="de.swa.feat.Bla"/>
	<fusion-definition name="merge2" class="de.swa.feat.Bla"/>
	<fusion-definition name="merge3" class="de.swa.feat.Bla"/>
	
	<export-definition name="mpeg7" class="de.swa.exp.Bla"/>
	<export-definition name="xml" class="de.swa.exp.Bla"/>
	<export-definition name="graphml" class="de.swa.exp.Bla"/>
		
	<resource-definition name="upload-dir" type="folder" location="temp/upload"/>
	<resource-definition name="target-dir" type="folder" location="temp/target"/>
	<resource-definition name="export-dir" type="folder" location="temp/export"/>
	<resource-definition name="facebook" type="url" location="http://www...."/>

	<param name="plugin1.lod" value="2"/>
	<param name="plugin2.output" value="temp"/>
	
	<flow-source name="upload-dir"/>
	<mmfg processor="plugin1, plugin2, plugin3"/>
	<fusion processor="merge1"/>
	
	<param name="plugin5.source" value="5"/>
	<mmfg processor="plugin5"/>
	<fusion processor="merge3"/>
	<export target="export-dir" exporter="mpeg7"/>
	<export target="collection"/>
</process-flow>

*/