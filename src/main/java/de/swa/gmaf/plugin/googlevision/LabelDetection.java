package de.swa.gmaf.plugin.googlevision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;

import de.swa.mmfg.CompositionRelationship;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.ui.panels.LogPanel;

/** Label Detection Plugin based on the GoogleVision API 
 * 
 * @author stefan_wagenpfeil
 */

public class LabelDetection extends GoogleVisionBasePlugin {
	protected Feature getSearchFeature() {
		Feature f = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
		return f;
	}

	protected void processResult(AnnotateImageResponse res, MMFG fv) {
		for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
			Node n = fv.getCurrentNode();
			String txt = annotation.getDescription();
			LogPanel.getCurrentInstance().addToLog("----- LABEL DETECTED: " + txt);
			Node cn = new Node(txt, fv);
			cn.addTechnicalAttribute(getBoundingBox(annotation.getBoundingPoly()));
			n.addChildNode(cn);
			cn.setDetectedBy(this.getClass().getName());
			cn.addCompositionRelationship(new CompositionRelationship(CompositionRelationship.RELATION_PART_OF, n));
		}
	}
	
	public boolean providesRecoursiveData() {
		return false;
	}
}
