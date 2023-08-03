package de.swa.fuh.explanation;

/**
 * 
 * @author Annelie Lausch 6953476
 *
 *         Setter and getter for numbers needed for comparison of two result
 *         assets
 */
public class NumberHelper {
	private static int numberFeaturesForC;
	private static int numberPairsForC;

	public static void setNumberFeaturesForC(int numberFeaturesForC) {
		NumberHelper.numberFeaturesForC = numberFeaturesForC;
	}

	public static int getNumberFeaturesForC() {
		return numberFeaturesForC;
	}

	public static void setNumberPairsForC(int numberPairsForC) {
		NumberHelper.numberPairsForC = numberPairsForC;
	}

	public static int getNumberPairsForC() {
		return numberPairsForC;
	}
}
