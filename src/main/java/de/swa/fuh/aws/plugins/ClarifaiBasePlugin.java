package de.swa.fuh.aws.plugins;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.clarifai.grpc.api.MultiOutputResponse;
import com.clarifai.grpc.api.Region;

import de.swa.fuh.aws.clarifai.ClarifaiImageLabeler;
import de.swa.fuh.aws.clarifai.ClarifaiResponseHandler;
import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

/**
 * GMAF Plugin for Clarifai API. Is the basis for the Clarifai API and contains
 * standard methods for authentication, authorisaton and API Calls.
 *
 */
public class ClarifaiBasePlugin implements GMAF_Plugin {

	private ClarifaiImageLabeler clarifaiImageLabeler;
	private Float threshold = 0.5f;

	/**
	 * Initiates Clarifai-Image-Labeler Class.
	 */
	public ClarifaiBasePlugin() {
		// Connect Clarifai API //Theoretically should not be done always
		this.clarifaiImageLabeler = new ClarifaiImageLabeler();

	}

	public boolean canProcess(String extension) {
		if (extension.toLowerCase().endsWith("png"))
			return true;
		return false;
	}

	private Vector<Node> detectedNodes = new Vector<Node>();

	public Vector<Node> getDetectedNodes() {
		return detectedNodes;
	}

	public boolean isGeneralPlugin() {
		return false;
	}

	/**
	 * If response of API Call was successful, the data is extracted and added to
	 * the MMFG.
	 */
	public void process(URL url, File f, byte[] bytes, MMFG fv) {

		MultiOutputResponse responseOfClarifaiAPI = clarifaiImageLabeler.getResponseOfClarifaiQuery(f);

		// Check SuccessCode of response.
		boolean successfulQuery = clarifaiImageLabeler.isSuccesfulQuery();		
		
		if (successfulQuery) {
			ClarifaiResponseHandler clarifaiResponseHandler = new ClarifaiResponseHandler(responseOfClarifaiAPI);
			List<Region> regionListOfResponse = clarifaiResponseHandler.getRegionListOfResponse();
			
			for (Region region : regionListOfResponse) {
				Node currentNode = fv.getCurrentNode();
				Node n = clarifaiResponseHandler.createNode(region, threshold, fv);

				detectedNodes.add(n);
				currentNode.addChildNode(n);

			}

		} else {
			System.out.println("Could not complete query.");
		}

	}

	// if bounding boxes can be calculated, each detected object can be processed
	// recursively
	public boolean providesRecoursiveData() {
		return false;
	}

	/**
	 * Sets threshold for confidence: which concepts should be added to the mmfg.
	 * 
	 * @param threshold as float in decimal, e.g. 0.5f (also default value).
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
