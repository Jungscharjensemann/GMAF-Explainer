package de.swa.mmfg.builder;

import java.util.UUID;
import java.util.Vector;

import de.swa.mmfg.AssetLink;
import de.swa.mmfg.CompositionRelationship;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.GeneralMetadata;
import de.swa.mmfg.Location;
import de.swa.mmfg.Node;
import de.swa.mmfg.SemanticRelationship;
import de.swa.mmfg.TechnicalAttribute;
import de.swa.mmfg.Weight;

/** exports a MMFG as Graph ML **/
public class GraphMLFlattener implements Flattener {
	/*
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <graphml
	 * xmlns="http://graphml.graphdrawing.org/xmlns"
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns
	 * http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd"> <graph id="G"
	 * edgedefault="undirected"> <node id="A"/> <node id="B"/> <node id="C"/> <node
	 * id="D"/> <edge id="ab" source="A" target="B"/> <edge id="bc" source="B"
	 * target="C"/> <edge id="cd" source="C" target="D"/> <edge id="da" source="D"
	 * target="A"/> </graph> </graphml>
	 * 
	 */

	public String flatten(MMFG fv) {
		StringBuffer sb = new StringBuffer();

		// detect all nodes
		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<graphml xmlns='http://graphml.graphdrawing.org/xmlns' xmlns:java='http://www.yworks.com/xml/yfiles-common/1.0/java' xmlns:sys='http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0' xmlns:x='http://www.yworks.com/xml/yfiles-common/markup/2.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:y='http://www.yworks.com/xml/graphml' xmlns:yed='http://www.yworks.com/xml/yed/3' xsi:schemaLocation='http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd'>\n");
		sb.append("<key for='node' id='d1' yfiles.type='nodegraphics'/>\n");
		sb.append("<key for='edge' id='e1' yfiles.type='edgegraphics'/>\n");
		sb.append("<graph id='GMAF_Feature_Vector' edgedefault='undirected'>\n");

		Vector<Node> nodes = fv.getNodes();
		Vector<Node> allNodes = new Vector<Node>();

		StringBuffer linkBuffer = new StringBuffer();

		// Basic nodes
		for (Node n : fv.getNodes()) {
			process(n, sb, allNodes);
		}

		// Directly Attached objects to node
		Node root = null;
		
		for (Node n : allNodes) {
			Vector<SemanticRelationship> sr = n.getSemanticRelationships();
			Vector<AssetLink> al = n.getAssetLinks();
			Vector<TechnicalAttribute> ta = n.getTechnicalAttributes();
			Vector<CompositionRelationship> cr = n.getCompositionRelationships();
			Vector<Weight> ws = n.getWeights();
			String nn = xmlEncode(n.getName());
			if (nn.equals("Root-Image") || nn.equals("Root-Image")) root = n;

			for (SemanticRelationship s : sr) writeNodeAndEdge(sb, linkBuffer, s.getRelatedNode().toString(), "#54E0A3", "sr", "#54E0A3", xmlEncode(n.getName()));
			for (AssetLink a : al) writeNodeAndEdge(sb, linkBuffer, a.getLocation().toString(), "#E0DC54", "al", "#E0DC54", xmlEncode(n.getName()));

			for (TechnicalAttribute t : ta) {
				String bb = "BOX_" + t.getRelative_x() + "_" + t.getRelative_y() + "_" + t.getWidth() + "_" + t.getHeight();
				writeNodeAndEdge(sb, linkBuffer, bb, "#E0A954", "ta", "#E0A954", xmlEncode(n.getName()));
			}

			for (CompositionRelationship c : cr) {
				String type = "";
				if (c.getType() == CompositionRelationship.RELATION_ABOVE) type = "above";
				else if (c.getType() == CompositionRelationship.RELATION_ATTACHED_TO) type = "attached to";
				else if (c.getType() == CompositionRelationship.RELATION_BEFORE) type = "before";
				else if (c.getType() == CompositionRelationship.RELATION_BEHIND) type = "behind";
				else if (c.getType() == CompositionRelationship.RELATION_NEXT_TO) type = "next to";
				else if (c.getType() == CompositionRelationship.RELATION_PART_OF) type = "part of";
				else if (c.getType() == CompositionRelationship.RELATION_UNDER) type = "under";
				
				String newId = writeNodeAndEdge(sb, linkBuffer, type, "#E09BFE", "cr", "#E09BFE", xmlEncode(n.getName()));
				try {
					linkBuffer.append("<edge id='" + newId + "' source='" + newId + "' target='"
						+ xmlEncode(c.getRelatedObject().getName()) + "'>\n");
					linkBuffer.append(formatEdge("cr", "#E09BFE"));
					linkBuffer.append("</edge>\n");
				}
				catch (Exception x) {}
			}

			for (Weight w : ws) writeNodeAndEdge(sb, linkBuffer, w.getContext().getName() + ":" + w.getWeight(), "#FE9BEC", "w", "#FE9BEC", xmlEncode(n.getName()));

			for (Node ni : n.getChildNodes()) {
				String id = UUID.randomUUID().toString();
				linkBuffer.append("<edge id='" + id + "' source='" + xmlEncode(n.getName()) + "' target='"
						+ xmlEncode(ni.getName()) + "'>\n");
				linkBuffer.append(formatEdge("child", "#000000"));
				linkBuffer.append("</edge>\n");
			}
		}
		
		GeneralMetadata gm = fv.getGeneralMetadata();
		String fvid = writeNodeAndEdge(sb, linkBuffer, gm.getId().toString(), "#FA9BEC", "content", "#FA9BEC", "Root-Image");
		String gmid = writeNodeAndEdge(sb, linkBuffer, gm.getId().toString(), "#FA9BEC", "gm", "#FA9BEC", fvid);
		
		writeGMNode("Camera", "" + gm.getCameraModel(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getCityNearBy(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getLensModel(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getAperture(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getDate(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getFileSize(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getFocalLength(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getExposure(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getHeight(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getIso(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getLatitude(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getLongitude(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getResolution(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getShutterSpeed(), sb, linkBuffer, gmid);
		writeGMNode("Camera", "" + gm.getWidth(), sb, linkBuffer, gmid);

		Vector<Location> locs = fv.getLocations();
		for (Location l : locs) {
			writeNodeAndEdge(sb, linkBuffer, l.getName(), "#FA9BEC", "location", "#FA9BEC", fvid);
		}

		sb.append(linkBuffer.toString());
		sb.append("</graph></graphml>");
		return sb.toString();
	}
	
	private String writeNodeAndEdge(StringBuffer sb, StringBuffer linkBuffer, String node, String nodeColor, String edge, String edgeColor, String sourceNodeId) {
		String thisId = UUID.randomUUID().toString();
		sb.append("<node id='" + thisId + "'>\n");
		sb.append(formatNode(node, nodeColor));
		sb.append("</node>");
		
		linkBuffer.append("<edge id='e_" + thisId + "' source='" + sourceNodeId + "' target='" + thisId + "'>\n");
		linkBuffer.append(formatEdge(edge, edgeColor));
		linkBuffer.append("</edge>\n");
		return thisId;
	}
	
	private void writeGMNode(String name, String value, StringBuffer sb, StringBuffer linkBuffer, String gm_node) {
		if (!value.equals("")) {
			sb.append("<node id='GM_" + name + "'>\n");
			sb.append(formatNode(name + "=" + value, "#A7C4DE"));
			sb.append("</node>\n");
			
			linkBuffer.append("<edge id='gmloc_" + name + "' source='" + gm_node + "' target='GM_" + name + "'>\n");
			linkBuffer.append(formatEdge("Attribute", "#000000"));
			linkBuffer.append("</edge>\n");
		}
	}

	private void process(Node n, StringBuffer sb, Vector<Node> allNodes) {
		if (!allNodes.contains(n)) {
			allNodes.add(n);
			sb.append("<node id='" + xmlEncode(n.getName()) + "'>\n");
			sb.append(formatNode(xmlEncode(n.getName()), "#33AFFF"));
			sb.append("</node>\n");
			
			for (Node ni : n.getChildNodes()) {
				process(ni, sb, allNodes);
			}
		}
	}
	
	private String xmlEncode(String n) {
		String s = n;
		s = s.replaceAll("&", "+");
		s = s.replaceAll(" ", "_");
		s = s.replaceAll("\n", "_");
		s = s.replace("<", "_");
		s = s.replace(">", "_");
		s = s.replace("/", "_");
		s = s.replace("\"", "_");
		s = s.replace("'", "_");
		s = s.replace("=", "_");		
		return s;
	}
	
	private String formatEdge(String name, String color) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("		<data key='e1'>\n");
		sb.append("		<y:PolyLineEdge>\n");
		sb.append("			<y:Path sx='0.0' sy='0.0' tx='0.0' ty='0.0'/>\n");
		sb.append("			<y:LineStyle color='" + color + "' type='line' width='1.0'/>\n");
		sb.append("			<y:Arrows source='none' target='standard'/>\n");
		sb.append("			<y:EdgeLabel alignment='center'>" + name + "</y:EdgeLabel>\n");
		sb.append("		</y:PolyLineEdge>\n");
		sb.append(" </data>\n");

		
		return sb.toString();
	}
	
	private String formatNode(String name, String color) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("		<data key='d1'>\n");
		sb.append("		<y:ShapeNode>\n");
		sb.append("			<y:Geometry height='25.0' width='80.0'/>\n");
		sb.append("			<y:Fill color='" + color + "' transparent='false'/>\n");
		sb.append("			<y:BorderStyle color='#000000' type='line' width='1.0'/>\n");
		sb.append("			<y:NodeLabel>" + name + "</y:NodeLabel>\n");
		sb.append("		</y:ShapeNode>\n");
		sb.append(" </data>\n");

		
		return sb.toString();
	}
	
	public String getFileExtension() {
		return "graphml";
	}
	
	public String startFile() { return ""; }
	public String endFile() { return ""; }
}
