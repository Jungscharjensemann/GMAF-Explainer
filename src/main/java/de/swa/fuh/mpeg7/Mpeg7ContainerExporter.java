package de.swa.fuh.mpeg7;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Exporter Class to export Mpeg7Container either into MMFG or DMPEG7
 * @author michaelhermann
 *
 */
public class Mpeg7ContainerExporter {
	
	private String destinationPath;
	private List<String> filePaths = new ArrayList<String>();
	
	/**
	 * Get List of saved FilePaths
	 * @return List of Strings, containing paths to file
	 */
	public List<String> getFilePaths() {
		return filePaths;
	}
	

	/**
	 * Get Destination Path 
	 * @return Destinationpath
	 */
	public String getDestinationPath() {
		return destinationPath;
	}
	
	/**
	 * Set Pathdirection where files will be saved
	 * @param destinationPath
	 */
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	/**
	 * Exports Mpeg7 Container to MMFG XML File
	 * @param container
	 */
	public void export_to_mmfg(Mpeg7Container container) {
		
		Video video = (Video) container.getMultimediaContent();
		
		String videoId = video.getId();
		String mediaUri = video.getMediaLocator();
		String creationTitle = video.getCreationInformation().getTitle();
		String creationAbstract = video.getCreationInformation().getVideoInfo().getFreeTextAnnotation(); //TODO
		String creationCreator = video.getCreationInformation().getCreator();
		String creationCoordinatesName = video.getCreationInformation().getCreationPlace();
		String creationCoordinatesDate = video.getCreationInformation().getCreationTime();
		String assetMediaTimePoint =  video.getMediaTime().getMediaTimePoint();
		String assetMediaDuration =  video.getMediaTime().getMediaDuration();
		
		List<Scene> scenes = video.getScenes();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			Document mmfg_xml = dBuilder.newDocument();
			Element mmfg_root = mmfg_xml.createElement("de.swa.mmfg.MMFG");
			
			
			Element mmfg_nodes = mmfg_xml.createElement("nodes");
			Element mmfg_node_root = mmfg_xml.createElement("de.swa.mmfg.Node");
			mmfg_nodes.appendChild(mmfg_node_root);
			
			Element name_video_asset = mmfg_xml.createElement("name");
			name_video_asset.setTextContent(videoId);
			mmfg_node_root.appendChild(name_video_asset);
			
			Element childNodes_video_asset = mmfg_xml.createElement("childNodes");
			mmfg_node_root.appendChild(childNodes_video_asset);
			
			for (Scene scene : scenes) {
				Element mmfg_scene_node = mmfg_xml.createElement("de.swa.mmfg.Node"); 
				
				//TODO two types 1: firhter childnodes are keywords and the name is that. 2: name here is scene id and basta. 
				
				Element name_scene = mmfg_xml.createElement("name");
				name_scene.setTextContent(scene.getId());
				mmfg_scene_node.appendChild(name_scene);
				
				Element childNodes_scene = mmfg_xml.createElement("childNodes");
				mmfg_scene_node.appendChild(childNodes_scene);
				
				Element textAnnotation_scene = mmfg_xml.createElement("textAnnotation");
				textAnnotation_scene.setTextContent(scene.getAnnotation().getFreeTextAnnotation()); //TODO
				mmfg_scene_node.appendChild(textAnnotation_scene);
				
				Element technicalAttributes = mmfg_xml.createElement("technicalAttributes");
				mmfg_scene_node.appendChild(technicalAttributes);
				
				Element mmfg_TechnicalAttribute = mmfg_xml.createElement("de.swa.mmfg.TechnicalAttribute");
				technicalAttributes.appendChild(mmfg_TechnicalAttribute);
				
				Element MediaTimePoint = mmfg_xml.createElement("MediaTimePoint");
				MediaTimePoint.setTextContent(scene.getMediaTime().getMediaTimePoint());
				mmfg_TechnicalAttribute.appendChild(MediaTimePoint);
				
				Element MediaDuration = mmfg_xml.createElement("MediaDuration");
				MediaDuration.setTextContent(scene.getMediaTime().getMediaDuration());
				mmfg_TechnicalAttribute.appendChild(MediaDuration);
				
				
				
				childNodes_video_asset.appendChild(mmfg_scene_node);
				
			}
			
			
			Element technicalAttributes = mmfg_xml.createElement("technicalAttributes");
			mmfg_node_root.appendChild(technicalAttributes);
			
			Element mmfg_TechnicalAttribute = mmfg_xml.createElement("de.swa.mmfg.TechnicalAttribute");
			technicalAttributes.appendChild(mmfg_TechnicalAttribute);
			
			Element MediaTimePoint = mmfg_xml.createElement("MediaTimePoint");
			MediaTimePoint.setTextContent(assetMediaTimePoint);
			mmfg_TechnicalAttribute.appendChild(MediaTimePoint);
			
			Element MediaDuration = mmfg_xml.createElement("MediaDuration");
			MediaDuration.setTextContent(assetMediaDuration);
			mmfg_TechnicalAttribute.appendChild(MediaDuration);
			
			
			Element mmfg_generalmetadata = mmfg_xml.createElement("generalMetadata");
			Element title = mmfg_xml.createElement("title");
			title.setTextContent(creationTitle); 
			mmfg_generalmetadata.appendChild(title);
			Element videoInfo = mmfg_xml.createElement("videoInfo");
			videoInfo.setTextContent(creationAbstract); 
			mmfg_generalmetadata.appendChild(videoInfo);
			Element creator = mmfg_xml.createElement("creator");
			creator.setTextContent(creationCreator);
			mmfg_generalmetadata.appendChild(creator);
			Element creationTime = mmfg_xml.createElement("creationTime");
			creationTime.setTextContent(creationCoordinatesDate); 
			mmfg_generalmetadata.appendChild(creationTime);
			Element creationPlace = mmfg_xml.createElement("creationPlace");
			creationPlace.setTextContent(creationCoordinatesName); 
			mmfg_generalmetadata.appendChild(creationPlace);
			
			
			
			Element mmfg_locations = mmfg_xml.createElement("locations");
			Element mmfg_location = mmfg_xml.createElement("de.swa.mmfg.Location");
			mmfg_locations.appendChild(mmfg_location);
			Element type = mmfg_xml.createElement("type");
			type.setTextContent("1"); //default
			mmfg_location.appendChild(type);
			
			Element location = mmfg_xml.createElement("location");
			location.setTextContent(mediaUri);
			mmfg_location.appendChild(location);
			
			
			Element mmfg_currentNode = mmfg_xml.createElement("currentNode");
			Element mmfg_allNodes = mmfg_xml.createElement("allNodes");
			Element mmfg_collectionElements = mmfg_xml.createElement("collectionElements");
			
			
			mmfg_root.appendChild(mmfg_nodes);
			mmfg_root.appendChild(mmfg_generalmetadata);
			mmfg_root.appendChild(mmfg_locations);
			mmfg_root.appendChild(mmfg_currentNode);
			mmfg_root.appendChild(mmfg_allNodes);
			mmfg_root.appendChild(mmfg_collectionElements);
			
			mmfg_xml.appendChild(mmfg_root);

			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(mmfg_xml);
			
			String path_to_file = this.destinationPath + videoId  + ".xml";
			this.filePaths.add(path_to_file);
			StreamResult result =  new StreamResult(new File(path_to_file));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			System.out.println("Parser Error");
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			System.out.println("TransformerConfig Error");
			e.printStackTrace();
		} catch (TransformerException e) {
			System.out.println("Transformer Error");
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Exports Mpeg7 Container to Default MPEG7 XML File
	 * @param container Filled Mpeg7Container
	 */
	public void export_to_dmpeg7(Mpeg7Container container) {
		
		Video video = (Video) container.getMultimediaContent();
		
		String videoId = video.getId();
		String mediaUri = video.getMediaLocator();
		String creationTitle = video.getCreationInformation().getTitle();
		String creationAbstract = video.getCreationInformation().getVideoInfo().getFreeTextAnnotation(); //TODO
		String creationCreator = video.getCreationInformation().getCreator();
		String creationCoordinatesName = video.getCreationInformation().getCreationPlace();
		String creationCoordinatesDate = video.getCreationInformation().getCreationTime();
		String assetMediaTimePoint =  video.getMediaTime().getMediaTimePoint();
		String assetMediaDuration =  video.getMediaTime().getMediaDuration();
		
		
		List<Scene> scenes = video.getScenes();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document dmpeg7_xml = dBuilder.newDocument();
			dbFactory.setNamespaceAware(true); 
			
			//Mpeg7 root
			Element Mpeg7_tag = dmpeg7_xml.createElement("Mpeg7");
			Mpeg7_tag.setAttribute("xmlns:mpeg7", "urn:mpeg7:schema:2004");
			Mpeg7_tag.setAttribute("xmlns", "urn:mpeg:mpeg7:schema:2004");
			Mpeg7_tag.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			dmpeg7_xml.appendChild(Mpeg7_tag);
			
			//Description
			Element Description_tag = dmpeg7_xml.createElement("Description");
			Description_tag.setAttribute("xsi:type", "ContentEntityType");
			Mpeg7_tag.appendChild(Description_tag);
			
			Element Description_Metadata_tag = dmpeg7_xml.createElement("DescriptionMetadata");
			Description_tag.appendChild(Description_Metadata_tag);
			
			Element MultimediaContent_tag = dmpeg7_xml.createElement("MultimediaContent");
			MultimediaContent_tag.setAttribute("xsi:type", "VideoType");
			Description_tag.appendChild(MultimediaContent_tag);
			
			
			Element Video_tag = dmpeg7_xml.createElement("Video");
			Video_tag.setAttribute("id", videoId);
			MultimediaContent_tag.appendChild(Video_tag);
			
			Element MediaLocator_tag = dmpeg7_xml.createElement("MediaLocator");
			Video_tag.appendChild(MediaLocator_tag);
			Element MediaUri_tag = dmpeg7_xml.createElement("MediaUri");
			MediaUri_tag.setTextContent(mediaUri);
			MediaLocator_tag.appendChild(MediaUri_tag);
			
			Element CreationInformation_tag = dmpeg7_xml.createElement("CreationInformation");
			Video_tag.appendChild(CreationInformation_tag);
			
			Element Creation_tag = dmpeg7_xml.createElement("Creation");
			CreationInformation_tag.appendChild(Creation_tag);
			
			Element Title_tag = dmpeg7_xml.createElement("Title");
			Title_tag.setAttribute("type", "main");
			Title_tag.setTextContent(creationTitle);
			Creation_tag.appendChild(Title_tag);
			
			Element Abstract_tag = dmpeg7_xml.createElement("Abstract");
			Creation_tag.appendChild(Abstract_tag);
			Element Abstract_FreetextAnnotation_tag = dmpeg7_xml.createElement("FreeTextAnnotation");
			Abstract_FreetextAnnotation_tag.setTextContent(creationAbstract);
			Abstract_tag.appendChild(Abstract_FreetextAnnotation_tag);
			
			Element Creator_tag = dmpeg7_xml.createElement("Creator");
			Creation_tag.appendChild(Creator_tag);
			Element Role_tag = dmpeg7_xml.createElement("Role");
			Role_tag.setAttribute("href", "urn:mpeg:mpeg7:cs:RoleCS:2001:UNKNOWN");
			Creator_tag.appendChild(Role_tag);
			Element Role_Name_tag = dmpeg7_xml.createElement("Name");
			Role_Name_tag.setAttribute("xml:lang", "EN");
			Role_Name_tag.setTextContent(creationCreator);
			
			Element Agent_tag = dmpeg7_xml.createElement("Agent");
			Agent_tag.setAttribute("xsi:type", "PersonType");
			Creator_tag.appendChild(Agent_tag);
			
			Element Agent_Name_tag = dmpeg7_xml.createElement("Name");
			Agent_tag.appendChild(Agent_Name_tag);
			Element Agent_GivenName_tag = dmpeg7_xml.createElement("GivenName");
			Agent_GivenName_tag.setTextContent(creationCreator);
			Agent_Name_tag.appendChild(Agent_GivenName_tag);
			
			
			Element CreationCoordinates_tag = dmpeg7_xml.createElement("CreationCoordinates");
			Creation_tag.appendChild(CreationCoordinates_tag);
			Element Location_tag = dmpeg7_xml.createElement("Location");
			CreationCoordinates_tag.appendChild(Location_tag);
			
			Element Location_Name_tag = dmpeg7_xml.createElement("Name");
			Location_Name_tag.setTextContent(creationCoordinatesName);
			Location_tag.appendChild(Location_Name_tag);
			
			Element Location_Date_tag = dmpeg7_xml.createElement("Date");
			Location_Date_tag.setTextContent(creationCoordinatesDate);
			CreationCoordinates_tag.appendChild(Location_Date_tag);
			
			Element Location_Timepoint_tag = dmpeg7_xml.createElement("TimePoint");
			Location_Date_tag.setTextContent(creationCoordinatesDate);
			Location_Date_tag.appendChild(Location_Timepoint_tag);
			

			
			Element MediaTime_tag = dmpeg7_xml.createElement("MediaTime");
			Video_tag.appendChild(MediaTime_tag);
			
			Element MediaTimePoint_tag = dmpeg7_xml.createElement("MediaTimePoint");
			MediaTimePoint_tag.setTextContent(assetMediaTimePoint);
			MediaTime_tag.appendChild(MediaTimePoint_tag);
			
			Element MediaDuration_tag = dmpeg7_xml.createElement("MediaDuration");
			MediaDuration_tag.setTextContent(assetMediaDuration);
			MediaTime_tag.appendChild(MediaDuration_tag);
			
			
			
			Element TemporalDecomposition_tag = dmpeg7_xml.createElement("TemporalDecomposition");
			Video_tag.appendChild(TemporalDecomposition_tag);

			for (Scene scene : scenes) {
				Element VideoSegment_tag = dmpeg7_xml.createElement("VideoSegment");
				VideoSegment_tag.setAttribute("id",  scene.getId());
				TemporalDecomposition_tag.appendChild(VideoSegment_tag);
				
				Element Scene_TextAnnotation_tag = dmpeg7_xml.createElement("TextAnnotation");
				VideoSegment_tag.appendChild(Scene_TextAnnotation_tag);
				
				Element Scene_FreeTextAnnotation_tag = dmpeg7_xml.createElement("FreeTextAnnotation");
				Scene_FreeTextAnnotation_tag.setTextContent(scene.getAnnotation().getFreeTextAnnotation());
				Scene_TextAnnotation_tag.appendChild(Scene_FreeTextAnnotation_tag);
				
				Element Scene_MediaTime_tag = dmpeg7_xml.createElement("MediaTime");
				VideoSegment_tag.appendChild(Scene_MediaTime_tag);
				
				Element Scene_MediaTimePoint_tag = dmpeg7_xml.createElement("MediaTimePoint");
				Scene_MediaTimePoint_tag.setTextContent(scene.getMediaTime().getMediaTimePoint());
				Scene_MediaTime_tag.appendChild(Scene_MediaTimePoint_tag);
				Element Scene_MediaDuration_tag = dmpeg7_xml.createElement("MediaDuration");
				Scene_MediaDuration_tag.setTextContent(scene.getMediaTime().getMediaDuration());
				Scene_MediaTime_tag.appendChild(Scene_MediaDuration_tag);
				
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(dmpeg7_xml);
			
			String path_to_file = this.destinationPath + videoId  + ".xml";
			this.filePaths.add(path_to_file);
			StreamResult result =  new StreamResult(new File(path_to_file));
			transformer.transform(source, result);
			
			
			XMLDocumentBuilder doc_builder = new XMLDocumentBuilder();
			Document xml_t = doc_builder.build_document_from_xml(path_to_file);	
			
			Mpeg7XMLValidator XMLValidator = new Mpeg7XMLValidator();
			if (!XMLValidator.validate_MPEG7_XML_Schema(xml_t)) {
				System.out.println("Stop, because not a valid XML file was given");
			} else {
				System.out.println("The produced file is a valid MPEG7 file.");
			}
			
		} catch (ParserConfigurationException e) {
			System.out.println("Parser Error");
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			System.out.println("TransformerConfig Error");
			e.printStackTrace();
		} catch (TransformerException e) {
			System.out.println("Transformer Error");
			e.printStackTrace();
		}
		
	}

}
