package de.swa.fuh.mpeg7;

import java.util.List;

/**
 * Video Class. Represents VideoSegment Type in MPEG.
 * @author michaelhermann
 *
 */
public class Video extends MultimediaContent{
	
	public Video(String mediaLocator, CreationInformation creationInformation, String id, MediaTime mediaTime, List<Scene> scenes) {
		super(mediaLocator, creationInformation);
		this.id = id; 
		this.mediaTime = mediaTime; 
		this.scenes = scenes;
	}
	
	
	private String id;
	private MediaTime mediaTime;
	private List<Scene> scenes;
	
	
	public MediaTime getMediaTime() {
		return mediaTime;
	}
	public void setMediaTime(MediaTime mediaTime) {
		this.mediaTime = mediaTime;
	}
	public List<Scene> getScenes() {
		return scenes;
	}
	public void setScenes(List<Scene> scenes) {
		this.scenes = scenes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	

}
