package de.swa.fuh.mpeg7;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * HelperClass to build Documents from Paths
 * @author michaelhermann
 *
 */
public class XMLDocumentBuilder {
	
	private DocumentBuilderFactory factory;
	private String xmlPath;
	
	XMLDocumentBuilder(){
		this.factory = DocumentBuilderFactory.newInstance();
		this.factory.setNamespaceAware(true);
	}
	
	/**
	 * Builds the Document from path Input
	 * @param xmlPath String which represents path to XML-File
	 * @return Document
	 */
	public Document build_document_from_xml(String xmlPath) {
		this.xmlPath = xmlPath;
		DocumentBuilder builder = null;
		try {
			builder = this.factory.newDocumentBuilder();
			Document doc = builder.parse(xmlPath); 
			return doc;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Set the namespace awareness
	 * @param namespace_awareness boolean
	 * @return Document with adjusted awareness
	 */
	public Document setNameSpaceAwareness(boolean namespace_awareness) {
		this.factory.setNamespaceAware(namespace_awareness);
		return this.build_document_from_xml(this.xmlPath);
	}
	
	

}
