package de.swa.fuh.aws.awsrekognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;


public class RekognitionImageLabler_JavaV2 {

	private RekognitionClient rekClient;
	private Region region = Region.US_EAST_2;
	private Image souImage;
	private float threshold = 50f; 
	private int maxLabels = 20;
	private boolean imageBufferSetupSuccess = false;

	/**
	 * 
	 * @param file      Image file
	 * @param threshold Set threshold for confidence of label. By default 50f.
	 * @param maxLabels Set amount of labels per query. by default 20.
	 */
	public RekognitionImageLabler_JavaV2(File file, float threshold, int maxLabels) {
		this.rekClient = RekognitionClient.builder().region(region).build();
		this.threshold = threshold;
		this.maxLabels = maxLabels;
		setImageSourceBytes(file);
	}

	/**
	 * Uses default threshold and maxLabels Parameter
	 * 
	 * @param file Imagefile
	 */
	public RekognitionImageLabler_JavaV2(File file) {
		this.rekClient = RekognitionClient.builder().region(region).build();
		setImageSourceBytes(file);
	}

	/**
	 * Sets bytestreeam for upload to labeling.
	 * 
	 * @param file Image file
	 */
	public void setImageSourceBytes(File file) {
		try {
			InputStream sourceStream = new FileInputStream(file);
			SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

			// Create an Image object for the source image
			this.souImage = Image.builder().bytes(sourceBytes).build();
			
			imageBufferSetupSuccess = true;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not find image file to send to AWS Rekognition V2.");
			imageBufferSetupSuccess = false;
		}

	}

	/**
	 * Helperfunction to build request
	 * 
	 * @return DetectLabelsRequest - Datatype of AWS.
	 */
	protected DetectLabelsRequest buildRequest() {
		if (imageBufferSetupSuccess) {
			return DetectLabelsRequest.builder()
                .image(souImage)
                .maxLabels(maxLabels).minConfidence(threshold)
                .build();
        
		} else
			return null;

	}

	/**
	 * Sends query for labeling and detects labels.
	 * 
	 * @return List of Labels.
	 */
	public List<Label> detectLabels() {
		try {
	         DetectLabelsResponse labelsResponse = rekClient.detectLabels(buildRequest());
	         return labelsResponse.labels();
	         
		} catch (Exception e) {
			System.out.println("Could not detect labels.");
			return null;
		}
	}

	/**
	 * Setter for maxLabels. By default 20.
	 * 
	 * @param maxLabels
	 */
	public void setMaxLabels(int maxLabels) {
		this.maxLabels = maxLabels;
	}

	/**
	 * Returns current maxLabels amount.
	 * 
	 * @return int of maxLabels per query.
	 */
	public int getMaxLabels() {
		return maxLabels;
	}

	/**
	 * Setter for threshold. Which minimum confidence should a label have to be
	 * processed. By defualt 50f.
	 * 
	 * @param threshold float variable
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	/**
	 * Returns current set threshold for label to be processed.
	 * 
	 * @return float
	 */
	public float getThreshold() {
		return threshold;
	}

}
