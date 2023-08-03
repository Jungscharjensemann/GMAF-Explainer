package de.swa.fuh.explanation;

/**
 * 
 * @author Annelie Lausch 6953476
 *
 *         Setter and getter for the names of the assets
 *
 *         uncut names are needed for file access shorted names for display in
 *         same way as in GMAF
 */
public class NamesHelper {
	private static String queryName;
	private static String selectedName;
	private static String chosenName;
	private static String queryNameCut;
	private static String selectedNameCut;
	private static String chosenNameCut;

	/**
	 * Same method/code used in GMAF to display shorted name
	 * 
	 */
	private static String cutName(String name) {
		String ext = name.substring(name.lastIndexOf("."), name.length());
		if (name.length() > 20) {
			name = name.substring(0, 10) + ".." + ext;
		}
		return name;
	}

	public static void setQueryName(String queryName) {
		NamesHelper.queryName = queryName;
	}

	public static String getQueryName() {
		return queryName;
	}

	public static String getQueryNameCut() {
		queryNameCut = cutName(queryName);
		return queryNameCut;
	}

	public static void setSelectedName(String selectedName) {
		NamesHelper.selectedName = selectedName;
	}

	public static String getSelectedName() {
		return selectedName;
	}

	public static String getSelectedNameCut() {
		selectedNameCut = cutName(selectedName);
		return selectedNameCut;
	}

	public static void setChosenName(String chosenName) {
		NamesHelper.chosenName = chosenName;
	}

	public static String getChosenName() {
		return chosenName;
	}

	public static String getChosenNameCut() {
		chosenNameCut = cutName(chosenName);
		return chosenNameCut;
	}

}
