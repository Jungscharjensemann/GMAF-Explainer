package de.swa.mmfg.builder;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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

/** exports a MMFG as Neo4J Script **/

public class Neo4JFlattener implements Flattener {
//	private Statement stmt;

	public Neo4JFlattener() {
		init();
//		try {
//			Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost/test2?username=neo4j,password=stw476.log");
//			stmt = con.createStatement();
//
//		}
//		catch (Exception x) {
//			x.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
	}
	
	// CALL gds.nodeSimilarity.stream('mmfvg')
	
	public String flatten(MMFG fv) {
		String neo4j = getNeo4JString(fv);
		try {
//			stmt.execute(neo4j);
			System.out.println("written to neo4j");
//			stmt.execute("CALL gds.graph.drop('mmfvg') YIELD graphName;");
//			stmt.execute("CALL " + createGraph );
			neo4j = neo4j.substring(0, neo4j.length() - 1) + ";";
		}
		catch (Exception x) {
			x.printStackTrace();
		}

		return neo4j;
	}
	
	private Vector<String> nodeStrings;
	
	private RandomAccessFile nodeFile, linkFile, finalFile;
	
	public void init() {
		try {
			nodeFile = new RandomAccessFile("cipher_tmp_n.neo4j", "rw");
			linkFile = new RandomAccessFile("cipher_tmp_l.neo4j", "rw");
			nodeFile.setLength(0);
			linkFile.setLength(0);
			nodeStrings = new Vector<String>();
			allNodes = new Vector<Node>();
			written = new Vector<String>();
		}
		catch (Exception x) {}
	}
	
