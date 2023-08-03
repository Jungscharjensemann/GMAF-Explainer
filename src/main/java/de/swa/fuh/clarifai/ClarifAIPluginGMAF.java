package de.swa.fuh.clarifai;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;

import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Region;

/**
 * <p>This class functions as an API between ClarifAI and GMAF. It is based on the given example RandomObjectDetection.java and
 * enriches a given MMFG with nodes containing objects found through ClarifAI object detection.</p>
 * 
 * <p>As per the signature of method process() the API can accept URL objects, File objects and byte arrays for object detection
 * via GMAF.</p>
 * 
 * @author Julius K�ndiger
 *
 */
public class ClarifAIPluginGMAF implements GMAF_Plugin {
	public boolean canProcess(String extension) {
		if (extension.toLowerCase().endsWith("png"))
			return true;
		return false;
	}

	private Vector<Node> detectedNodes = new Vector<Node>();

	public Vector<Node> getDetectedNodes() {
		return detectedNodes;
	}

	public boolean isGeneralPlugin() {
		return false;
	}

	public void process(URL url, File f, byte[] bytes, MMFG fv) {
		try {
			ClarifAIObjectDetection clarifaiRequest = new ClarifAIObjectDetection();
			File inputFile = f;
			
			List<Region> boundingBoxes = null;
			if(url != null) {
				boundingBoxes = clarifaiRequest.getObjects(url);
				inputFile = new File(url.toURI());
			} else if (f != null) {
				try {
					boundingBoxes = clarifaiRequest.getObjects(f);
					inputFile = f;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (bytes != null) {
				boundingBoxes = clarifaiRequest.getObjects(bytes);
				String filePath = System.getProperty("user.dir")+"temp";
				FileOutputStream fos = new FileOutputStream(filePath);
				fos.write(bytes);
				fos.close();
				inputFile = new File(filePath);
			} 
			
			if(boundingBoxes.isEmpty()) {
				throw new Error();
			} else {
				Vector<String> detectedObjects = new Vector<String>();
				Node currentNode = fv.getCurrentNode();
				Dimension dimension = getImageDimension(inputFile);
				
				for (Region r : boundingBoxes) {
					if(r.hasData()) {
						for(Concept c : r.getData().getConceptsList()) {
							String currentObject = c.getName(); 
							detectedObjects.add(currentObject);
							Node n = new Node(currentObject,fv);
							
							int xValue = (int) (r.getRegionInfo().getBoundingBox().getLeftCol() * dimension.getWidth());
							int yValue = (int) (r.getRegionInfo().getBoundingBox().getTopRow() * dimension.getHeight());
							int widthValue = (int) ((r.getRegionInfo().getBoundingBox().getRightCol() * dimension.getWidth()) 
									- (r.getRegionInfo().getBoundingBox().getLeftCol() * dimension.getWidth()));
							int heightValue = (int) ((r.getRegionInfo().getBoundingBox().getBottomRow() * dimension.getHeight()) 
									- (r.getRegionInfo().getBoundingBox().getTopRow() * dimension.getHeight()));
							
							n.addTechnicalAttribute(new TechnicalAttribute(xValue,yValue,widthValue,heightValue,c.getValue(),0.0f));
							detectedNodes.add(n);
							currentNode.addChildNode(n);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// if bounding boxes can be calculated, each detected object can be processed recursively
	public boolean providesRecoursiveData() {
		return false;
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

