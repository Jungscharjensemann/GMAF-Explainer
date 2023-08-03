package de.swa.fuh.mpeg7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * 
 * @author michaelhermann
 * Class that imports MPEG7 XML Files to a MMFG File(s) and exports a MMFG FIle to a Default-MPEG7 File
 */
public class Mpeg7Converter {
	
	private XMLDocumentBuilder doc_builder;
	
	private String pathToMapExport;
	private String pathToMapImport;
	
	/**
	 * Init with default map files
	 */
	public Mpeg7Converter (){ 
		this.pathToMapExport = "schema/Map_MMFG.csv";
		this.pathToMapImport = "schema/Map_DMPEG7.csv";
	}
	
	/**
	 * 
	 * @param pathToMapExport Path to Map for Export from MMFG to DMPEG7.
	 * @param pathToMapImport Path to Map for Import from MPEG7 to MMFG.
	 */
	public Mpeg7Converter (String pathToMapExport, String pathToMapImport){
		this.pathToMapExport = pathToMapExport;
		this.pathToMapImport = pathToMapImport;
	}
	
	/**
	 * Set map for export 
	 * @param pathToMapExport Path to Map for Export from MMFG to DMPEG7.
	 */
	public void set_pathToMapExport(String pathToMapExport) {
		this.pathToMapExport = pathToMapExport;
	}
	
	/**
	 * Get current map for export
	 * @return Path to Export Map from MMFG to DMPEG7
	 */
	public String get_pathToMapExport() {
		return pathToMapExport;
	}
	
	/**
	 * Set map for import
	 * @param pathToMapImport Path to Map for Import from MPEG7 to MMFG.
	 */
	public void set_pathToMapImport(String pathToMapImport) {
		this.pathToMapImport = pathToMapImport;
	}
	
	/**
	 * Get current map for import
	 * @return Path to Import Map from MPEG7 to MMFG
	 */
	public String get_pathToMapImport() {
		return pathToMapImport;
	}
	
	/**
	 * Export MMFG to DMPEG7 XML File
	 * @param pathToMMFG Path to MMFG
	 * @param pathToDestination Path where output (DMPEG7) will be saved
	 * @return List of String of Paths for created file
	 */
	public List<String> export_mmfg(String pathToMMFG, String pathToDestination) {
		
		//Build Document
		Document xml_doc = this.build_document(pathToMMFG);
		
		//Init xpath
		XPath xpath = this.init_xpath(xml_doc);
		
		// Load mapping -> here separate between asset and scenes; split due to compatability -> less code to maintain
		Map<String, String> MMFG_to_DMPEG7_map_asset = this.build_key_value_map(this.pathToMapExport, 0, -5);
		Map<String, String> MMFG_to_DMPEG7_map_scene = this.build_key_value_map(this.pathToMapExport, 9, 0);
		
		//build map with info from xpaths
		Map<String, List<String>> MMFG_to_DMPEG7_asset_data_map = this.build_key_data_map(MMFG_to_DMPEG7_map_asset, xpath, xml_doc);
		Map<String, List<String>> MMFG_to_DMPEG7_scene_data_map = this.build_key_data_map(MMFG_to_DMPEG7_map_scene, xpath, xml_doc);
			
		//init export container and set destinationpath
		Mpeg7Container mpeg7Container = this.build_container(MMFG_to_DMPEG7_asset_data_map, 0, MMFG_to_DMPEG7_scene_data_map);

		//Default values for descriptionMetadata
		DescriptionMetadata descriptionMetadata = new DescriptionMetadata(); // is initialized with default values. see class DescriptionMetadata
		mpeg7Container.setDescriptionMetadata(descriptionMetadata);
		
		//init export container and set destinationpath
		Mpeg7ContainerExporter mpeg7ContainerExporter = new Mpeg7ContainerExporter();
		mpeg7ContainerExporter.setDestinationPath(pathToDestination);
		//export container
		mpeg7ContainerExporter.export_to_dmpeg7(mpeg7Container);


		return mpeg7ContainerExporter.getFilePaths();	
	}
	
