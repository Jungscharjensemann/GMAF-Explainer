package de.swa.ui.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JPanel;

import de.swa.gc.GraphCode;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;

/** panel that shows the similarity **/
public class SimilarAssetPanel extends JPanel {
	private static SimilarAssetPanel instance;
	public SimilarAssetPanel() {
		instance = this;
		setPreferredSize(new Dimension(120, 120));
		refresh();
	}
	
	public void refresh() {
		setVisible(false);
		removeAll();
		
		try {
			File selection = Configuration.getInstance().getSelectedAsset();
			MMFG mmfg = MMFGCollection.getInstance().getMMFGForFile(selection);
			GraphCode gc = MMFGCollection.getInstance().getOrGenerateGraphCode(mmfg);
			Vector<MMFG> sim = MMFGCollection.getInstance().getSimilarAssets(gc);
			
			setLayout(new GridLayout(3, 3));
			for (int i = 0; i < 9; i++) {
				MMFGPanel mp = new MMFGPanel(sim.get(i), true);
				add(mp);
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		setVisible(true);
	}
	
	public static SimilarAssetPanel getCurrentInstance() {
		return instance;
	}
}
