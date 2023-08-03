package de.swa.fuh.aws.clarifai;



import com.clarifai.channel.ClarifaiChannel;
import io.grpc.Channel;


/**
 * 
 * Helperclass to connect to the Clarifai backend.
 *	
 */

public class ClarifaiConnector {
	private Channel channel;
	
	public ClarifaiConnector(ConnectionType type){
		setChannel(type);
	}
	
	/**
	 * Set connection type. Tested with Windows and MacOS as of 31.07.2021
	 * @param  Enum ConnectionType. Chose between connection types: gPPRC, json, unEncryptedgPRC.
	 */
	private void setChannel(ConnectionType type){
		switch(type) {
		case gPRC:
			this.channel = ClarifaiChannel.INSTANCE.getGrpcChannel();
			break;
		case json:
			this.channel =  ClarifaiChannel.INSTANCE.getJsonChannel();
			break;
		case unEncryptedgPRC:
			this.channel =  ClarifaiChannel.INSTANCE.getInsecureGrpcChannel();
			break;
		default:
			this.channel = null;
		}
	}
	
	public Channel getChannel() {
		return channel;
	}
	

}
