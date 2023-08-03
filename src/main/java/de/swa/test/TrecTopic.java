package de.swa.test;

import java.util.Vector;

public class TrecTopic {
	/* 
	 * 
<top>
    <num> Number: 936 </num>
    <docid> f831cae6-bfa4-11e1-9ce8-ff26651238d0 </docid>
    <url> https://www.washingtonpost.com/local/obituaries/nora-ephron-prolific-author-and-screenwriter-dies-at-age-71/2012/06/26/gJQAMOtN5V_story.html </url>
    <title> Nora Ephron dies at 71 </title>
    <desc> I'm looking for information on the passing of author and screenwriter Nora Ephron. </desc>
    <narr>
    Please provide information as to when author and screenwriter Nora Ephron died and of what causes.  Biographical details of her life and career are all relevant. Reactions to her passing by friends and the media are relevant as well.
    </narr>
    <subtopics>
        <sub num="0">Find details of Nora Ephron's life and accomplishments.</sub>
        <sub num="1">What are Nora Ephron's most well-known works?</sub>
        <sub num="2">Did Nora Ephron know that Mark Felt was "Deep Throat" of Watergate fame?</sub>
    </subtopics>
</top>

	 */

	private String number;
	private String docId;
	private String url;
	private String title;
	private String description;
	private String narration;
	private Vector<TrecTopic> subtopics = new Vector<TrecTopic>();
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public Vector<TrecTopic> getSubtopics() {
		return subtopics;
	}
	public void addSubtopics(TrecTopic subtopics) {
		this.subtopics.add(subtopics);
	}
	
	
}
