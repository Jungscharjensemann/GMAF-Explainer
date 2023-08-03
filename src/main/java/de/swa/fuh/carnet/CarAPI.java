package de.swa.fuh.carnet;

/**
 * Die Klasse CarAPI modelliert ein Plugin fuer den GMAF-Framework. Die angesprochene API ist auf die Erkennung von Autos
 * spezialisiert. Der von API zurueckgegebene JSON-String wird innerhalb der Plugin verarbeitet und die entsprechende Nodes zu
 * dem FeatureVektor hinzugefuegt. 
 * 
 * @see GMAF
 * 
 * @author Paulina Thiele
 */


import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.CompositionRelationship;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;

public class CarAPI implements GMAF_Plugin {

	// Enter a valid subscription key.
	/** Noetiges subscriptionKey fuer die Validation von API-Requests */
	static String subscriptionKey = "680c6de2-6a88-4c9b-8ca4-7072810afb7d";
	/** Das Attribut erlaubt verschiedene Erkennungs-Strategien zu setzen. Es gibt drei moegliche Optionen: All, Center, Largest.
	 * Default: All */
	private static Strategy strategy = Strategy.ALL;
	/** Das Attribut erlaubt verschiedene Erkennungs-Features zu setzen. Es gibt 4 moegliche Optionen: MM, MMG, Color, Angle.
	 * Default: MMG */
	private static Feature feature = Feature.MMG;
	/** URL unter dem die API angesprochen werden kann*/
	private String host = "https://api.carnet.ai/v2/mmg/detect?box_offset=0&box_min_width=180&box_min_height=180&box_min_ratio=1&box_max_ratio=3.15&box_select="
			+ strategy + "&features=" + feature + "&region=DEF";
	/** Knoten mit erkannten Objekten, die von dem GMAF genutzt werden*/
	private Vector<Node> detectedNodes = new Vector<Node>();
	/** erkannte Objekten */
	private Vector<String> detectedObjects = new Vector<String>();
	protected int width, height;
	
	/**
	 * Diese Methode gibt zurueck, ob das Plugin Dateien mit einer bestimmten Erweiterung verarbeiten kann.
	 * 
	 * @param ext Erwaiterung der Datei
	 * @return true, falls die Datei verarbeitet werden kann
	 */
	@Override
	public boolean canProcess(String ext) {
		if ((ext.equals(".jpg")) || (ext.equals(".png"))) {
		return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Getter-Methode, die vom GMAF aufgerufen wird, um die Ergebnisse des Plugins zu erhalten.
	 * 
	 * @return ein Vektor von MMFG-Knoten 
	 */
	@Override
	public Vector<Node> getDetectedNodes() {
		// TODO Auto-generated method stub
		return detectedNodes;
	}
	
	/**
	 * Gibt zurueck, ob die Plugin allgemeine Metadaten enthaelt.
	 * 
	 * @return true, falls die Plugin allgemeine Metadaten enthaelt.
	 */
	@Override
	public boolean isGeneralPlugin() {
		return false;
	}
	
	/**
	 * Diese Methode wird vom GMAF-Framework aufgerufen, wenn ein neues Asset verarbeitet werden soll.
	 * 
	 * @param url die temporaere URL des zu verarbeitenden Multimedia-Assets 
	 * @param file die temporaere Datei des zu verarbeitenden Assets 
	 * @param bytes enthaelt die Bytes der Datei
	 * @param fv aktuelle MMFG, in das die Ergebnisse dieses Plugins fusioniert werden sollen 
	 */
	@Override
	public void process(URL url, File file, byte[] bytes, MMFG fv) {
		try {
			//send the POST Request and create a new JSONObject with the returned String from the POST Request
			JSONObject jobject = new JSONObject(sendPostRequest(host, file));
			JSONArray detections = new JSONArray(jobject.getJSONArray("detections"));
			//if no object was detected the detection array is empty
			if (detections.isEmpty() == false) {
				// auslesen von detected objects
				for (int i = 0; i < detections.length(); i++) {
					// get the detected object from delivered JSON-response
					JSONObject getClass = new JSONObject(
							jobject.getJSONArray("detections").getJSONObject(i).get("class").toString());
					String objectClass = getClass.getString("name");
					System.out.println("objectClass: " + objectClass);
					detectedObjects.add(objectClass);
					Node currentNode = fv.getCurrentNode();
					Node n = new Node(objectClass, fv);
					currentNode.addChildNode(n);

					// check status code of detected object
					JSONObject status = new JSONObject(
							jobject.getJSONArray("detections").getJSONObject(i).get("status").toString());
					int statusCode = status.getInt("code");
					System.out.println("code: " + statusCode);

					// only the objects with status code = 0 have detailed attributes
					// get the detected features if the feature is set to MMG mode
					if ((statusCode == 0) && (feature.toString().equals("MMG"))) {

						// get the detected car-model name from delivered JSON-Response
						JSONArray array = new JSONArray(jobject.getJSONArray("detections").getJSONObject(i)
								.getJSONArray(feature.toString().toLowerCase()).toString());
						String model = array.getJSONObject(0).get("model_name").toString();
						String make_name = array.getJSONObject(0).get("make_name").toString();
						String generation_name = array.getJSONObject(0).get("generation_name").toString();
						
						//create nodes for detected features
						Node childNode = new Node(model, fv);
						Node childNode2 = new Node(make_name, fv);
						Node childNode3 = new Node(generation_name, fv);
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, childNode));
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, childNode2));
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, childNode3));
						detectedNodes.add(n);
					}

