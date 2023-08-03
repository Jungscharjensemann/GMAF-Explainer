package de.swa.fuh.explanation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
 *         Button displayed below assets and button for detail panel
 *
 */
public class InfoButton {
	// Button in asset list (below assets) to display ranking information of one
	// result asset
	public static void getInfoButtonJPanel(JPanel icons, String fileName) {
		JButton info = new JButton(new ImageIcon("resources/information_small.png"));
		icons.add(info);
		info.setToolTipText("Press this button to get ranking information.");
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fileGC = new File(
						Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + fileName + ".gc");
				GraphCode gc = GraphCodeIO.read(fileGC);
				GraphCode gcQuery;
				NamesHelper.setSelectedName(fileName);
				MainExplanation mainFrame = new MainExplanation();
				// Check if button is in AssetListPanel (main display) or in
				// RecommendedAssetPanel/SimilarAssetPanel (detail display)
				if (info.getParent().getParent().getParent() instanceof AssetListPanel
						&& BooleanHelper.getRefineButton()) {
					gcQuery = MMFGCollection.getInstance().getCurrentQuery();
					mainFrame.explain(gcQuery, gc, false);
					BooleanHelper.setSelectedOk(true);
				} else if ((info.getParent().getParent().getParent() instanceof RecommendedAssetPanel
						|| info.getParent().getParent().getParent() instanceof SimilarAssetPanel)
						&& !BooleanHelper.getRefineButton()) {
					gcQuery = GraphCodeHelper.getGraphCode();
					mainFrame.explain(gcQuery, gc, false);
					BooleanHelper.setSelectedROk(true);
				} else
					mainFrame.showMessage(
							"<html>Please enter new query or select button for <p> query information when on detail panel.</p></html>");
			}
		});
	}

	// Button in detail panel to display query information
	public static void getInfoButtonJTabbedPane(JTabbedPane tp, File f) {
		JButton info = new JButton(new ImageIcon("resources/information.png"));
		info.setToolTipText("Press this button to get query information.");
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BooleanHelper.setRefineButton(false);
				BooleanHelper.setSelectedOk(false);
				BooleanHelper.setSelectedROk(false);
				BooleanHelper.setIsRefined(false);
				NamesHelper.setQueryName(f.getName());
				File fileGC = new File(
						Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + f.getName() + ".gc");
				GraphCode gc = GraphCodeIO.read(fileGC);
				GraphCodeHelper.setGraphCode(gc);
				MainExplanation mainFrame = new MainExplanation();
				mainFrame.explain(gc);
			}
		});
		tp.addTab("Info", null);
		tp.setTabComponentAt(tp.getTabCount() - 1, info);
	}
}
