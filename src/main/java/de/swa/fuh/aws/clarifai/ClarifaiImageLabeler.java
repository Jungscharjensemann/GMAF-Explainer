package de.swa.fuh.aws.clarifai;

import com.clarifai.grpc.api.*;
import com.google.protobuf.ByteString;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.V2Grpc;
import io.grpc.Channel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 
 * Class to call Image labeling operations.
 */
public class ClarifaiImageLabeler {

	private boolean succesfulQuery = false;
	private ClarifaiConnector clarifaiConnector;

	/**
	 * Constructor. Creates Clarifaiconnector and established Connection. By default
	 * uses gPRC, because Clarifai recommends using this protocol with their
	 * service.
	 */
	public ClarifaiImageLabeler() {
		this.clarifaiConnector = new ClarifaiConnector(ConnectionType.gPRC);
	}

	/**
	 * 
	 * @param file Image file.
	 * @return MultiOutputResponse. Data-structure designed by Clarifai. Holds all
	 *         information of query.
	 */
	public MultiOutputResponse getResponseOfClarifaiQuery(File file) {
		Channel channel = clarifaiConnector.getChannel();
		V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(channel)
				.withCallCredentials(new ClarifaiCallCredentials(new ClarifaiKeyManager().getKey()));

		boolean readingByteStringSuccess = false;

		// exclude bytestring check from method call.
		byte[] byteString;
		try {
			byteString = Files.readAllBytes(file.toPath());
			readingByteStringSuccess = true;
		} catch (IOException e) {
			byteString = null;
			readingByteStringSuccess = false;
			e.printStackTrace();
		}

		if (readingByteStringSuccess) {
			MultiOutputResponse response = stub.postModelOutputs(PostModelOutputsRequest.newBuilder()
					.setModelId("9f54c0342741574068ec696ddbebd699") // Model name: general; model type id:
																	// visual-detector
					/*
					 * .setVersionId("{THE_MODEL_VERSION_ID") // This is optional. Defaults to the
					 * latest model version.
					 */
					.addInputs(Input.newBuilder().setData(
							Data.newBuilder().setImage(Image.newBuilder().setBase64(ByteString.copyFrom(byteString)))))
					.build());
			setSuccesfulQuery(true);
			return response;
		} else {
			setSuccesfulQuery(false);
			return null; // null will be resolved to empty label list.
		}
	}

	public boolean isSuccesfulQuery() {
		return succesfulQuery;
	}

	public void setSuccesfulQuery(boolean succesfulQuery) {
		this.succesfulQuery = succesfulQuery;
	}
	
	

}
