package de.swa.fuh.opencv;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.TechnicalAttribute;

/**
 * Helper class to generate with OpenCV DNN Module results.
 * @author Tobias Lukoschek
 */
public class OpenCVObjectDetector {
	/** The classifications provided from yolov3. */
	private static final String[] cocoNames = new String[] { "person", "bicycle", "car", 
			"motorbike", "aeroplane", "bus", "train", "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", 
			"bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "elephant", "bear", "zebra", "giraffe", "backpack", 
			"umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat", 
			"baseball glove", "skateboard" , "surfboard", "tennis racket", "bottle", "wine glass" , "cup", "fork", "knife", "spoon" ,
			"bowl", "banana", "apple", "sandwich" , "orange", "broccoli", "carrot", "hot dog" , "pizza", "donut", "cake", "chair" , 
			"sofa", "pottedplant", "bed", "diningtable" , "toilet", "tvmonitor", "laptop", "mouse" , "remote" , "keyboard" , 
			"cell phone" , "microwave" , "oven" , "toaster" , "sink" , "refrigerator" , "book" , "clock", "vase" , "scissors", 
			"teddy bear" , "hair drier", "toothbrush" };
	
	/** Path to the resources folder to access the model data. */
	private String mAbsolutePath; 
	/** OpenCV class {@link Net} work with neuronal networks. */
	private Net mNet;
	/** Data working class of OpenCV which holds the image. */ 
	private Mat frame;
	
	/**
	 * Load OpenCV native library. 
	 * 
	 * @implNote must be called before using any class of the modul. 
	 */
	public static void loadOpenCV() {
		System.loadLibrary("opencv_java453");
	}
	
	/** 
	 * Get the absolute path of the src/main/resources folder and save it in mAbsolutePath.
	 */
	public void loadResourceLocation() {
		URL res = getClass().getClassLoader().getResource("yolov3.cfg");
		try {
			File file = Paths.get(res.toURI()).toFile();
			mAbsolutePath = file.getAbsolutePath();
			mAbsolutePath = mAbsolutePath.replace("yolov3.cfg", "");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			mAbsolutePath = "";
		}
	}
	
	/** 
	 * Loads the model yolov3 as the neuronal network to use.
	 */
	public void loadModel() {
		mNet = Dnn.readNetFromDarknet(mAbsolutePath + "yolov3.cfg", mAbsolutePath + "yolov3.weights");
		mNet.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
	}
	
	/** 
	 * Loading the image in the working data class of OpenCV. 
	 * @param path Path to the image file. 
	 */
	public void loadImageFromPath(final String path) {
		frame = Imgcodecs.imread(path);
	}
	
	/** 
	 * Generates the detections of the model. The nodes contains the classification name specified in
	 * cocoNames and the position in the TechnicalAttribute. 
	 * @param fv holds the node where to save the detections. 
	 * @param detectedNodes also save the nodes in this vector to make it compatible with 
	 * the interface of {@link GMAF_Plugin}.
	 * @param confThreshold sets the threshold which evaluates of a detection is enough sure.
	 */
	public void addDetectionsToNode(MMFG fv, Vector<Node> detectedNodes, float confThreshold) {
		Node currentNode = fv.getCurrentNode();
	    Size frame_size = new Size(416, 416);
	    Scalar mean = new Scalar(127.5);
	    List<Mat> result = new ArrayList<>();
	    List<String> outBlobNames = mNet.getUnconnectedOutLayersNames();

	    Mat blob = Dnn.blobFromImage(frame, 1.0 / 255.0, frame_size, mean, true, false);
	    mNet.setInput(blob);
	    mNet.forward(result, outBlobNames);

	    for (int i = 0; i < result.size(); ++i) {
	        Mat level = result.get(i);
	        for (int j = 0; j < level.rows(); ++j) {
	            Mat row = level.row(j);
	            Mat scores = row.colRange(5, level.cols());
	            Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
	            float confidence = (float) mm.maxVal;
	            Point classIdPoint = mm.maxLoc;
	            if (confidence > confThreshold) {

	                int centerX = (int) (row.get(0, 0)[0] * frame.cols());
	                int centerY = (int) (row.get(0, 1)[0] * frame.rows());
	                int width = (int) (row.get(0, 2)[0] * frame.cols());
	                int height = (int) (row.get(0, 3)[0] * frame.rows());

	                int left = (int) (centerX - width * 0.5);
	                int top =(int)(centerY - height * 0.5);
	                int right =(int)(centerX + width * 0.5);
	                int bottom =(int)(centerY + height * 0.5);

	                int class_id = (int) classIdPoint.x;
	                Node n = new Node(cocoNames[class_id], fv);
	                n.addTechnicalAttribute(new TechnicalAttribute(left, top, left- right, top - bottom, 1.0f, 0.0f));
	                
	    			detectedNodes.add(n);
	    			currentNode.addChildNode(n);
	            }
	        }
	    }
		
	}
}
