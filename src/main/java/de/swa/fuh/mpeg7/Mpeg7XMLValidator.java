package de.swa.fuh.mpeg7;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.*;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;



/**
 * Class to help check if Mpeg7 file is following the specified schema
 * @author michaelhermann
 *
 */
public class Mpeg7XMLValidator {
	
	private List<String> available_mpeg7_iso_versions = Arrays.asList("1", "2", "3", "4");
	
	/**
	 * Get which ISO Versions are currently listed by the ISO
	 * @return List
	 */
	public List<String> getAvailable_mpeg7_iso_versions() {
		return available_mpeg7_iso_versions;
	}

	/**
	 * Validate if Document is following the correct MPEG7 Schema, based on declared version
	 * @param xml_doc
	 * @return true, if it is a valid MPEG7 XML File
	 */
	public boolean validate_MPEG7_XML_Schema(Document xml_doc){
		System.setProperty("jdk.xml.maxOccurLimit", "1000000"); // due to max recur error
		
		//get Schema 
		String mpeg7_schema_version = get_mpeg7_schema_url(xml_doc);
		URL mpeg7_schema_url = build_mpeg7_schema_url(mpeg7_schema_version);
//		System.out.println("Used mpeg7 schema: " + mpeg7_schema_url.toString());
		
		//set Schema
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setValidating(true);
		fac.setNamespaceAware(true);
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		
		try {
			Schema schema = schemaFactory.newSchema(mpeg7_schema_url);
			fac.setSchema(schema);
			
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(xml_doc));
	        
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Build MPEG7 URL 
	 * @param mpeg7_namespace_version
	 * @return URL, depending on the version
	 */
	private URL build_mpeg7_schema_url(String mpeg7_namespace_version) {
		try {
			return new URL("https://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-7_schema_files/mpeg7-v" + mpeg7_namespace_version + ".xsd");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the declared MPEG7 Schema URL 
	 * @param xml_doc
	 * @return
	 */
	private String get_mpeg7_schema_url(Document xml_doc) {
		Element root = xml_doc.getDocumentElement();
		String mpeg7_namespace = root.getNamespaceURI();
		String mpeg7_namespace_version = mpeg7_namespace.substring(mpeg7_namespace.length()-1);
		

		if (!available_mpeg7_iso_versions.contains(mpeg7_namespace_version)) {
			System.out.println("Did not Match the correct MPEG-7 Schema file on ISO");
			return "";
		}
		
		return mpeg7_namespace_version;
	}
	
	

}
