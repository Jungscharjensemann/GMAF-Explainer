package de.swa.gc;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.poi.ss.formula.functions.T;

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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String s = gson.toJson(this);
		return s;
	}

	public String printGC(boolean pretty) {
		Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
		return gson.toJson(this);
	}

	public String formattedTerms() {
		TreeSet<Integer> uniqueValues = new TreeSet<>();
		for (int[] ints : matrix) {
			for (int anInt : ints) {
				uniqueValues.add(anInt);
			}
		}
		String formattedTerms = uniqueValues.stream()
				.map(this::formattedTerms)
				.collect(Collectors.joining(","));
		return formattedTerms;
	}

	public String formattedTerms2() {
		TreeSet<Integer> uniqueValues = new TreeSet<>();
		for(int[] ints : matrix) {
			for(int i : ints) {
				uniqueValues.add(i);
			}
		}

		TreeMultimap<Integer, Multimap<Integer, Integer>> treeMultimap;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				// Diagonale ignorieren
				if(i != j) {
					Multimap<Integer, Integer> m = ArrayListMultimap.create();
				}
			}
		}

		// TreeSet<Einzigartiger Wert, ArrayList(MultiMap<Index des Terms, Werte>)>
		return null;
	}

	public String getFormattedTerms2() {
		List<String> formats = new ArrayList<>();
		for(int row = 0; row < matrix.length; row++) {
			int[] rowArr = matrix[row];
			// MultiMap sortiert nach:
			// 1. Schlüssel (0,1,...)
			// 2. Reihenfolge des Hinzufügens
			Multimap<Integer, String> m = MultimapBuilder.treeKeys().arrayListValues().build();
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
			m.asMap().forEach((k, v) -> {
				if(k == 1) {
					formatsInRow.add(String.format("<%s> - <%s>",
							finalRow + 1,
							v.stream()
									.map(s -> String.valueOf(dictionary.indexOf(s) + 1))
									.collect(Collectors.joining(",")))
					);
				} else if(k > 1) {
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
		return !formats.isEmpty() ? String.join(",", formats) : "";
	}

	public String getFormattedTerms() {
		Table<String, String, Integer> table = ArrayTable.create(dictionary, dictionary);
		for(String s : dictionary) {
			for(String t : dictionary) {
				table.put(s, t, getEdgeValueForTerms(s, t));
			}
		}

		// Für jeden Eintrag (Reihe) durchlaufen...
		// Dann sollte für jede Reihe die darin vorkommenden einzigartigen Werte gesammelt werden (TreeSet)
		// Für jeden einzigartigen Wert dann Einträge sammeln...
		// Nach Werten differenzen (wenn 1, dann -, sonst <...> Wert <...,...,...>
		// Diese gesammelten Einträge dann verbinden
		// Format erstellen
		// Formate verbinden...

		Set<Map.Entry<String, Map<String, Integer>>> entries = table.rowMap().entrySet();
		/*for(Map.Entry<String, Map<String, Integer>> row : entries) {
			String key = row.getKey();
			Map<String, Integer> columnEntry = row.getValue();
			// Einzigartige Werte bestimmen...
			TreeSet<Integer> uniqueValues = new TreeSet<>(columnEntry.values());

			for(Integer i : uniqueValues) {
				List<Map.Entry<String, Integer>> um = columnEntry.entrySet()
						.stream()
						.filter(column -> !column.getKey().equalsIgnoreCase(key)
										&& Objects.equals(column.getValue(), i))
						.collect(Collectors.toList());
				if(Objects.equals(i, 1)) {
					String format = String.format(
							"<%s> - <%s>", dictionary.indexOf(key) + 1, um.stream()
									.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
									.collect(Collectors.joining(","))
					);
					sb.append(format);
				} else if(i > 1){
					if(!um.isEmpty()) {
						String format = String.format(
								"<%s> %s <%s>", dictionary.indexOf(key) + 1, i, um.stream()
										.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
										.collect(Collectors.joining(","))
						);
						sb.append(format);
					}
				}
			}

			String formats = uniqueValues.stream().map(i -> {
				List<Map.Entry<String, Integer>> um = columnEntry.entrySet()
						.stream()
						.filter(column -> !column.getKey().equalsIgnoreCase(key)
								&& Objects.equals(column.getValue(), i))
						.collect(Collectors.toList());
				if(Objects.equals(i, 1)) {
					return String.format(
							"<%s> - <%s>", dictionary.indexOf(key) + 1, um.stream()
									.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
									.collect(Collectors.joining(","))
					);
				} else if(i > 1){
					if(!um.isEmpty()) {
						return String.format(
								"<%s> %s <%s>", dictionary.indexOf(key) + 1, i, um.stream()
										.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
										.collect(Collectors.joining(","))
						);
					}
				}
				return null;
			}).filter(s -> s != null && !s.isEmpty())
					.collect(Collectors.joining(","));
			//System.out.println(formats);
		}*/

		return entries.stream().map(row -> {
			String key = row.getKey();
			Map<String, Integer> columnEntry = row.getValue();
			TreeSet<Integer> uniqueValues = new TreeSet<>(columnEntry.values());
			return uniqueValues.stream().map(i -> {
						List<Map.Entry<String, Integer>> um = columnEntry.entrySet()
								.stream()
								.filter(column -> !column.getKey().equalsIgnoreCase(key)
										&& Objects.equals(column.getValue(), i))
								.collect(Collectors.toList());
						if(Objects.equals(i, 1)) {
							return String.format(
									"<%s> - <%s>", dictionary.indexOf(key) + 1, um.stream()
											.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
											.collect(Collectors.joining(","))
							);
						} else if(i > 1){
							if(!um.isEmpty()) {
								return String.format(
										"<%s> %s <%s>", dictionary.indexOf(key) + 1, i, um.stream()
												.map(a -> String.valueOf(dictionary.indexOf(a.getKey()) + 1))
												.collect(Collectors.joining(","))
								);
							}
						}
						return "";
					}).filter(s -> s != null && !s.isEmpty())
					.collect(Collectors.joining(","));
		})
				.filter(s -> !s.isEmpty())
				.collect(Collectors.joining(","));
	}

	public String formattedTerms(int value) {
		Multimap<String, String> m = getTermsWithValue(value);
		Map<String, Collection<String>> map = m.asMap();


		/*return map.entrySet().
				stream().
				map(entry -> String.format(
						"<%s> - <%s>",
						entry.getKey(),
						String.join(",", entry.getValue())))
				.collect(Collectors.joining(","));*/
		return map.entrySet().
				stream().
				map(entry -> String.format(
						"<%s> - <%s>",
						dictionary.indexOf(entry.getKey()) + 1,
						entry.getValue().stream().map(t -> String.valueOf(dictionary.indexOf(t) + 1)).collect(Collectors.joining(","))))
				.collect(Collectors.joining(","));
	}

	public String listTerms() {
		return String.join(",", dictionary);
	}

	public String reducedString() {
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(gson.toJson(this), JsonElement.class);
		jsonElement.getAsJsonObject().remove("collectionElements");
		jsonElement.getAsJsonObject().remove("matrix");
		return gson.toJson(jsonElement);
	}
}
