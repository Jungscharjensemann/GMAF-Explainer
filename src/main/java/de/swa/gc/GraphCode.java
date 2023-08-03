package de.swa.gc;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/** Implementation of a Graph Code as a 2D representation of MMIR features 
 * 
 * @author stefan_wagenpfeil
 */

public class GraphCode implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Vector<String> dictionary = new Vector<String>();
	protected Vector<GraphCode> collectionElements = new Vector<GraphCode>();
	
	public GraphCode() {}

	protected int[][] matrix;

	/** the Graph Code Dictionary contains a list of feature vocabulary terms **/
	public Vector<String> getDictionary() {
		Vector<String> dict = new Vector<String>();
		for (String s : dictionary) {
			s = s.toLowerCase();
			if (s.length() == 1) continue;
			if (!dict.contains(s)) dict.add(s.toLowerCase());
		}
		return dict;
	}
	
	/** adds a Graph Code to a collection **/
	public void addGraphCode(GraphCode gc) {
		collectionElements.add(gc);
	}
	
	/** returns the elements of a Graph Code collection **/
	public Vector<GraphCode> getCollectionElements() {
		return collectionElements;
	}

	/** sets the dictionary of the Graph Code, i.e. the list of feature vocabulary terms **/
	public void setDictionary(Vector<String> d) {
		dictionary = new Vector<String>();
		for (String s : d) {
			s = s.toLowerCase();
			dictionary.add(s.toLowerCase());
		}
		matrix = new int[d.size()][d.size()];
	}
	
	/** returns, if the Graph Code is part of a collection or a single Graph Code **/
	public boolean isCollection() {
		return collectionElements.size() != 0;
	}

	/** returns the matrix value on position x and y **/
	public int getValue(int x, int y) {
		return matrix[x][y];
	}

	/** sets the matrix value of position x and y **/
	public void setValue(int x, int y, int v) {
		matrix[x][y] = v;
	}
	
	/** returns the matrix value for two feature vocabulary terms **/
	public int getEdgeValueForTerms(String term1, String term2) {
		int translated_x = dictionary.indexOf(term1.toLowerCase());
		int translated_y = dictionary.indexOf(term2.toLowerCase());
		try {
			return matrix[translated_x][translated_y];
		}
		catch (Exception ex) {
			//System.out.println("T1: " + term1 + " (" + translated_x + ")   T2: " + term2 + " (" + translated_y + ")");
			return 0;
		}
	}
	
	/** sets the matrix value for two feature vocabulary terms **/
	public void setValueForTerms(String term1, String term2, int val) {
		int idx_a = dictionary.indexOf(term1.toLowerCase());
		int idx_b = dictionary.indexOf(term2.toLowerCase());
		matrix[idx_a][idx_b] = val;
//		System.out.println(idx_a + " " + idx_b + " (" + term1 + "), (" + term2 + ") -> " + val);
	}

	public Multimap<String, String> getTermsWithValue(int value) {
		Multimap<String, String> multiMap = ArrayListMultimap.create();
		for(int i = 0; i < dictionary.size(); i++) {
			for(int j = 0; j < dictionary.size(); j++) {
				if(matrix[i][j] == value) {
					multiMap.put(dictionary.get(i), dictionary.get(j));
				}
			}
		}
		return multiMap;
	}
	
	/** returns a JSon representation of this Graph Cocde **/
	public String toString() {
		Gson gson = new Gson();
		String s = gson.toJson(this);
		return s;
	}

	public String formattedTerms(int value) {
		Multimap<String, String> m = getTermsWithValue(value);
		Map<String, Collection<String>> map = m.asMap();

		return map.entrySet().stream().map(entry -> String.format("<%s> - <%s>", entry.getKey(), String.join(",", entry.getValue()))).collect(Collectors.joining(","));
	}

	public String reducedString() {
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(gson.toJson(this), JsonElement.class);
		jsonElement.getAsJsonObject().remove("collectionElements");
		jsonElement.getAsJsonObject().remove("matrix");
		return gson.toJson(jsonElement);
	}
}