	/**
	 * Import MPEG7 to MMFG XML FIle
	 * @param pathToMpeg7 Path to MPEG7 File
	 * @param pathToDestination Path where output (MMFG) will be saved
	 * @return List of Strings of Paths for created files
	 */
	public List<String> import_mpeg7(String pathToMpeg7, String pathToDestination) {
		
		//read xml file
		Document xml_doc = this.build_document(pathToMpeg7);

		
		//Check if it is valid mpeg7-xmlFile
		if (!this.is_valid_mpeg7(xml_doc)) {
			System.out.println("Stop, because not a valid MPEG7-XML file was given");
			return null;
		}
		
		//Reset Namespace awareness to build correct MPEG7 Namespaces
		xml_doc = this.doc_builder.setNameSpaceAwareness(false);
		
		//Init xpath
		XPath xpath = this.init_xpath(xml_doc);
		
		// Load mapping -> here separate between asset and scenes
		Map<String, String> MPEG7_to_DMPEG7_keys_mapping_asset = this.build_key_value_map(this.pathToMapImport, 0, -5);
		Map<String, String> MPEG7_to_DMPEG7_keys_mapping_scenes = this.build_key_value_map(this.pathToMapImport, 9, 0);
		

		//build map with info from xpaths
		Map<String, List<String>> DMPEG7_key_data_map = this.build_key_data_map(MPEG7_to_DMPEG7_keys_mapping_asset, xpath, xml_doc);
		
		//for video asset in xml doc -> get associated scenes
		int amount_of_assets = DMPEG7_key_data_map.get("VideoId").size();
		Map<Integer, Map<String, List<String>>> scenes_map = this.build_scene_per_asset_map(amount_of_assets, MPEG7_to_DMPEG7_keys_mapping_scenes, xpath, xml_doc);
		

		//init export container and set destinationpath
		Mpeg7ContainerExporter mpeg7ContainerExporter = new Mpeg7ContainerExporter();
		mpeg7ContainerExporter.setDestinationPath(pathToDestination);
		
		//export each asset by building container and then exporting
		for (int asset_index = 0; asset_index < amount_of_assets; asset_index++) {
			
			Mpeg7Container mpeg7Container = this.build_container(DMPEG7_key_data_map, asset_index, scenes_map.get(asset_index+1));
			mpeg7ContainerExporter.export_to_mmfg(mpeg7Container);
		}
		
		//returns list of paths to files in destination path
		return mpeg7ContainerExporter.getFilePaths();
		
	}
	
	/**
	 * Builds a Map of Scenes for the corresponding Asset(s).
	 * @param amount_of_assets Gives the amount of 
	 * @param key_data_map Map which maps key to data of document per asset
	 * @param xpath Set up XPath 
	 * @param xml_doc Document
	 * @return Map where the key corresponds to the asset and the value is a another map of data for the scenes of said asset
	 */
	private Map<Integer, Map<String, List<String>>> build_scene_per_asset_map(int amount_of_assets, Map<String, String> key_data_map, XPath xpath, Document xml_doc) {
		
		Map<Integer, Map<String, List<String>>> scenes_map = new HashMap<>();
		for (int i = 1; i <= amount_of_assets; i++) {
		
			Map<String, List<String>> scene_map = new HashMap<>();
			for (String key : key_data_map.keySet()) {
			
			NodeList node_list_of_scenes_per_asset = null;
			List<String> info_from_node_list_of_scenes_per_asset = new ArrayList<>();
			
			String xpath_string = key_data_map.get(key);
			if (!xpath_string.equals("None")) {
				String xpath_string_update = xpath_string.replace("[i]", "[position()=" + i + "]");
				try {
					XPathExpression expr = xpath.compile(xpath_string_update);
					node_list_of_scenes_per_asset = (NodeList) expr.evaluate(xml_doc, XPathConstants.NODESET);
					
					info_from_node_list_of_scenes_per_asset = extract_info_from_nodeList(node_list_of_scenes_per_asset);
					scene_map.put(key, info_from_node_list_of_scenes_per_asset);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					System.out.println("Error while building the Map for Scenes of DMPEG7");
				}
				}
			}	
		scenes_map.put(i, scene_map);
		}
		return scenes_map;
	}
	

