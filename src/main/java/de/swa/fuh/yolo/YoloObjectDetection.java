package de.swa.fuh.yolo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;
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
 * 
 * This Class acts as a Plugin to implement object recognition,
 * using the YOLOv3 convolutional neural network, into the GMAF 
 * Framework. 
 *
 * Prerequisites for the usage of the Plugin (as detailed in installation.pdf) are:
 * - An installation of the OpenCV 4.52 Library on the using machine
 * - Inclusion of the OpenCV 4.52 Library into the GMAF Project Buildpath
 * - Folder /yolo/ containing provided yolo_class.names, yolo.cfg and
 *   yolo.weights files
 * - de.fuh.fpss21.group4.YoloObjectDetection added to GMAF plugin.config 
 *   
 *  
 * @author Christoph Kieloch
 *
 */

public class YoloObjectDetection implements GMAF_Plugin {

	private Vector<Node> detectedNodes = new Vector<Node>();
	
	// 20210619 (Christoph Kieloch): initial creation as Webcam application
	// 20210704 (Christoph Kieloch): implementation of image file input
	// 20210711 (Christoph Kieloch): switch from standalone application to GMAF method implementation
	// 20210725 (Christoph Kieloch): fixed bugs in Mat handling that led to "dims >= 2" error 
	
	@Override
	public boolean canProcess(String arg0) {
		if (arg0.toLowerCase().endsWith("png")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean providesRecoursiveData() {
		return false;
	}

	@Override
	public boolean isGeneralPlugin() {
		return false;
	}

	@Override
	public Vector<Node> getDetectedNodes() {
		return detectedNodes;
	}

/**
 * An implementation of GMAF_Plugin process method for use with the YOLOv3 CNN.
 * 
 * @param fv	Prepared feature vector which is passed from the GMAF Framework. Found child nodes will be attached here
 * @param f		Pointer to the image file. Required! Only way to pass an image to the plugin in this implementation
 * @param url	This way of passing an image file is not supported by the implementation and will result in 0 objects found
 * @param bytes	This way of passing an image file is not supported by the implementation and will result in 0 objects found
 *  
 */
	@Override
	public void process(URL url, File f, byte[] bytes, MMFG fv) {

		try {

			Node currentNode = fv.getCurrentNode();

			// Setting parameters for YOLO Network explained in the comments
			Size imgsize = new Size(608, 608); // Size of input expected by the network
			Scalar mean = new Scalar(0, 0, 0); // Mean normalization, value which is subtracted from every pixel in the
												// image per RGB Channel
			double scalefactor = (double) 1 / (double) 255; // Scaling normalization factor applied to image after mean
															// subtraction
			float confidenceThreshold = (float) 0.5; // Parameter for minimum confidence required to further consider a
														// potential object detection
			float nmsThreshold = (float) 0.3; // Parameter for non - maximum - suppression (deleting of weaker
												// confidence bounding boxes within other boxes)

			// Reading in class names from a text file (in this example COCO Dataset
			// classes, which the network was pre-trained with)
			List<String> coconames = new ArrayList<String>();
			File n = new File("yolo/yolo_class.names");
			try (BufferedReader br = new BufferedReader(new FileReader(n))) {
				String line;
				while ((line = br.readLine()) != null) {
					coconames.add(line);
				}
			}

			// Initializing the YOLO Neural Network via OpenCV 4.52 Library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Net yolo = Dnn.readNetFromDarknet("yolo//yolo.cfg", "yolo//yolo.weights");
			yolo.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
			yolo.setPreferableTarget(Dnn.DNN_TARGET_CPU);

			// Image file passed by GMAF is read into a Mat data type. Mat (Matrix) is a
			// native data type for the handling of images in the OpenCV Library
			Mat image = Imgcodecs.imread(f.getPath());

			// The raw image needs to be transformed into a so called "Blob" Matrix for use
			// with the YOLO Network
			Mat blob = Dnn.blobFromImage(image, scalefactor, imgsize, mean, true, false);
			yolo.setInput(blob);

			// The YOLO recognition/classificatione pass is started by providing the
			// (unconnected = not connected with further convolutional layers) output
			// layers.
			List<String> outputLayers = yolo.getUnconnectedOutLayersNames();
			List<Mat> outputs = new ArrayList<>();
			yolo.forward(outputs, outputLayers);

			List<Float> confidences = new ArrayList<>();
			List<Rect2d> bboxes = new ArrayList<>();
			List<Integer> classIds = new ArrayList<>();

			// Object candidate finding loop: Sift actual recognitions from the generated
			// outputs
			for (int i = 0; i < outputs.size(); i++) {
				Mat output = outputs.get(i); // Select output matrix from available Scales, ...

				for (int j = 0; j < output.rows(); j++) { // ... for each output matrix row (each matrix rows represents
															// one bounding box with a potential recognition), ...
					Mat currentRow = output.row(j);
					Mat scores = currentRow.colRange(5, currentRow.cols()); // ... select a subset of the columns. This
																			// subset contains the confidences of
																			// detection of a specific class within the
																			// bounding box
					Float maxConf = (float) Core.minMaxLoc(scores).maxVal;
					if (maxConf > confidenceThreshold) { // If the confidence of detection of a certain class is above
															// the threshold, ...
						double width = (currentRow.get(0, 2)[0] * image.cols());
						double height = (currentRow.get(0, 3)[0] * image.rows());
						double x = ((currentRow.get(0, 0)[0] * image.cols()) - width / 2);
						double y = ((currentRow.get(0, 1)[0] * image.rows()) - height / 2);
						bboxes.add(new Rect2d(x, y, width, height)); // ... add the bounding box, confidence, and
																		// related class ID to list of detections
						confidences.add(maxConf);
						classIds.add((int) Core.minMaxLoc(scores).maxLoc.x);
					}
				}
			}

			// Candidate Overlapping Boxes Elimination Loop ("Non Maximum Suppression") -
			// only started if bounding boxes found
			if (bboxes.isEmpty() == false) {
				MatOfInt indices = new MatOfInt();
				MatOfRect2d bboxesMat = new MatOfRect2d();
				MatOfFloat confidencesMat = new MatOfFloat();
				bboxesMat.fromList(bboxes);
				confidencesMat.fromList(confidences);

				Dnn.NMSBoxes(bboxesMat, confidencesMat, confidenceThreshold, nmsThreshold, indices);

				// Create new Subnodes for each found object and add them to the current Feature
				// Graph
				int[] indicesArray = indices.toArray();
				for (int i = 0; i < indicesArray.length; i++) {
					String classname = coconames.get(classIds.get(indicesArray[i]));
					Node newSubnode = new Node(classname, fv);

					// Add Bounding Box technical attribute
					Rect2d bbox = bboxes.get(indicesArray[i]);
					System.out.println(bbox.x + " / " + bbox.y);
					TechnicalAttribute ta = new TechnicalAttribute((int) bbox.x, (int) bbox.y, (int) bbox.width,
							(int) bbox.height, 1.0f, 0.0f);
					newSubnode.addTechnicalAttribute(ta);

					currentNode.addChildNode(newSubnode);
					detectedNodes.add(newSubnode);

					// Diagnosis text
					System.out.println("[YoloPlugin] I sent to GMAF a new subnode for an object type/coords: "
							+ classname + " / " + ta.getRelative_x() + " , " + ta.getRelative_y());
				}
			} else // (if no bounding boxes with objects discovered)
			{
				// Diagnosis text
				System.out.println("[YoloPlugin] I found no new objects / subnodes for current image");
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("An unknown exception occured in YoloObjectDetection Plugin.");
		}
	}
}
