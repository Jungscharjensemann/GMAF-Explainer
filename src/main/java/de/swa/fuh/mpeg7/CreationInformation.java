package de.swa.fuh.mpeg7;

public class CreationInformation {
	
	private String title; 
	private Annotation videoInfo;
	private String creator; 
	private String creationTime; 
	private String creationPlace;
	
	public CreationInformation() {};
	
	public CreationInformation(String title, Annotation videoInfo, String creator, String creationTime, String creationPlace) {
		this.title = title;
		this.videoInfo = videoInfo;
		this.creator = creator; 
		this.creationTime = creationTime;
		this.creationPlace = creationPlace;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Annotation getVideoInfo() {
		return videoInfo;
	}
	public void setVideoInfo(Annotation videoInfo) {
		this.videoInfo = videoInfo;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getCreationPlace() {
		return creationPlace;
	}
	public void setCreationPlace(String creationPlace) {
		this.creationPlace = creationPlace;
	}
	

}
