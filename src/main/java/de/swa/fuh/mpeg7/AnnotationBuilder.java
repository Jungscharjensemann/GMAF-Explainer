//package de.swa.ext.fuh.mpeg7;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//import mpeg7.Language;
//
///**
// * Builds Annotation 
// * @author michaelhermann
// *
// */
//public class AnnotationBuilder {
//	
//	private Language language;
//	private List<String> acceptedPosTagsList = Arrays.asList("PROPN", "NOUN"); //What should be extracted as keyword from Text
//	
//	public AnnotationBuilder(){
//		this.setLanguage(Language.DE); 
//	}
//	
//	AnnotationBuilder(String language){
//		this.setLanguage(Language.valueOf(language));
//	}
//	
//	/**
//	 * Returns the current set language
//	 * @return
//	 */
//	public Language getLanguage() {
//		return language;
//	}
//
//	/**
//	 * Sets the language
//	 * @param language Language
//	 */
//	public void setLanguage(Language language) {
//		this.language = language;
//	}
//	
//	/**
//	 * Sets the MaxentTagger depending on the language
//	 * @return MaxentTagger set up in chosen language
//	 */
//	private MaxentTagger setMaxentTagger() {
//		MaxentTagger tagger = null;
//		switch(this.language) {
//		case DE:
//			tagger =  new MaxentTagger("taggers/german-ud.tagger");
//			break;
//		case EN: 
//			tagger =  new MaxentTagger("taggers/english-caseless-left3words-distsim.tagger");
//			break;
//		default:
//			tagger =  new MaxentTagger("taggers/german-ud.tagger");
//		}
//		return tagger;
//		
//	}
//	
//	/**
//	 * Returns List of accepted POS Tags
//	 * @return List 
//	 */
//	public List<String> getAcceptedPosTagsList() {
//		return acceptedPosTagsList;
//	}
//
//	/**
//	 * Sets Accepted POS Tags
//	 * @param acceptedPosTagsList
//	 */
//	public void setAcceptedPosTagsList(List<String> acceptedPosTagsList) {
//		this.acceptedPosTagsList = acceptedPosTagsList;
//	}
//	
//	/**
//	 * Get Keywords Annotation from TextAnnotation
//	 * @param inputAnnotation
//	 * @return List of Keywords
//	 */
//	public List<String> getKeywordAnnotation (String inputAnnotation) {
//		
//
//		MaxentTagger tagger = setMaxentTagger();
//		String taggedString = tagger.tagTokenizedString(inputAnnotation);
//		
//		String[] keywords = taggedString.split(" ");
//		
//		List<String> keywordList = new ArrayList<String>();
//		for (String keyword_tag : keywords) {
//			String[] keyword_split = keyword_tag.split("_");
//			String keyword = keyword_split[0];
//			String tag = keyword_split[1];
//			
//			
//			if (this.acceptedPosTagsList.contains(tag)) {
//				keywordList.add(keyword);
//			}
//		}
//
//		return keywordList;
//	}
//	
//	
//
//}
