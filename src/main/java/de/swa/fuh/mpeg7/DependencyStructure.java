package de.swa.fuh.mpeg7;

public class DependencyStructure extends Annotation{
	
	private String annotation;

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.setAnnotationType(AnnotationType.DependencyStructureAnnotation);
		this.annotation = annotation;
	}
	

	//TODO

}
