package de.swa.fuh.mpeg7;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which reads a CSV and returns List from each column
 * @author michaelhermann
 *
 */
public class Map_Reader {
	
	private List<String> keys = new ArrayList<>();
	private List<String> xpath_strings = new ArrayList<>();
	
	Map_Reader(String fileName){ 
		this.read_csv(fileName);
	}
	
	/**
	 * Reads CSV file and saves the Lists in the Object. 
	 * Removes the headings of each columns 
	 * @param fileName Path to CSV
	 */
	private void read_csv(String fileName) {
		
		Path pathToFile = Paths.get(fileName);		
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {
        	
        	
        	String line = br.readLine(); 
        	
        	while (line != null) {
        		
        		String[] attributes = line.split(";");
        		
        		String key = attributes[0];
        		String xpath = attributes[1];
        		
        		this.keys.add(key);
        		this.xpath_strings.add(xpath);        		
        		
        		line = br.readLine();
        	}
        	
        	//remove column name
        	this.keys.remove(0);
        	this.xpath_strings.remove(0);

        	
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
		
	}
	
	/**
	 * Returns the extracted keys (first column)
	 * @return Keys as List
	 */
	public List<String> get_keys() {
		return keys;
	}

	/**
	 * Sets Keys Strings List
	 * @param keys List of keys
	 */
	public void set_keys(List<String> keys) {
		this.keys = keys;
	}

	/**
	 * Returns the extracted XPaths (second column)
	 * @return
	 */
	public List<String> getXPath_strings() {
		return xpath_strings;
	}
	
	/**
	 * Set XPath Strings List
	 * @param xpath_strings List of XPath Strings
	 */
	public void setXPath_strings(List<String> xpath_strings) {
		this.xpath_strings = xpath_strings;
	}
	
	

}
