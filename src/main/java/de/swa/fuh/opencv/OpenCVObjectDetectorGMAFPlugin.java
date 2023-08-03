package de.swa.fuh.opencv;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

/** 
 * The Plugin class to make OpenCV compatible with GMAF.
 * @author Tobias Lukoschek
 */
public class OpenCVObjectDetectorGMAFPlugin implements GMAF_Plugin {

	/** 
	 * Loads OpenCV at the beginning of the process. 
	 */
	static { OpenCVObjectDetector.loadOpenCV(); } 
	
	/**
	 * Only PNGs can be proceeded. 
	 */
	public boolean canProcess(String extension) {
		if (extension.toLowerCase().endsWith("png"))
			return true;
		return false;
	}

	/** Holds the detected nodes after {@link OpenCVObjectDetectorGMAFPlugin.process} was called. */ 
	private Vector<Node> detectedNodes = new Vector<Node>();

	/**
	 * Returns the detected nodes  after {@link OpenCVObjectDetectorGMAFPlugin.process} was called. 
	 */
	public Vector<Node> getDetectedNodes() {
		return detectedNodes;
	}

	/**
	 * Returns if this is a general plugin.
	 */
	public boolean isGeneralPlugin() {
		return false;
	}

	/** 
	 * Process the detection of OpenCV. 
	 * @param url url of the image.
	 * @param f the image file. 
	 * @param bytes the image loaded in bytes [].
	 * @param fv The MMFG used to get the parent node. 
	 */
	public void process(URL url, File f, byte[] bytes, MMFG fv) {
		if(!canProcess(f.getName())) {
			throw new IllegalArgumentException("The file f cannot be proceeded.");
		}
		final OpenCVObjectDetector openCVObjectDetector = new OpenCVObjectDetector();
		openCVObjectDetector.loadResourceLocation();
		openCVObjectDetector.loadModel();
		openCVObjectDetector.loadImageFromPath(f.getAbsolutePath());
		openCVObjectDetector.addDetectionsToNode(fv, detectedNodes, 0.5f);
	}

	/** 
	 * The module generates bounding boxes so it can used recursively. 
	 */
	public boolean providesRecoursiveData() {
		return true;
	}
}