	/**
	 * Reads path and returns a Document
	 * @param pathToXMLFile path to the XML File
	 * @return Document of XML File
	 */
	private Document build_document (String pathToXMLFile) {
		this.doc_builder = new XMLDocumentBuilder();
		return doc_builder.build_document_from_xml(pathToXMLFile);	
	}
	
	/**
	 * Checks whether the Document is a valid mpeg7 XML File
	 * @param xml_doc Document which is checkes
	 * @return boolean, true if valid, false if not valid
	 */
	private boolean is_valid_mpeg7(Document xml_doc) {
		Mpeg7XMLValidator XMLValidator = new Mpeg7XMLValidator();
		if(XMLValidator.validate_MPEG7_XML_Schema(xml_doc)) {
			return true;
		} else {
			System.out.println("Length of key and values does not match");
			return false;
		}
	}
	
	/**
	 * HelperFunction that inits the XPath Factory etc.
	 * @param xml_doc xml doc
	 * @return Set up XPath XPath
	 */
	private XPath init_xpath(Document xml_doc) {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
//		xpath.setNamespaceContext(new NamespaceResolver(xml_doc));
		return xpath;
	}
	
	/**
	 * Builds a key value map of csv file. Possible to specify where to start. Function already cuts of heading of column
	 * @param pathToMap Path where map is saved
	 * @param start start reading from which row
	 * @param end reads till row 
	 * @return Map of key value, where key is the destination and value is the corresponding XPath
	 */
	private Map<String, String> build_key_value_map(String pathToMap, int start, int end) {
		// Read mapping
		Map_Reader map_reader = new Map_Reader(pathToMap);
		// Get keys and values (xpaths) of mapping etc.
		List<String> key_list = map_reader.get_keys();		
		List<String> xpath_list = map_reader.getXPath_strings();
		
		//Check if loading was successful
		if (!this.same_length_checker(key_list, xpath_list)) {
			return null;
		}

		//Build map with key, value pair
		if (end == 0) {
			end = xpath_list.size();
		} else if (end < 0){
			end = xpath_list.size() + end;
		}
		
		Map<String, String> key_value_map = new HashMap<>();
		for (int x = start; x < end; x++) {
			key_value_map.put(key_list.get(x), xpath_list.get(x));
		}
		return key_value_map;
	}
	
