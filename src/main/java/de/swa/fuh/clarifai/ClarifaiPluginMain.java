package de.swa.fuh.clarifai;

import com.clarifai.grpc.api.*;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/**
 * Application for object detection in image files through the standard ClarifAI API.</br>
 * 
 * Images can be submitted as URL objects, as image files or as byte arrays.</br>
 * 
 * Legal image formats: JPG, PNG, TIFF, BMP, WEBP</br>
 * 
 * Application needs exactly one argument, containing one of the following:</br>
 * <ul>
 * <li>Image url as a string</li>
 * <li>Image file path</li>
 * <li>Directory path containing image files</li>
 * </ul>
 * 
 * The application will send either the single image given as an argument to the API or
 * every image contained in the directory in seperate API calls.
 * 
 * Results of API calls are being saved as XML files in the image file or directory file
 * or in current user directory if the application argument was an image url.
 * 
 * The following parameters for the API call can be edited through clarifaiPlugin.config</br>
 * <ul>
 * <li><strong>API key</strong>: This needs to be generated through clarifai.com in order for this application to work!</li>
 * <li><strong>Model ID</strong>: The model id to be used for making API calls. For more information see 
 * <a href="https://www.clarifai.com/developers/pre-trained-models">ClarifAI pre trained models</a></li>
 * <li><strong>maximumConcepts</strong>: Defines the maximum amount of objects returned by the API. Default is 0 for no limitation.</li>
 * <li><strong>minConfidece</strong>: Defines the minimal confidence for objects to be returned by the API. Default is 0 for no limitation.</li>
 * </ul>
 * 
 * @author: Julius K�ndiger, ClarifAI
 * 
 */
public class ClarifaiPluginMain {
	static String path;
	static List<Region> results;
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		long startTimeGeneral = System.currentTimeMillis();
		
		ClarifAIObjectDetection objectDetection = new ClarifAIObjectDetection();
		
		if (args[0] != null) {
			boolean isWeb = args[0].startsWith("http");
			String filepath = "";
			long timeDetection = 0;
			if(isWeb) {
				URL url = new URL(args[0]);
				long startTimeDetection = System.currentTimeMillis();
				results = objectDetection.getObjects(url);
				timeDetection = (long)(System.currentTimeMillis() - startTimeDetection)/1000;
				filepath = System.getProperty("user.dir") + url.getFile() + ".xml";
				File inputFile = new File(url.toURI());
				Dimension dimension = getImageDimension(inputFile);
				generateXML(results,filepath,dimension);
			} else {
				File inputFile = new File(args[0]);
				if(inputFile.isFile()) {
					long startTimeDetection = System.currentTimeMillis();
					results = objectDetection.getObjects(inputFile);
					timeDetection = (long)(System.currentTimeMillis() - startTimeDetection)/1000;
					filepath = inputFile.getPath() + ".xml";
					Dimension dimension = getImageDimension(inputFile);
					generateXML(results,filepath,dimension);
				} else {
					ImageFilter imageFilter = new ImageFilter();
					File[] files = inputFile.listFiles(imageFilter);
					for(int i=0;i<files.length;i++) {
						long startTimeDetection = System.currentTimeMillis();
						results = objectDetection.getObjects(files[i]);
						timeDetection = timeDetection + (long)(System.currentTimeMillis() - startTimeDetection)/1000;
						filepath = files[i].getPath() + ".xml";
						Dimension dimension = getImageDimension(files[i]);
						generateXML(results,filepath,dimension);
					}
				}
				
			}
			generateJSON(startTimeGeneral,timeDetection,filepath);
			
		} else throw new IllegalArgumentException("No arguments!");
	
	}
	
	/**
	 * Generates an XML file containing the results of the API call.
	 * 
	 * @param results Bounding boxes gotten from the API call
	 * @param filepath Path of the XML file to be generated
	 * @param dimension Image dimensions needed for calculating scaled bounding boxes
	 */
	public static void generateXML(List<Region> results,String filepath,Dimension dimension) {
		XMLHandler xmlParser = new XMLHandler();
		File outputFile = new File(filepath);
		xmlParser.write(outputFile,results,dimension);
	}
	
	/**
	 * Generates a JSON file in image directory which contains the times needed for the whole application as well as for
	 * the image procession via the API.
	 * 
	 * @param startTimeGeneral Start time generated at the start of the application
	 * @param timeDetection Time needed for image processing through API
	 * @param filepath Path of the current image or directory being processed
	 * @throws IOException if file cannot be created
	 */
	public static void generateJSON(long startTimeGeneral,long timeDetection,String filepath) throws IOException {
		long timeGeneral = (long)(System.currentTimeMillis()-startTimeGeneral)/1000; //Calculating final runtime
		String jsonString = "{\"duration\":"+timeGeneral+"\",\"durationRecognitionOnly\":"+timeDetection+"}";
		File json = new File(filepath + ".json");
		json.createNewFile();
		FileWriter fileWriter = new FileWriter(json);
		fileWriter.write(jsonString);
		fileWriter.close();
		System.out.println("JSON File angelegt: "+json.getPath());
	}
	

	/**
	 * Gets image dimensions for given file 
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
	  int pos = imgFile.getName().lastIndexOf(".");
	  if (pos == -1)
	    throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
	  String suffix = imgFile.getName().substring(pos + 1);
	  Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	  while(iter.hasNext()) {
	    ImageReader reader = iter.next();
	    try {
	      ImageInputStream stream = new FileImageInputStream(imgFile);
	      reader.setInput(stream);
	      int width = reader.getWidth(reader.getMinIndex());
	      int height = reader.getHeight(reader.getMinIndex());
	      stream.close();
	      return new Dimension(width, height);
	    } catch (IOException e) {
	      System.out.println("Error reading: " + imgFile.getAbsolutePath());
	    } finally {
	      reader.dispose();
	    }
	  }
	
	  throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
	}
}
