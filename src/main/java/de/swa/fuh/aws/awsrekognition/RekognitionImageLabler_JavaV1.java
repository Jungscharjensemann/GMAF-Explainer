package de.swa.fuh.aws.awsrekognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;
import com.amazonaws.services.rekognition.AmazonRekognition;

/**
 * 
 * Helper Class to call image labeling. 
 *
 */
public class RekognitionImageLabler_JavaV1 {
	
	private AmazonRekognition rekognitionClient;
	private ByteBuffer imageBytes;
	private float threshold = 50f;
	private int maxLabels = 20;
	private boolean imageByteSetupSuccess = false;
	
	/**
	 * 
	 * @param file Image file
	 * @param threshold Set threshold for confidence of label. By default 50f. 
	 * @param maxLabels Set amount of labels per query. by default 20.
	 */
	public RekognitionImageLabler_JavaV1(File file, float threshold, int maxLabels){
		this.rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		this.threshold = threshold;
		this.maxLabels = maxLabels;
		setImageBytes(file);
	}
	
	/**
	 * Uses default threshold and maxLabels Parameter
	 * @param file Imagefile
	 */
	public RekognitionImageLabler_JavaV1(File file){
		this.rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		setImageBytes(file);
	}
	
	/**
	 * Checks if image size is to big for labeling. 
	 * @param file Imagefile.
	 * @return boolean wheter size is ok.
	 */
	public boolean checkImageSize(File file) {
		long fileLength = file.length();
		int maxLength = 5242880; // See https://docs.aws.amazon.com/rekognition/latest/dg/API_Image.html
		return fileLength < maxLength;
	}
	
	//this.rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
	
	/**
	 * Sets bytestreeam for upload to labeling.
	 * @param file Image file
	 */
	public void setImageBytes(File file) {
		if(checkImageSize(file)) {
			try {
				InputStream inputStream = new FileInputStream(file);
				this.imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
				imageByteSetupSuccess = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not find image file to send to AWS Rekognition.");
				imageByteSetupSuccess = false;
			}
		} else {
			System.out.println("Image was too big for this version of AWS Rekognition (JavaV1). Max are 5242880 Bytes.\nSee https://docs.aws.amazon.com/rekognition/latest/dg/API_Image.html" );
			imageByteSetupSuccess = false;
		}
	}
		
	/**
	 * Helperfunction to build request
	 * @return DetectLabelsRequest - Datatype of AWS.
	 */
	protected DetectLabelsRequest buildRequest() {
		if(imageByteSetupSuccess) {
			return new DetectLabelsRequest()
			          .withImage(new Image()
			                  .withBytes(imageBytes))
			          .withMaxLabels(maxLabels)
			          .withMinConfidence(threshold); 
		} else return null;

	}
	
	/**
	 * Sends query for labeling and detects labels. 
	 * @return List of Labels. Or Empty list if request goes wrong.
	 */
	public List<Label> detectLabels() {
		try {
			DetectLabelsRequest queryRequest = buildRequest();
			DetectLabelsResult result = rekognitionClient.detectLabels(queryRequest);
			return result.getLabels();
		} catch (Exception e) {
			System.out.println("Could not detect any labels.");
			return new ArrayList<>();
		}
	}
	
	
	/**
	 * Setter for maxLabels. By default 20.
	 * @param maxLabels
	 */
	public void setMaxLabels(int maxLabels) {
		this.maxLabels = maxLabels;
	}
	
	/**
	 * Returns current maxLabels amount.
	 * @return int of maxLabels per query.
	 */
	public int getMaxLabels() {
		return maxLabels;
	}
	
	/**
	 * Setter for threshold. Which minimum confidence should a label have to be processed. By defualt 50f.
	 * @param threshold float variable
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	/**
	 * Returns current set threshold for label to be processed.
	 * @return float 
	 */
	public float getThreshold() {
		return threshold;
	}

	/**
	 * Return ImageBytes
	 * @return
	 */
	public ByteBuffer getImageBytes() {
		return this.imageBytes;
	}
	
}
