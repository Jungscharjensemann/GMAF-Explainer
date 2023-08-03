package de.swa.fuh.mpeg7;

public class MultimediaContent {
	
	private String mediaLocator;
	private CreationInformation creationInformation;
	
	public MultimediaContent(String mediaLocator, CreationInformation creationInformation) {
		this.mediaLocator = mediaLocator;
		this.creationInformation = creationInformation;
	}
	
	public String getMediaLocator() {
		return mediaLocator;
	}
	public void setMediaLocator(String mediaLocator) {
		this.mediaLocator = mediaLocator;
	}
	public CreationInformation getCreationInformation() {
		return creationInformation;
	}
	public void setCreationInformation(CreationInformation creationInformation) {
		this.creationInformation = creationInformation;
	}



	

}
