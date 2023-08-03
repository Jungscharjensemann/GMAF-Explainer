package de.swa.fuh.explanation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeIO;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;
import de.swa.ui.panels.AssetListPanel;
import de.swa.ui.panels.RecommendedAssetPanel;
import de.swa.ui.panels.SimilarAssetPanel;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         MouseListener for choosing second asset for information display
 *
 */
public class MouseListenerForChosenAsset {
	
	/**
	 * 
	 * @param tb       (button for the mouseListener)
	 * @param fileName (name of chosen asset)
	 */
	public static void getMouseListenerForChosenAsset(JButton tb, String fileName) {
		tb.addMouseListener((MouseListener) new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					File fileGC = new File(
							Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + fileName + ".gc");
					GraphCode gc = GraphCodeIO.read(fileGC);
					GraphCode gcQuery;
					MainExplanation mainFrame = new MainExplanation();
					NamesHelper.setChosenName(fileName);
					// Check if mouse is in AssetListPanel (main display) or in
					// RecommendedAssetPanel/SimilarAssetPanel (detail display)
					if (e.getComponent().getParent().getParent() instanceof AssetListPanel
							&& BooleanHelper.getRefineButton() && BooleanHelper.getSelectedOk()) {
						gcQuery = MMFGCollection.getInstance().getCurrentQuery();
						mainFrame.explain(gcQuery, gc, true);
					} else if ((e.getComponent().getParent().getParent() instanceof RecommendedAssetPanel
							|| e.getComponent().getParent().getParent() instanceof SimilarAssetPanel)
							&& !BooleanHelper.getRefineButton() && BooleanHelper.getSelectedROk()) {
						gcQuery = GraphCodeHelper.getGraphCode();
						mainFrame.explain(gcQuery, gc, true);
					} else
						mainFrame.showMessage("No asset to compare to was selected!");
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
	}
}
