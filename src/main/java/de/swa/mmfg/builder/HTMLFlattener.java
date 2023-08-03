package de.swa.mmfg.builder;

import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

/** exports a MMFG as HTML **/

public class HTMLFlattener implements Flattener{
	public String flatten(MMFG fv) {
		StringBuffer sb = new StringBuffer();
		for (Node n : fv.getNodes()) process(n, sb, "");
		return sb.toString();
	}
	
	private void process(Node n, StringBuffer sb, String offset) {
		sb.append("<br>" + offset + "Node: " + n.getName());
		for (Node ni : n.getChildNodes()) process(ni, sb, offset + "&nbsp;&nbsp;");
	}

	public String getFileExtension() {
		return "html";
	}
	public String startFile() { return ""; }
	public String endFile() { return ""; }

}