	public String getCipherScript() {
		try {
			nodeFile.close();
			linkFile.close();
			finalFile = new RandomAccessFile("cipher_tmp_h10.neo4j", "rw");
			
			StringBuffer content = new StringBuffer();
			nodeFile = new RandomAccessFile("cipher_tmp_n.neo4j", "r");
			String line = "";
			content.append("CREATE \n");
			finalFile.writeBytes("CREATE \n"); 
			while ((line = nodeFile.readLine()) != null) {
				content.append(line + "\n");
				finalFile.writeBytes(line + "\n");
			}
			nodeFile.close();
			
			content.append("\n");
			linkFile = new RandomAccessFile("cipher_tmp_l.neo4j", "r");
			while ((line = linkFile.readLine()) != null) {
				content.append(line + "\n");
				finalFile.writeBytes(line + "\n");
			}

			return content.toString();
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}
	
	private String image = "";
	private Vector<Node> allNodes = new Vector<Node>();
	private String category = "";
	
	private String getNeo4JString(MMFG fv) {
		allNodes.clear();

		for (Location loc : fv.getLocations()) {
			if (loc.getType() == Location.TYPE_ORIGINAL) {
				if (loc.getLocation() == null) {
					image = "IMG_" + loc.getName();
					writeNode(image, "Image", image);
				}
			}
		}

		// Basic nodes
		for (Node n : fv.getNodes()) {
			process(n, allNodes);
		}
		if (allNodes.size() < 50) category = "C50";
		else if (allNodes.size() < 100) category = "C100";
		else if (allNodes.size() < 200) category = "C200";
		else category = "C500";

		writeNode(category, "Category", category);
		writeLink(category, image, "cat");
		
		// Directly Attached objects to node
		Node root = null;
		
		for (Node n : allNodes) {
			Vector<SemanticRelationship> sr = n.getSemanticRelationships();
			Vector<AssetLink> al = n.getAssetLinks();
			Vector<TechnicalAttribute> ta = n.getTechnicalAttributes();
			Vector<CompositionRelationship> cr = n.getCompositionRelationships();
			Vector<Weight> ws = n.getWeights();
			if (n.getName().equals("Root-Asset")) {
				root = n;
				writeNode("Root_Image_" + image, "Node", "Root_Image");
			}

			for (SemanticRelationship s : sr) {
				writeNode(s.getRelatedNode().toString(), "Node", s.getRelatedNode().toString());
				writeLink(s.getRelatedNode().toString(), n.getName(), "sr");
			}
			for (AssetLink a : al) {
				writeNode(a.getLocation().toString(), "Node", a.getLocation().toString());
				writeLink(a.getLocation().toString(), n.getName(), "al");
			}

			for (TechnicalAttribute t : ta) {
				String bb = "BOX_" + t.getRelative_x() + "_" + t.getRelative_y() + "_" + t.getWidth() + "_" + t.getHeight();
				writeNode(bb, "Node", "bb");
				writeLink(bb, n.getName(), "ta");
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

				writeNode(type, "Node", type);
				writeLink(type, n.getName(), "cr");
			}

			for (Weight w : ws) {
				writeNode("C_" + w.getContext().getName(), "Node", "C_" + w.getContext().getName());
				writeLink("C_" + w.getContext().getName(), n.getName(), "w");
			}
		}
		
		GeneralMetadata gm = fv.getGeneralMetadata();
		writeNode("GeneralMeta", "Node", "GeneralMeta");
		writeLink("Root_Image_" + image, "GeneralMeta", "content");
		
		Vector<Location> locs = fv.getLocations();
		for (Location l : locs) {
			writeNode(l.getName(), "Node", l.getName());
			writeLink(l.getName(), "Root_Image_" + image, "loc");
		}

		for (Node ni : root.getChildNodes())
			writeHierarchy(ni, "Root_Image_" + image);
		
		return "";
	}
	
	private void writeHierarchy(Node n, String parent) {
		writeLink(parent, n.getName(), "cn");
		for (Node ni : n.getChildNodes()) {
			writeHierarchy(ni, n.getName());
		}
	}

	private Vector<String> written = new Vector<String>();
	
	private void writeNode(String n, String t, String l) {
		if (!written.contains(n)) {
			try {
				String nn = xmlEncode(l);
				String addLabel = "";
				if (n.startsWith("Root_Image")) addLabel = ", image:'" + image + "'";
				nodeFile.writeBytes("(" + xmlEncode(n) + ":" + t + " {name:'" + nn + "'" + addLabel + "}),\n");
				writeLink(image, n, "img");
				written.add(n);
			}
			catch (Exception x) {
				System.out.println("Exc: " + x);
			}
		}
	}
	
	private void writeLink(String source, String target, String type) {
		try {
			linkFile.writeBytes("(" + xmlEncode(source) + ")-[:" + type + "]->(" + xmlEncode(target) + "),\n");
		}
		catch (Exception x) {
			System.out.println("Exc: " + x);
		}
	}
		
	/*
	+ "  (trumpet1:Instrument {name: 'Trumpet'}),\n"
	+ "\n"
	+ "  (alice1)-[:LIKES]->(guitar1),\n"
*/
	private void process(Node n, Vector<Node> allNodes) {
		if (!allNodes.contains(n)) {
			allNodes.add(n);
			String node = n.getName();
			if (!nodeStrings.contains(xmlEncode(node))) {
				nodeStrings.add(xmlEncode(node));
				writeNode(node, "Node", node);
				writeLink(image, node, "img");
			}
			
			for (Node ni : n.getChildNodes()) {
				process(ni, allNodes);
			}
		}
	}
	
	private String xmlEncode(String n) {
		String s = n;
		s = s.replace('.', '_');
		s = s.replace(',', '_');
		s = s.replace(';', '_');
		s = s.replace('-', '_');
		s = s.replace(' ', '_');
		s = s.replace('&', '_');
		s = s.replace('+', '_');
		s = s.replace(':', '_');
		s = s.replace('/', '_');
		s = s.replace('\n', '_');
		s = s.replace('\t', '_');
		s = s.replace('\'', '_');
		s = s.replaceAll("[^-_/.,\\p{L}0-9 ]+","");
		s = "N_" + s;
		
		try {
			s = new String(s.getBytes(), "UTF8");
		} catch (UnsupportedEncodingException e) {
		}
		return s;
	}	
	
	public String getFileExtension() {
		return "neo4j";
	}
	public String endFile() { return ""; }
	public String startFile() { return ""; }

	
	/*
	 * 

MATCH (r1:Node {name: 'N_Root_Image'})-[*..100]->(child1)
WITH r1, collect(id(child1)) AS r1Child
MATCH (c:Category {name:"N_C50"})-[:cat]->(i:Image)-[:img]->(r2:Node {name: 'N_Root_Image'})-[*..100]->(child2) WHERE r1 <> r2
WITH r1, r1Child, r2, collect(id(child2)) AS r2Child
RETURN r1.image AS from, r2.image AS to, gds.alpha.similarity.jaccard(r1Child, r2Child) AS similarity	

MATCH (r1:Node {name: 'N_Root_Image_1'})-[*..100]->(child1)
WITH r1, collect(id(child1)) AS r1Child
MATCH (i:Image)-[:img]->(r2:Node {name: 'N_Root_Image'})-[*..100]->(child2) WHERE r1 <> r2
WITH r1, r1Child, r2, collect(id(child2)) AS r2Child
RETURN r1.image AS from, r2.image AS to, gds.alpha.similarity.jaccard(r1Child, r2Child) AS similarity	


MATCH (n)
DETACH DELETE n

	 */
}