					// get the detected features if the feature is set to MM mode
					if ((statusCode == 0) && (feature.toString().equals("MM"))) {

						// get the detected car-model name from delivered JSON-Response
						JSONArray array = new JSONArray(jobject.getJSONArray("detections").getJSONObject(i)
								.getJSONArray(feature.toString().toLowerCase()).toString());
						String model = array.getJSONObject(0).get("model_name").toString();
						String make_name = array.getJSONObject(0).get("make_name").toString();

						Node childNode = new Node(model, fv);
						Node childNode2 = new Node(make_name, fv);

						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, childNode));
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, childNode2));

						detectedNodes.add(n);

					}

					// get the detected color if the feature is set to "color"
					if ((statusCode == 0) && (feature.toString().equals("COLOR"))) {
						JSONArray colorArray = new JSONArray(jobject.getJSONArray("detections").getJSONObject(i)
								.getJSONArray(feature.toString().toLowerCase()).toString());
						String color1 = colorArray.getJSONObject(0).get("name").toString();
						System.out.println(color1);
						TechnicalAttribute dominantColor = new TechnicalAttribute();
						dominantColor.addDominantColor(new Color(70));
						n.addTechnicalAttribute(dominantColor);
						Node colorNode = new Node(color1, fv);
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_DESCRIPTION, colorNode));
					}
					// get the detected angle if the feature is set to "angle"
					if ((statusCode == 0) && (feature.toString().equals("ANGLE"))) {
						// the first listed anlge always has the highest probability
						JSONArray angleArray = new JSONArray(jobject.getJSONArray("detections").getJSONObject(i)
								.getJSONArray(feature.toString().toLowerCase()).toString());
						String angle = angleArray.getJSONObject(0).get("name").toString();
						System.out.println(angle);
						Node angleNode = new Node(angle, fv);
						n.addCompositionRelationship(
								new CompositionRelationship(CompositionRelationship.RELATION_DESCRIPTION, angleNode));
					}
					
					// get the detected object box
					JSONObject getBox = new JSONObject(
							jobject.getJSONArray("detections").getJSONObject(i).get("box").toString());
					int boxH = (int) (getBox.getFloat("br_x") * 100);
					int boxB = (int) (getBox.getFloat("br_y") * 100);
					int tl_x = (int) (getBox.getFloat("tl_x") * 100);
					int tl_y = (int) (getBox.getFloat("tl_y") * 100);

					// if existing, set the bounding box of the detected object
					n.addTechnicalAttribute(new TechnicalAttribute(boxH, boxB, tl_x, tl_y, 1.0f, 0.0f));
				}

			}

		} catch (Exception x) {
			x.printStackTrace();
		}

	}
	
	/**
	 * Diese Methode schickt die POST-Request.
	 * 
	 * @param host URL Adresse des Servers
	 * @param file Datei, die das Objekt, das erkannt werden soll, enthaelt
	 * 
	 * @throws Exception
	 */
	private static String sendPostRequest(String host, File file) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost request = new HttpPost(host);

		request.addHeader("Accept", "application/json");
		request.addHeader("api-key", subscriptionKey);
		request.addHeader("Content-Type", "application/octet-stream");
		request.setEntity(new FileEntity(file));

		// Execute and get the response.
		HttpResponse response = httpclient.execute(request);
		
		int code = response.getStatusLine().getStatusCode();
		System.out.println("Response code: " + code);
		
		String jsonString = EntityUtils.toString(response.getEntity());
		return jsonString;
	}
	
	/**
	 * Diese Methode zeigt an, ob die Plugin rekursive Daten zurueckgibt, die dann innerhalb des GMAF-Frameworks erneut
	 * verarbeitet werden.
	 * 
	 * @return true, wenn die Plugin rekursive Daten zurueckgibt
	 */
	
	@Override
	public boolean providesRecoursiveData() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/**
	 * Setter-Methode, die das Erkennungsstrategie setzt
	 * @param strategy1 Erkennungsstrategie
	 */
	
	public static void setStrategy(Strategy strategy1) {
		strategy = strategy1;
	}
	
	/**
	 * Setter-Methode, die das Erkennungs-Feature setzt
	 * @param feature1 Erkennungs-Feature
	 */
	public static void setFeature(Feature feature1) {
		feature = feature1;
	}

}
