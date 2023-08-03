package de.swa.gmaf.processingflow;

import java.util.Hashtable;

public class ProcessElement {
	private String name;
	private Hashtable<String, String> attributes = new Hashtable<String, String>();
	
	public ProcessElement(String n) {
		name = n;
	}
	
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	public String getName() {
		return name;
	}
	
	public String getAttributeValue(String attributeName) {
		return attributes.get(attributeName);
	}
}
