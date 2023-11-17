package de.swa.gc;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
			//if (s.length() == 1) continue;
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

	/** returns a JSon representation of this Graph Cocde **/
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	@SuppressWarnings("unused")
	public String printGC(boolean pretty) {
		Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
		return gson.toJson(this);
	}

	/**
	 * Fügt die Wörter in einem Wörterbuch und die zwischen
	 * diesen bestehenden Beziehungen in einem
	 * bestimmten Format zusammen.
	 * Dieses Format lautet: <i_t> - <i_t1, i_t2, ..., i_tn>
	 * @return Format.
	 */
	public String getFormattedTerms() {
		List<String> formats = new ArrayList<>();
		for(int row = 0; row < matrix.length; row++) {
			int[] rowArr = matrix[row];
			// MultiMap sortiert nach:
			// 1. Schlüssel (0,1,...)
			// 2. Reihenfolge des Hinzufügens
			Multimap<Integer, String> m = MultimapBuilder.treeKeys().arrayListValues().build();
			// Für jeden einzigartigen Beziehungswert in einer Zeile,
			// die entsprechenden Wörter sammeln.
			for(int column = 0; column < rowArr.length; column++) {
				if(column != row) {
					int entry = rowArr[column];
					if(entry > 0) {
						m.put(rowArr[column], dictionary.get(column));
					}
				}
			}
			List<String> formatsInRow = new ArrayList<>();
			int finalRow = row;
			// Format als String formatieren.
			m.asMap().forEach((k, v) -> {
				// Beziehungswert nur 1.
				if(k == 1) {
					formatsInRow.add(String.format("<%s> - <%s>",
							finalRow + 1,
							v.stream()
									.map(s -> String.valueOf(dictionary.indexOf(s) + 1))
									.collect(Collectors.joining(",")))
					);
				}
				// Beziehungswert alles außer 1 und positiv.
				else if(k > 1) {
					formatsInRow.add(String.format("<%s> %s <%s>",
							finalRow + 1,
							k,
							v.stream()
									.map(s -> String.valueOf(dictionary.indexOf(s) + 1))
									.collect(Collectors.joining(",")))
					);
				}
			});
			if(!formatsInRow.isEmpty()) {
				formats.add(String.join(",", formatsInRow));
			}
		}
		// Formate für jede Zeile in einem einzigen Format zusammenfügen.
		return !formats.isEmpty() ? String.join(",", formats) : "";
	}

	/**
	 * Zählt alle Wörter im Wörterbuch auf und
	 * fügt diese mit einem Komma getrennt, zusammen.
	 * @return Zusammengefügtes Wörterbuch.
	 */
	public String listTerms() {
		return String.join(",", dictionary);
	}

	@SuppressWarnings("unused")
	public String reducedString() {
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(gson.toJson(this), JsonElement.class);
		jsonElement.getAsJsonObject().remove("collectionElements");
		jsonElement.getAsJsonObject().remove("matrix");
		return gson.toJson(jsonElement);
	}
}
