package de.swa.fuh.xml;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;

public class GenericXMLImporter extends DefaultHandler implements GMAF_Plugin {
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
		this.fv = fv;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			current = new Node();
			fv.addNode(current);
			sp.parse(f, this);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean providesRecoursiveData() {
		return false;
	}
	
	
	private MMFG fv;
	private String lastCharacters = "";
	private String lastStartTag = "";
	private int x, y, width, height;
	private String term = "";
	private String file = "";
	private Node current;
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		lastStartTag = qName;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		lastCharacters = new String(ch, start, length);
		if (lastStartTag.equals("term")) term = lastCharacters;
		if (lastStartTag.equals("x")) x = Integer.parseInt(lastCharacters);
		if (lastStartTag.equals("y")) y = Integer.parseInt(lastCharacters);
		if (lastStartTag.equals("width")) width = Integer.parseInt(lastCharacters);
		if (lastStartTag.equals("height")) height = Integer.parseInt(lastCharacters);
		if (lastStartTag.equals("file")) file = lastCharacters;
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("object")) {
			Node n = new Node();
			n.setName(term);
			TechnicalAttribute ta = new TechnicalAttribute();
			ta.setRelative_x(x);
			ta.setRelative_y(y);
			ta.setWidth(width);
			ta.setHeight(height);
			n.addTechnicalAttribute(ta);
			current.addChildNode(n);
			allNodes.add(n);
		}
		else if (qName.equals("gmaf-data")) {
			Node n = new Node();
			n.setName(file);
			current.addChildNode(n);
			allNodes.add(n);
			fv.addNode(current);
			current = new Node();
		}
	}
}


/*
<?xml version="1.0" encoding="utf-8"?>
<gmaf-collection xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="gmaf_schema.xsd">

<gmaf-data>
<file>IMG_0001.png</file>
<date>14.04.2021</date>
<objects>

	<object>
		<term>cat</term>
		<bounding-box>
			<x>320</x>
			<y>121</y>
			<width>423</width>
			<height>522</height>
		</bounding-box>
		<probability>0.94</probability>
	</object>

</objects>
</gmaf-data>
</gmaf-collection>

*/