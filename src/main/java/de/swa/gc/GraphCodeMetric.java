package de.swa.gc;

import java.util.HashSet;
import java.util.Vector;
import java.util.stream.Collectors;

/** Implementation of the Graph Code Metric
 * 
 * @author stefan_wagenpfeil
 */

public class GraphCodeMetric {

	public static float[] calculateSimilarities(Vector<GraphCode> gcs) {
		float node_metric = 0f;
		Vector<String> dictionary = new Vector<>(gcs.get(0).getDictionary());
		HashSet<String> strings = new HashSet<>();
		for(GraphCode gc : gcs) {
			strings.addAll(gc.getDictionary());
			dictionary.retainAll(gc.getDictionary());
		}
		node_metric = (float) dictionary.size() / strings.size();
		return new float[] {node_metric};
	}
	/** calculates the metric triple for Graph Codes based on a given query **/
	public static float[] calculateSimilarity(GraphCode gcQuery, GraphCode gc) {
		float node_metric = 0f;
		float edge_metric = 0f;
		float edge_type_metric = 0f;
		
		// node metric checks matching vocabulary terms
		Vector<String> voc = gcQuery.getDictionary();
		int sim = 0;
		for (String s : voc) {
			if (s.trim().equals("")) continue;
			Vector<String> otherDict = gc.getDictionary();
			for (String t : otherDict) {
				if (s.equals(t)) sim ++;
//				if (s.indexOf(t) >= 0) { 
//					sim ++;
//				}
//				else if (t.indexOf(s) >= 0) {
//					sim ++; 
//				}
			}
		}
		if (sim > voc.size()) sim = voc.size();
		node_metric = (float)sim / (float)voc.size();
		
		// edge metric checks matching edges of the non diagonal fields
		// edge type metric checks for corresponding type values
		int num_of_non_zero_edges = 0;
		int edge_metric_count = 0;
		int edge_type = 0;
		for (int i = 0; i < voc.size(); i++) {
			for (int j = 0; j < voc.size(); j++) {
				if (i != j) {
					if (gcQuery.getValue(i, j) != 0) {
						num_of_non_zero_edges ++;
						try {
							int gc_edge = gc.getEdgeValueForTerms(voc.get(i), voc.get(j));
							if (gc_edge != 0) edge_metric_count ++;
							if (gc_edge == gcQuery.getValue(i, j)) edge_type ++;
						}
						catch (Exception x) {
							x.printStackTrace();
						}
					}
				}
			}
		}
		if (num_of_non_zero_edges > 0) edge_metric = (float)edge_metric_count / (float)num_of_non_zero_edges;
		if (edge_metric_count > 0) edge_type_metric = (float)edge_type / (float)edge_metric_count;

		return new float[] {node_metric, edge_metric, edge_type_metric};
	}

	/** Calculates the graph density metric **/
	public static float calculateDensity(GraphCode gc) {
		int numNodes = gc.getDictionary().size();
		int numEdges = 0;

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				if (i != j && gc.getValue(i, j) != 0) {
					numEdges++;
				}
			}
		}

		int maxPossibleEdges = numNodes * (numNodes - 1);
		return (float) numEdges / maxPossibleEdges;
	}

	/** Calculates the clustering coefficient metric **/
	public static float calculateClusteringCoefficient(GraphCode gc) {
		int numNodes = gc.getDictionary().size();
		int numTriangles = 0;

		for (int i = 0; i < numNodes; i++) {
			Vector<Integer> neighbors = new Vector<>();
			for (int j = 0; j < numNodes; j++) {
				if (i != j && gc.getValue(i, j) != 0) {
					neighbors.add(j);
				}
			}

			int numNeighbors = neighbors.size();
			if (numNeighbors >= 2) {
				int numConnectedPairs = 0;
				for (int k = 0; k < numNeighbors - 1; k++) {
					for (int l = k + 1; l < numNeighbors; l++) {
						if (gc.getValue(neighbors.get(k), neighbors.get(l)) != 0) {
							numConnectedPairs++;
						}
					}
				}
				numTriangles += numConnectedPairs;
			}
		}

		int maxPossibleTriangles = numNodes * (numNodes - 1) * (numNodes - 2) / 6;
		return (float) numTriangles / maxPossibleTriangles;
	}

	/** Calculates the PageRank metric **/
	public static float[] calculatePageRank(GraphCode gc, float dampingFactor, int numIterations) {
		int numNodes = gc.getDictionary().size();
		float[] pageRank = new float[numNodes];
		float[] newPageRank = new float[numNodes];

		// Initialize PageRank values
		for (int i = 0; i < numNodes; i++) {
			pageRank[i] = 1.0f / numNodes;
		}

		// Iteratively calculate PageRank
		for (int iteration = 0; iteration < numIterations; iteration++) {
			for (int i = 0; i < numNodes; i++) {
				newPageRank[i] = (1 - dampingFactor) / numNodes;

				for (int j = 0; j < numNodes; j++) {
					if (i != j && gc.getValue(j, i) != 0) {
						newPageRank[i] += dampingFactor * (pageRank[j] / outDegree(gc, j));
					}
				}
			}

			// Update PageRank values
			System.arraycopy(newPageRank, 0, pageRank, 0, numNodes);
		}

		return pageRank;
	}

	// Helper method to calculate out-degree of a node
	private static int outDegree(GraphCode gc, int nodeIndex) {
		int outDegree = 0;
		int numNodes = gc.getDictionary().size();

		for (int i = 0; i < numNodes; i++) {
			if (gc.getValue(nodeIndex, i) != 0) {
				outDegree++;
			}
		}

		return outDegree;
	}
}
