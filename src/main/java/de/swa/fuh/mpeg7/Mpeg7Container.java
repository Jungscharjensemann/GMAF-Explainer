package de.swa.fuh.mpeg7;


public class Mpeg7Container {
	
	private DescriptionMetadata descriptionMetadata;
	private MultimediaContent multimediaContent;
	private boolean isValid;
	
	public Mpeg7Container() {
		
	}

	public Mpeg7Container( DescriptionMetadata descriptionMetadata, MultimediaContent multimediaContent) {
		this.descriptionMetadata = descriptionMetadata;
		this.multimediaContent = multimediaContent;
		this.isValid = true;
	}
	
	public DescriptionMetadata getDescriptionMetadata() {
		return descriptionMetadata;
	}
	
	public void setDescriptionMetadata(DescriptionMetadata descriptionMetadata) {
		this.descriptionMetadata = descriptionMetadata;
	}
	
	public MultimediaContent getMultimediaContent() {
		return multimediaContent;
	}
	
	public void setMultimediaContent(MultimediaContent multimediaContent) {
		this.multimediaContent = multimediaContent;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
}
