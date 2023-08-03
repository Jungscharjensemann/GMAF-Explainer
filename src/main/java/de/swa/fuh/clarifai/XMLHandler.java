package de.swa.fuh.clarifai;

import org.w3c.dom.*;

import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Region;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.Dimension;
import java.io.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 
 * Class that takes ClarifAI API responses and builds XML output in the defined GMAF format.
 * 
 * @author Julius K�ndiger
 *
 */
public class XMLHandler {
	private DocumentBuilderFactory dFactory;
	private DocumentBuilder builder;
	private Document doc;
	
	public XMLHandler() {
		try {
			dFactory = DocumentBuilderFactory.newInstance();
			builder = dFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Parses the API output, writes and saves an XML file containing the file name, found objects,
	 * bounding box parameters and probabilities.
	 * 
	 * ClarifAI returns bounding boxes as normalized values ranging from 0 to 1. This method takes the
	 * image's dimensions and applies them to this output to determine pixel based bounding boxes for
	 * the XML output.
	 * 
	 * @param file The XML file that will be written through this method
	 * @param results Bounding boxes resulting from the API call
	 * @param dimension Image dimensions needed for calculating bounding boxes
	 */
	public boolean write(File file,List<Region> results,Dimension dimension) {
		doc = builder.newDocument();
		
		//Header scheme as determined by Stefan Wagenpfeil
		Element header = doc.createElement("xs:schema");
		header.setAttribute("targetNamespace","http://www.fernuni-hagen.de/gmaf");
		header.setAttribute("elementFormDefault","qualified");
		header.setAttribute("xmlns","gmaf_schema.xsd");
		header.setAttribute("xmlns:xs","http://www.w3.org/2001/XMLSchema");
		doc.appendChild(header);
		
		Element root = doc.createElement("xs:gmaf-data");
		header.appendChild(root);
		
		Element fileName = doc.createElement("xs:file");
		fileName.appendChild(doc.createTextNode(file.getName().substring(0,file.getName().length() - 4)));
		root.appendChild(fileName);
		
		Element date = doc.createElement("xs:date");
		LocalDate today = LocalDate.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		date.appendChild(doc.createTextNode(today.format(dateFormat)));
		root.appendChild(date);
		
		Element objects = doc.createElement("xs:objects");
		root.appendChild(objects);
		
		for (Region r : results) {
			if(r.hasData()) {
				for(Concept c : r.getData().getConceptsList()) {
					Element object = doc.createElement("xs:object");
					
					Element term = doc.createElement("xs:term");
					term.appendChild(doc.createTextNode(c.getName()));
					object.appendChild(term);
					
					Element boundingBox = doc.createElement("xs:bounding-box");
					object.appendChild(boundingBox);
					
					//X = Left most value of API result * image width
					Element x = doc.createElement("xs:x");
					int xValue = (int) (r.getRegionInfo().getBoundingBox().getLeftCol() * dimension.getWidth());
					x.appendChild(doc.createTextNode(String.valueOf(xValue)));
					
					//Y = Top most value of API result * image height
					Element y = doc.createElement("xs:y");
					int yValue = (int) (r.getRegionInfo().getBoundingBox().getTopRow() * dimension.getHeight());
					y.appendChild(doc.createTextNode(String.valueOf(yValue)));
					
					//Width = (Right most value of API result - left most value of API result) * image width
					Element width = doc.createElement("xs:width");
					int widthValue = (int) ((r.getRegionInfo().getBoundingBox().getRightCol() * dimension.getWidth()) 
							- (r.getRegionInfo().getBoundingBox().getLeftCol() * dimension.getWidth()));
					width.appendChild(doc.createTextNode(String.valueOf(widthValue)));
					
					//Height = (Bottom most value of API result - top most value of API result) * image height
					Element height = doc.createElement("xs:height");
					int heightValue = (int) ((r.getRegionInfo().getBoundingBox().getBottomRow() * dimension.getHeight()) 
							- (r.getRegionInfo().getBoundingBox().getTopRow() * dimension.getHeight()));
					height.appendChild(doc.createTextNode(String.valueOf(heightValue)));
					
					boundingBox.appendChild(x);
					boundingBox.appendChild(y);
					boundingBox.appendChild(width);
					boundingBox.appendChild(height);
					
					Element propability = doc.createElement("xs:probability");
					propability.appendChild(doc.createTextNode(String.valueOf(c.getValue())));
					object.appendChild(propability);
					
					objects.appendChild(object);
				}
			}
		}
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
	        
	        DOMSource domSource = new DOMSource(doc);
	        StreamResult streamResult = new StreamResult(file);
	        
	        transformer.transform(domSource, streamResult);
	        System.out.println("Datei erfolgreich angelegt: "+file.getPath());
	        return true;
		} catch (TransformerException e) {
            e.printStackTrace();
        }
		
		return false;
	}

}
