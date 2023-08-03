package de.swa.fuh.mpeg7;

public class Scene {
	
	private String id;
	private MediaTime mediaTime;
	private Annotation annotation;
	boolean hasAnnotation;
	private AnnotationType annotation_type;


	
	public Scene(String id, MediaTime mediaTime, Annotation annotation) {
		this.id = id;
		this.mediaTime = mediaTime;
		this.annotation = annotation; 
		if (annotation != null) {
			this.hasAnnotation = true;
			this.annotation_type = annotation.getAnnotationType();
		} else {
			this.hasAnnotation = false;
		}
		

	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public MediaTime getMediaTime() {
		return mediaTime;
	}
	public void setMediaTime(MediaTime mediaTime) {
		this.mediaTime = mediaTime;
	}
	public Annotation getAnnotation() {
		return annotation;
	}
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public AnnotationType getAnnotation_type() {
		return annotation_type;
	}

	public void setAnnotation_type(AnnotationType annotation_type) {
		this.annotation_type = annotation_type;
	}
	
	@Override	
	public String toString() {
		return this.id + "; " + this.mediaTime + "; " + this.annotation;
	}


}
