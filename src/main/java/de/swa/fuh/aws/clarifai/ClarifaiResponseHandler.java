package de.swa.fuh.aws.clarifai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.clarifai.grpc.api.BoundingBox;
import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Data;
import com.clarifai.grpc.api.MultiOutputResponse;
import com.clarifai.grpc.api.Region;

import de.swa.mmfg.Context;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;
import de.swa.mmfg.Weight;

/**
 * Helper class to handle respondes of the clarifai API
 *
 */
public class ClarifaiResponseHandler {
	private int imageWidth; 
	private int imageHeight;
	private MultiOutputResponse multiOutputResponse;
	
	public ClarifaiResponseHandler(MultiOutputResponse response){
		this.multiOutputResponse = response;
	}
	
	/**
	 * Return List<Region>. 
	 * @return
	 */
	public List<Region> getRegionListOfResponse(){
		try {
			return multiOutputResponse.getOutputs(0).getData().getRegionsList();
		} catch (Exception e){
			return new ArrayList<>();
		}
		
	}
	
	public Concept getConcept(Region region) {
		Data data = region.getData();
		Concept concept = data.getConceptsList().get(0);
		return concept;
	}
	
	/**
	 * 
	 * @param region
	 * @param threshold
	 * @param fv
	 * @return Node, which will be added to MMFG.
	 */
	public Node createNode(Region region, float threshold, MMFG fv) {
		Node n = new Node("", fv);
		
		Concept concept = getConcept(region);
		String name = concept.getName();
		
		float confidence = region.getValue(); //also through concept.getValue() accessbilbe
		
		if (confidence > threshold) {
			n.setName(name);
			n.setDetectedBy("Clarifai");
			n.addWeight(setWeight(confidence));
			
			n.addTechnicalAttribute(buildTechAtt(region)); 
		}
		return n;
		
	}
	
	private Weight setWeight(float confidence) {
		Context gmafContext = new Context();
		gmafContext.setName("Confidence-Clarifai");
		return new Weight(gmafContext, confidence);
	}

	/**
	 * 
	 * Assuming that the origin is at the top left.
	 * @param region
	 * @return
	 */
	private TechnicalAttribute buildTechAtt(Region region) {

		//Get Data of Boundingbox of API
		BoundingBox boundingbox = region.getRegionInfo().getBoundingBox();
		float topRow = boundingbox.getTopRow();
		float bottomRow = boundingbox.getBottomRow();
		float leftCol = boundingbox.getLeftCol();
		float rightCol = boundingbox.getRightCol();
		
		
		//Convert Data of Boundingbox to image size
		int heightBox = (int) ((bottomRow - topRow)*imageHeight);
		int widthBox = (int) ((rightCol - leftCol)*imageWidth);
		int relative_x = (int) leftCol*imageWidth ;
		int relative_y = (int) topRow*imageHeight;
		
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
	protected void setImageSize(File file) {
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
