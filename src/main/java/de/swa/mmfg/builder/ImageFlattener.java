package de.swa.mmfg.builder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import de.swa.mmfg.CompositionRelationship;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.GeneralMetadata;
import de.swa.mmfg.Location;
import de.swa.mmfg.Node;
import de.swa.mmfg.SemanticRelationship;
import de.swa.mmfg.TechnicalAttribute;
import de.swa.mmfg.Weight;

/** exports a MMFG as Rendered JPEG **/

public class ImageFlattener implements Flattener {
	private static final String font = "Times";
	private static String folder = "";
	
	public String flatten(MMFG fv) {
		BufferedImage bi = new BufferedImage(1000, 20000, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 20000);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		String filename = folder + "fv_" + System.currentTimeMillis() + ".jpg";
		try {
			int offset = paintGeneralMeta(fv, g);
			for (Node n : fv.getNodes()) {
				offset = paintNode(n, g, 0, offset);
			}
			ImageIO.write(bi, "jpg", new File(filename));
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return filename;
	}
	
	public static void setRootFolder(String f) {
		folder = f;
	}
	
	private int paintGeneralMeta(MMFG fv, Graphics2D g) {
		int start_x = 30;
		int start_y = 30;
		g.setColor(Color.BLACK);
		
		g.setFont(new Font(font, Font.BOLD, 12));
		g.drawString("Feature Vector", start_x, start_y);
		start_y += 14;
		g.setFont(new Font(font, Font.PLAIN, 10));
		
		for (Location loc : fv.getLocations()) {
			String sloc = loc.getName() + ": " + loc.getLocation();
			if (sloc.length() > 40) {
				if (loc.getType() == Location.TYPE_HIGHRES) sloc = "HIGHRES " + sloc;
				else if (loc.getType() == Location.TYPE_LOWRES) sloc = "LOWRES " + sloc;
				else if (loc.getType() == Location.TYPE_ORIGINAL) sloc = "ORIGINAL " + sloc;
				else if (loc.getType() == Location.TYPE_ORIGINAL_COPY) sloc = "COPY " + sloc;
				else if (loc.getType() == Location.TYPE_THUMBNAIL) sloc = "THUMBNAIL " + sloc;
				
 				sloc = sloc.substring(0, 15) + "...." + sloc.substring(sloc.length() - 20, sloc.length());
			}
			g.drawString(sloc, 30, start_y);
			start_y += 12;
		}

		g.drawLine(10, start_y, 490, start_y);
		String acl = "";
		String gid = "";
		String uid = "";
		if (fv.getSecurity() != null) {
			acl = fv.getSecurity().getAcl();
			gid = fv.getSecurity().getGroup_id();
			uid = fv.getSecurity().getOwner_id();
		}
		start_y += 12;
		start_y += 12;
		g.drawString("Security ACL: " + acl, 30, start_y);	start_y += 12;
		g.drawString("Security Group: " + gid, 30, start_y);	start_y += 12;
		g.drawString("Security Owner: " + uid, 30, start_y);	start_y += 12;
		
		g.drawRect(10, 10, 480, start_y);
		g.drawLine(490, 50, 520, 50);
		
		int start_y2 = 60;
		int start_x2 = 540;
		
		GeneralMetadata gm = fv.getGeneralMetadata();
		g.setFont(new Font(font, Font.BOLD, 12));
		g.drawString("General Metadata", start_x2, start_y2); start_y2 += 14;
		g.setFont(new Font(font, Font.PLAIN, 10));
		
		g.drawString("Aperture: " + gm.getAperture(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Camera: " + gm.getCameraModel(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("City: " + gm.getCityNearBy(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Exposure: " + gm.getExposure(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("FileSize: " + gm.getFileSize(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("FocalLength: " + gm.getFocalLength(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Height: " + gm.getHeight(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("ISO: " + gm.getIso(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Latitude: " + gm.getLatitude(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("LensModel: " + gm.getLensModel(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Longitude: " + gm.getLongitude(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Resolution: " + gm.getResolution(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("ShutterSpeed: " + gm.getShutterSpeed(), start_x2, start_y2);	start_y2 += 12;
		g.drawString("Width: " + gm.getWidth(), start_x2, start_y2);	
		
		g.drawRect(520, 40, 220, start_y2);
		
		g.drawLine(50, start_y + 12, 50, Math.max(start_y, start_y2) + 30);
		
		return Math.max(start_y, start_y2);
	}
	
	private int paintNode(Node n, Graphics2D g, int x_offset, int y_offset) {
		if (n.getName().equals("")) return y_offset;
		int start_x = 50 + x_offset;
		int start_y = 50 + y_offset;
		g.setColor(Color.BLACK);
		
		// Node Box
		g.setFont(new Font(font, Font.BOLD, 12));
		g.drawString(n.getName(), start_x, start_y);	start_y += 14;
		g.setFont(new Font(font, Font.ITALIC, 10));
		if (n.getDetectedBy() != null) {
			g.drawString(n.getDetectedBy(), start_x, start_y); start_y += 14;
		}
		g.setFont(new Font(font, Font.PLAIN, 10));
		
		g.drawLine(start_x - 20,  start_y - 5,  start_x + 280,  start_y - 5);
		start_y += 11;
		// Weight Section
		for (Weight w : n.getWeights()) {
			g.drawString("w(" + w.getContext().getName() + ") = " + w.getWeight(), start_x, start_y); start_y += 11;
		}
		g.drawLine(start_x - 20,  start_y - 5,  start_x + 280,  start_y - 5);
		start_y += 11;
		for (SemanticRelationship sr : n.getSemanticRelationships()) {
			g.drawString("Semantic: " + sr.getRelatedNode().toString(), start_x, start_y); start_y += 11;
		}
		for (TechnicalAttribute ta : n.getTechnicalAttributes()) {
			String bounds = "x=" + ta.getRelative_x() + ",y=" + ta.getRelative_y() + ",w=" + ta.getWidth() + ",h=" + ta.getHeight();
			g.drawString("Bounds: " + bounds, start_x, start_y); start_y += 11;
			int ic = 0;
			for (Color c : ta.getDominantColors()) {
				ic ++;
				g.setColor(c);
				g.fillRect(start_x + 10*ic, start_y, 10, 10);
			}
		}
		g.drawLine(start_x - 20,  start_y - 5,  start_x + 280,  start_y - 5);
		start_y += 11;
		for (CompositionRelationship cr : n.getCompositionRelationships()) {
			String type = "";
			if (cr.getType() == CompositionRelationship.RELATION_ABOVE) type = "ABOVE -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_ATTACHED_TO) type = "ATTACHED -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_BEFORE) type = "BEFORE -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_BEHIND) type = "BEHIND -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_NEXT_TO) type = "NEXT_TO -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_PART_OF) type = "PART_OF -> " + cr.getRelatedObject().getName();
			if (cr.getType() == CompositionRelationship.RELATION_UNDER) type = "UNDER -> " + cr.getRelatedObject().getName();
			
			g.drawString("Composition: " + type, start_x, start_y); 
		}
		g.drawRect(30 + x_offset, 30 + y_offset, 300, start_y - y_offset - 20);
		

		for (Node ni : n.getChildNodes()) {
			if (!ni.getName().equals("")) {
				start_y = paintNode(ni, g, x_offset + 30, start_y + 40);
				g.drawLine(x_offset + 100, start_y + 10, x_offset + 100, start_y + 70);
			}
		}
		
		return start_y;
	}

	public String getFileExtension() {
		return "jpg";
	}
	public String endFile() { return ""; }
	public String startFile() { return ""; }

}
