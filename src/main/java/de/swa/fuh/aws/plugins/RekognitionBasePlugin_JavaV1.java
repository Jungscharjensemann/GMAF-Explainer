package de.swa.fuh.aws.plugins;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.amazonaws.services.rekognition.model.Label;

import de.swa.fuh.aws.awsrekognition.RekognitionImageLabler_JavaV1;
import de.swa.fuh.aws.awsrekognition.RekognitionResponseHandler_JavaV1;
import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class RekognitionBasePlugin_JavaV1 implements GMAF_Plugin {

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
		
		// Also checks whether image size is too big for V1. Max 5242880 Bytes
		RekognitionImageLabler_JavaV1 rekognitionLabler = new RekognitionImageLabler_JavaV1(f);

		// Response
		List<Label> labelsByRekognitionResponse_JavaV1 = rekognitionLabler.detectLabels();
		
		RekognitionResponseHandler_JavaV1 responseHandler_JavaV1 = new RekognitionResponseHandler_JavaV1();
		responseHandler_JavaV1.setImageSize(f);

		if (!labelsByRekognitionResponse_JavaV1.isEmpty()) {

			for (Label label : labelsByRekognitionResponse_JavaV1) {
				Node currentNode = fv.getCurrentNode();

				Node n = responseHandler_JavaV1.getNode(label, fv);

				detectedNodes.add(n);
				currentNode.addChildNode(n);
			}
		} else {
			System.out.println("Labeling was not successful. List is empty.");
		}

	}

	// if bounding boxes can be calculated, each detected object can be processed
	// recursively
	public boolean providesRecoursiveData() {
		return false;
	}
}
