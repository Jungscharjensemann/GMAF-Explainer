package de.swa.test;

import java.util.Vector;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

 * 
 */
public class TrecTopicParser extends DefaultHandler {
	private String lastString = "";
	private TrecTopic current = new TrecTopic();
	private Vector<TrecTopic> topics = new Vector<TrecTopic>();
	private int subtopic_counter = 0;
	
	public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException { 
		lastString = new String(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("top")) {
			topics.add(current);
			System.out.println("adding topic " + current.getNumber() + " " + current.getTitle());
			current = new TrecTopic();
		}
		else if (qName.equals("num")) {
			String number = lastString.substring(8, lastString.length()).trim();
			current.setNumber(number);
		}
		else if (qName.equals("docid")) current.setDocId(lastString);
		else if (qName.equals("url")) current.setUrl(lastString);
		else if (qName.equals("title")) current.setTitle(lastString);
		else if (qName.equals("narr")) current.setNarration(lastString);
		else if (qName.equals("desc")) current.setDescription(lastString);
		else if (qName.equals("sub")) {
			TrecTopic sub = new TrecTopic();
			sub.setNumber("" + subtopic_counter);
			sub.setTitle(lastString);
			current.addSubtopics(sub);
			subtopic_counter ++;
		}
		else if (qName.equals("subtopics")) subtopic_counter = 0;
	}
	
	public Vector<TrecTopic> getTopics() {
		return topics;
	}
}
