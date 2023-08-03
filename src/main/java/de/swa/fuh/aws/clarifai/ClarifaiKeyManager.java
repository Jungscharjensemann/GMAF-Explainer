package de.swa.fuh.aws.clarifai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.swa.fuh.aws.helperClasses.OperatingSystem;
import de.swa.fuh.aws.helperClasses.OperatingSystemClassifier;

/**
 * 
 * Clarifai does not offer a Key-Management and Reading System.
 * This Class loads the location of the key file.
 *
 */
class ClarifaiKeyManager {
	
	private String pathToCredentialsFile;
	private String API_KEY;
	
	/**
	 * On init sets operating system by OperatingSystemClassifier. 
	 * Sets the credentials path based on os and checks if credentials file exists
	 */
	ClarifaiKeyManager(){
		setCredentialsPathBasedOnOS(new OperatingSystemClassifier().getOperatingSystem());
		if(checkIfCredentialsFileExists()) {
			readKey(pathToCredentialsFile);
		} else {
			System.out.println("Key file is not provided.");
		}
	
	}
	
	/**
	 * 
	 * @param Enum OperatingSystem. Can be MacOSX, Windows or Linux.
	 * Other systems are not supported.
	 * @return Path to key file.
	 */
	private void setCredentialsPathBasedOnOS(OperatingSystem os) {
		switch (os) {
		case WINDOWS:			
			pathToCredentialsFile = "C:\\clarifai\\Clarifai_Key.txt";
			break;
		case MACOSX:
			pathToCredentialsFile = "/Users/" + System.getProperty("user.name") + "/clarifai/Clarifai_Key.txt";
			// return "~/clarifai/Clarifai_Key.txt"; did not work
			break;
		case LINUX:
			pathToCredentialsFile = "~/clarifai/Clarifai_Key.txt";
			break;
		default:
			System.out.println("Not supported OS.");
			pathToCredentialsFile =  null;
		}
		
	}

	/**
	 * If applicable: set alternative credentials path.
	 * @param pathToKey
	 */
	public void setAlternativeCredentialsPath(String pathToKey) {
		this.pathToCredentialsFile = pathToKey;
		if(checkIfCredentialsFileExists()) {
			readKey(pathToKey);
		} else {
			System.out.println("File not found");
		}
	}
	
	/**
	 * Check if file defined by the path exists.
	 * @return
	 */
	public boolean checkIfCredentialsFileExists() {
		File file = new File(pathToCredentialsFile);
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reads API Key as String from textfile and sets private variable to the key.
	 */
	private void readKey(String pathToKey) {
		try {			
			this.API_KEY = new String(Files.readAllBytes(Paths.get(pathToKey)));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No file with API key found.");
		}
	}
	
	
	/**
	 * Return read API KEY
	 * @return
	 */
	protected String getKey() {
		return API_KEY;
	}

}