	/**
	 * Checks if two lists have the same length
	 * @param list_1 First List
	 * @param list_2 Second List
	 * @return true, if the two lists have the same length
	 */
	private boolean same_length_checker(List<String> list_1, List<String> list_2) {
		if (list_1.size() == list_2.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Builds a Map with key and corresponding Lists of extracted data from a Document
	 * @param key_value_map Map where the key points to the corresponding XPath
	 * @param xpath XPath
	 * @param xml_doc Document
	 * @return Map of Keys to Lists of Data 
	 */
	private Map<String, List<String>> build_key_data_map (Map<String, String> key_value_map, XPath xpath, Document xml_doc){
		Map<String, List<String>> key_data_map = new HashMap<>();
		for (String key : key_value_map.keySet()) {
			List<String> info_from_nodeList = new ArrayList<>();
			String xpath_string = key_value_map.get(key);
			if(!xpath_string.equals("None")) {
				try {
					XPathExpression expr = xpath.compile(xpath_string);
					NodeList node_list = (NodeList) expr.evaluate(xml_doc, XPathConstants.NODESET);
					info_from_nodeList = extract_info_from_nodeList(node_list);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					System.out.println("Error while building the map with data");
				}	
			}
			key_data_map.put(key, info_from_nodeList);		
		}
		return key_data_map;
	}
	
	/**
	 * Build a List of Scenes 
	 * @param scene_map A Map with key to data list, specified for scenes
	 * @return List of Scenes
	 */
	private List<Scene> build_scenes_list(Map<String, List<String>> scene_map){
		List<Scene> scenes = new ArrayList<Scene>();
		for (int scene_index = 0; scene_index < scene_map.get("SceneId").size(); scene_index++) {
			scenes.add(build_scene(scene_map, scene_index));
		}
		return scenes;
	}
	
	/**
	 * Builds a Scene from a Map of multiple Scenes
	 * @param scene_map Scene map 
	 * @param scene_index Scene index
	 * @return Scene Scene
	 */
	private Scene build_scene(Map<String, List<String>> scene_map, int scene_index) {
		String scene_id = get_value_from_map(scene_map, "SceneId", scene_index);
		String scene_mediaTimePoint = get_value_from_map(scene_map, "SceneMediaTimePoint", scene_index);
		String scene_mediaTimeDuration = get_value_from_map(scene_map, "SceneMediaDuration", scene_index);
		String scene_freeTextAnnotation = get_value_from_map(scene_map, "TextAnnotation", scene_index);
		
//		String scene_keywordAnnotation = get_value_from_map(scene_map, "KeywordAnnotation", scene_index);
		
		MediaTime scene_mediaTime = new MediaTime(scene_mediaTimePoint, scene_mediaTimeDuration);
		Annotation scene_annotation = new Annotation(scene_freeTextAnnotation); //TODO add unterschied keyword vs. freetext
		Scene scene = new Scene(scene_id, scene_mediaTime, scene_annotation);
		
		return scene;
	}
	
	/**
	 * Builds a Mpeg7 Container 
	 * @param data_asset_map Map 
	 * @param asset_index Declara which asset to build into a Container. Set to 0 if it is the only asset. 
	 * @param scene_map All relevant scenes of the asset
	 * @return Mpeg7 Container
	 */
	private Mpeg7Container build_container(Map<String, List<String>> data_asset_map, int asset_index, Map<String, List<String>> scene_map) {
		
		String videoId = get_value_from_map(data_asset_map, "VideoId", asset_index);	
		
		String mediaUri = get_value_from_map(data_asset_map, "MediaUri", asset_index);
		
		String creationAbstract = get_value_from_map(data_asset_map, "CreationAbstract", asset_index);
		Annotation creationAbstract_Annotation = new Annotation(creationAbstract);
		
		String creationTitle = get_value_from_map(data_asset_map, "CreationTitle", asset_index);		
		String creationCreator = get_value_from_map(data_asset_map, "CreationCreator", asset_index);
		String creationCoordinatesName = get_value_from_map(data_asset_map, "CreationCoordinatesName", asset_index);
		String creationCoordinatesDate = get_value_from_map(data_asset_map, "CreationCoordinatesDate", asset_index);
		CreationInformation creationInformation = new CreationInformation(creationTitle, creationAbstract_Annotation, creationCreator, creationCoordinatesDate, creationCoordinatesName);
		
		String assetMediaTimePoint = get_value_from_map(data_asset_map, "AssetMediaTimePoint", asset_index);
		String assetMediaDuration = get_value_from_map(data_asset_map, "AssetMediaDuration", asset_index);
		MediaTime mediaTime_asset = new MediaTime(assetMediaTimePoint, assetMediaDuration);
		
		
		List<Scene> scenes = this.build_scenes_list(scene_map);
		
		//Combine to MediaContent (Video)
		Video mediacontent_as_video = new Video(mediaUri, creationInformation, videoId, mediaTime_asset, scenes);
		//Set Container - empty DescriptionMetadata
		Mpeg7Container mpeg7Container = new Mpeg7Container(null, mediacontent_as_video);
		
		return mpeg7Container;
		
		
	}
	
	/**
	 * Gets value from a Map. 
	 * @param map map
	 * @param key Key 
	 * @param index Index
	 * @return Value from map. If map has no value returns an empty String
	 */
	private String get_value_from_map(Map<String, List<String>> map, String key, int index) {
		if  (map.get(key) != null) {
			if (map.get(key).size() > 0){
				return map.get(key).get(index);
			}
		}
		return "";		
	}
	
	/**
	 * Extracts data from NodeList 
	 * @param nodeList Nodelist
	 * @return List of Strings containing the information from the nodes
	 */
	private List<String> extract_info_from_nodeList(NodeList nodeList){
		List<String> info_list = new ArrayList<>();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			String info = nodeList.item(i).getTextContent();
			
			info_list.add(info);
		}
		return info_list;
	}
	

}
