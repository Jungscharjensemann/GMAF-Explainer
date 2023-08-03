package de.swa.fuh.explanation;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Setting and getting boolean variables
 *
 */
public class BooleanHelper {

	private static boolean selectedOk = false; // For main panel in GMAF
	private static boolean selectedROk = false;// For detail panel in GMAF
	private static boolean refineButton = false;
	private static boolean isRefined = false;

	public static void setSelectedOk(Boolean selectedOk) {
		BooleanHelper.selectedOk = selectedOk;
	}

	public static boolean getSelectedOk() {
		return selectedOk;
	}

	public static void setSelectedROk(Boolean selectedROk) {
		BooleanHelper.selectedROk = selectedROk;
	}

	public static boolean getSelectedROk() {
		return selectedROk;
	}

	public static void setRefineButton(Boolean refineButton) {
		BooleanHelper.refineButton = refineButton;
	}

	public static boolean getRefineButton() {
		return refineButton;
	}

	public static boolean getIsRefined() {
		return isRefined;
	}

	public static void setIsRefined(Boolean isRefined) {
		BooleanHelper.isRefined = isRefined;
	}
}
