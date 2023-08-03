package de.swa.fuh.mpeg7;

public class DescriptionMetadata {
	
	private String privateIdentifier;
	private String creator;
	private String creationLocation;
	private String creationTime;
	private String instrument;
	private String rights;
	
	public DescriptionMetadata(){
		//Init with default values
		this.privateIdentifier = "GMAF";
		this.creator = "Hermann";
		this.creationLocation = "Germany";
		this.creationTime = "2022";
		this.instrument = "GMAF Tool";
		this.rights = "licensed";
	};
	
	public DescriptionMetadata(String privateIdentifier, String creator, String creationLocation, String creationTime, String instrument, String rights){
		this.privateIdentifier = privateIdentifier;
		this.creator = creator;
		this.creationLocation = creationLocation;
		this.creationTime = creationTime;
		this.instrument = instrument;
		this.rights = rights;
	}
	
	
	public String getCreationLocation() {
		return creationLocation;
	}
	
	public void setCreationLocation(String creationLocation) {
		this.creationLocation = creationLocation;
	}
	

	public String getCreationTime() {
		return creationTime;
	}
	
	//TODO bestimmte Form aufweisen
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	
	public String getInstrument() {
		return instrument;
	}
	
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}
	
	public String getRights() {
		return rights;
	}
	
	public void setRights(String rights) {
		this.rights = rights;
	}
	
	public String getPrivateIdentifier() {
		return privateIdentifier;
	}
	
	public void setPrivateIdentifier(String privateIdentifier) {
		this.privateIdentifier = privateIdentifier;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public void printDescriptionMetadata() {
		System.out.println("Printing all data for DescriptionMetadata");
		
		System.out.println("privateIdentifier: " + this.privateIdentifier);
		System.out.println("creator: " + this.creator);
		System.out.println("creationLocation: " + this.creationLocation);
		System.out.println("creationTime: " + this.creationTime);
		System.out.println("instrument: " + this.instrument);
		System.out.println("rights: " + this.rights);

	}
	

}
