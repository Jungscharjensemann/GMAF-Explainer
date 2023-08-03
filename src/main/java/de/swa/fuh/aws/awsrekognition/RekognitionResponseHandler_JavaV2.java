package de.swa.fuh.aws.awsrekognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import software.amazon.awssdk.services.rekognition.model.Label;

import de.swa.mmfg.Context;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;
import de.swa.mmfg.Weight;

public class RekognitionResponseHandler_JavaV2 {
	private int imageWidth; 
	private int imageHeight;
	
	/**
	 * 
	 * @param label Detected Rekognition Label.
	 * @param fv MMFG 
	 * @return Node with set Name, Confidence, DetectedBy-Argument and (if available) boundingbox.
	 */
	public Node getNode(Label label, MMFG fv) {
		Node n = new Node(label.name(), fv);
		n.setDetectedBy("Rekognition_V1");
		float confidence = label.confidence();
		n.addWeight(setWeight(confidence/100f));
		
		if(!checkBoundingBox(label)) { //add Boundingbox
			n.addTechnicalAttribute(setTechAtt(label)); 
		}
		return n;
	
	}
	
	/**
	 * Helperfunction to set confidence in Node. Needs to be divides by 100. To fit GMAF Standard.
	 * @param confidence float value.
	 * @return Weight with confidence
	 */
	private Weight setWeight(float confidence) {
		Context gmafContext = new Context();
		gmafContext.setName("Confidence-Clarifai");
		return new Weight(gmafContext, confidence/100);
	}
	
	/**
	 * Checks whether a bounding box was send for particular label
	 * @param label 
	 * @return boolean.
	 */
	private boolean checkBoundingBox(Label label) {
		return label.instances().isEmpty();
	}
	
	/**
	 * Helperfunction to set bounding box
	 * Assuming that the origin is at the top left.
	 * @param label
	 * @return TechnicalAttribute
	 */
	private TechnicalAttribute setTechAtt(Label label) {
		//Get Data of Boundingbox of API
		float left = label.instances().get(0).boundingBox().left();
		float top = label.instances().get(0).boundingBox().top();
		float height = label.instances().get(0).boundingBox().height();
		float width = label.instances().get(0).boundingBox().width();

		
		//Convert Data of Boundingbox to image size
		int heightBox = (int) height * imageHeight;
		int widthBox = (int) width * imageWidth;
		int relative_x = (int) (left * imageWidth);
		int relative_y = (int) (top * imageHeight);
		
		//create new technicalAttribute
		TechnicalAttribute techAtt = new TechnicalAttribute();
		
		//Sett Attribute to coordinates
		techAtt.setRelative_x(relative_x);
		techAtt.setRelative_y(relative_y);
		techAtt.setHeight(heightBox);
		techAtt.setWidth(widthBox);
		techAtt.setSharpness(1.0f); // Default
		techAtt.setBlurryness(0.0f); //Default

		return techAtt;
	}
	
	
	/**
	 * Sets image size of particular file
	 * @param width
	 * @param height
	 */
	public void setImageSize(File file) {
		BufferedImage bimg;
		try {
			bimg = ImageIO.read(file);
			this.imageHeight = bimg.getHeight();
			this.imageWidth = bimg.getWidth();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not set image size.");
		}		

	}

}
