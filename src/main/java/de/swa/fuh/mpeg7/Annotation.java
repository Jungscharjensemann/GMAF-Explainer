package de.swa.fuh.mpeg7;

/**
 * Annotation Class. Can have different types. 
 * FreetextAnnotation is implemented. 
 * KeywordAnnotaton and DependencyStructure is extendible
 * @author michaelhermann
 *
 */
public class Annotation {
	
	//Basic Type - FreetextAnnotation
	private boolean hasAnnotation = false;
	private AnnotationType annotationType;
	private String freeTextAnnotation;
	
	public Annotation() {};
	
	public Annotation(String annotation) {
		this.freeTextAnnotation = annotation; 
		this.hasAnnotation = true;
		this.annotationType = AnnotationType.FreeAnnotation;
	}

	public boolean isHasAnnotation() {
		return hasAnnotation;
	}

	public void setHasAnnotation(boolean hasAnnotation) {
		this.hasAnnotation = hasAnnotation;
	}

	public String getFreeTextAnnotation() {
		return freeTextAnnotation;
	}

	public void setFreeTextAnnotation(String freeTextAnnotation) {
		this.annotationType = AnnotationType.FreeAnnotation;
		this.freeTextAnnotation = freeTextAnnotation;
	}

	public AnnotationType getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(AnnotationType annotationType) {
		this.annotationType = annotationType;
	}
	
	public boolean isFreeAnnotation() {
		if (this.annotationType == AnnotationType.FreeAnnotation) {
			return true;
		}
		return false;
	}
	
	public boolean isKeywordAnnotation() {
		if (this.annotationType == AnnotationType.KeywordAnnotation) {
			return true;
		}
		return false;
	}


	
}
