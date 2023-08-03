package de.swa.fuh.mpeg7;

import java.util.List;

//import builder.AnnotationBuilder;

/**
 * KeywordAnnotation
 * @author michaelhermann
 *
 */
public class KeywordAnnotation extends Annotation {
	
	private List<String> keywords;
	
	public List<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(List<String> keywords) {
		this.setAnnotationType(AnnotationType.KeywordAnnotation);
		this.keywords = keywords;
	}
	
//	/**
//	 * Returns List of NOUNS and PRON from FreeTextAnnotaion input
//	 * @param freeTextAnnotation
//	 * @return List<String>
//	 */
//	public List<String> build_keywordAnnotation_from_freeTextAnnotation(String freeTextAnnotation){
//		AnnotationBuilder annotationBuilder = new AnnotationBuilder();
//		return annotationBuilder.getKeywordAnnotation(freeTextAnnotation);
//	}
	

}
