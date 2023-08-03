package de.swa.gmaf;

import de.swa.gmaf.api.GMAF_Facade;
import de.swa.gmaf.api.GMAF_UI_Facade;

public class SessionFactory {
	public static GMAF_Facade api = null;
	public static GMAF_UI_Facade ui = null;
	public static String sessionId = "";
}
