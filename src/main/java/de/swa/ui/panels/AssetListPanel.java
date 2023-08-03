package de.swa.ui.panels;

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;

import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;

/** panel that shows the list of assets **/
public class AssetListPanel extends JPanel {
	private static AssetListPanel currentInstance;
	public static AssetListPanel getCurrentInstance() {
		return currentInstance;
	}
	
	public AssetListPanel() {
		currentInstance = this;
		refresh();
	}

	private Vector<MMFGPanel> buttons = new Vector<MMFGPanel>();
	
	public void refresh() {
		refresh(true);
	}
	
	public void refresh(boolean fromModel) {
		if (fromModel) buttons = getAssetButtons();
		int rows = (int)(buttons.size() / 5);
		
		setVisible(false);
		removeAll();
		setLayout(new GridLayout(rows + 1, 5, 3, 3));
		
		for (MMFGPanel mp : buttons) add(mp);
		setVisible(true);
	}
	
	private Vector<MMFGPanel> getAssetButtons() {
		Vector<MMFGPanel> result = new Vector<MMFGPanel>();
		
		Vector<MMFG> collection = MMFGCollection.getInstance().getCollection();
		for (MMFG m : collection) {
			float[] sim = m.getTempSimilarity();
			if (sim[0] == 0.0f && sim[1] == 0.0f && sim[1] == 0.0f && MMFGCollection.isQuery) {
				MMFGPanel mp = new MMFGPanel(m, false, false);
				result.add(mp);
			}
			else {
				MMFGPanel mp = new MMFGPanel(m);
				result.add(mp);
			}
		}

		return result;
	}
}
