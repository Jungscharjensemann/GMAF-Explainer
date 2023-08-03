package de.swa.fuh.aws.plugins;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import de.swa.fuh.aws.awsrekognition.RekognitionImageLabler_JavaV2;
import de.swa.fuh.aws.awsrekognition.RekognitionResponseHandler_JavaV2;
import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import software.amazon.awssdk.services.rekognition.model.Label;


public class RekognitionBasePlugin_JavaV2 implements GMAF_Plugin {
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

	public void process(URL url, File f, byte[] bytes, MMFG fv) {
		
		RekognitionImageLabler_JavaV2 rekognitionLabler = new RekognitionImageLabler_JavaV2(f);

		//Response
		List<Label> labelsByRekognitionResponse_JavaV2 = rekognitionLabler.detectLabels();
		RekognitionResponseHandler_JavaV2 responseHandler_JavaV2 = new RekognitionResponseHandler_JavaV2();
		responseHandler_JavaV2.setImageSize(f);
		
		
		for (Label label : labelsByRekognitionResponse_JavaV2) {
			Node currentNode = fv.getCurrentNode();	
			
			
			Node n = responseHandler_JavaV2.getNode(label, fv);
			
			detectedNodes.add(n);
			currentNode.addChildNode(n);
		}
		
		
	}

	// if bounding boxes can be calculated, each detected object can be processed
	// recursively
	public boolean providesRecoursiveData() {
		return false;
	}
}
