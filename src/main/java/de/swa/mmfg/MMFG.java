package de.swa.mmfg;

import java.net.URL;
import java.util.Vector;

/** data type to represent the MMFG **/
public class MMFG {
	private Vector<Node> nodes = new Vector<Node>();
	private GeneralMetadata generalMetadata;
	private Security security;
	private Vector<Location> locations = new Vector<Location>();
	private Node currentNode;
	private transient float[] similarity = new float[] {0f, 0f, 0f};
	private transient float[] tempSimilarity = new float[] {0f, 0f, 0f};
	
	public Vector<Node> allNodes = new Vector<Node>();
	private Vector<MMFG> collectionElements = new Vector<MMFG>();
	
	public MMFG () {}
	
	public MMFG (MMFG m)
	{
		this.nodes = m.getNodes();
		this.generalMetadata = m.getGeneralMetadata();
		this.security = m.getSecurity();
		this.locations = m.getLocations();
		this.currentNode = m.getCurrentNode();
		this.similarity = m.getSimilarity();
		this.tempSimilarity = m.getTempSimilarity();
	}

	public Vector<Node> getNodesByTerm(String term) {
		Vector<Node> result = new Vector<Node>();
		for (Node n : nodes) {
			if (n.getName().equals(term)) {
				result.add(n);
				Vector<TechnicalAttribute> ta = n.getTechnicalAttributes();
				for (TechnicalAttribute t : ta) {
					System.out.println("TA: " + t.getWidth());
				}
			}
			for (Node ni : n.getChildNodes()) {
				if (ni.getName().equals(term) && !result.contains(ni)) result.add(ni);
			}
		}
		return result;
	}
	
	public void addNode(Node n) {
		nodes.add(n);
		allNodes.add(n);
	}

	public Vector<Node> getNodes() {
		return nodes;
	}
	
	public Vector<Node> getAllNodes() {
		return allNodes;
	}
	
	public void addToCollection(MMFG m) {
		collectionElements.add(m);
	}
	
	public Vector<MMFG> getCollectionElements() {
		return collectionElements;
	}

	public void addLocation(Location loc) {
		for (Location l : locations) {
			if (l.getName().equals(loc.getName()))
				return;
		}
		locations.add(loc);
	}

	public Vector<Location> getLocations() {
		return locations;
	}

	public GeneralMetadata getGeneralMetadata() {
		return generalMetadata;
	}

	public void setGeneralMetadata(GeneralMetadata generalMetadata) {
		this.generalMetadata = generalMetadata;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public float[] getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float[] similarity) {
		this.similarity = similarity;
	}

	public float[] getTempSimilarity() {
		return tempSimilarity;
	}

	public void setTempSimilarity(float[] similarity) {
		this.tempSimilarity = similarity;
	}
	
}
