package de.swa.ui.command;

import java.io.File;

import de.ja.view.ExplainerFrame;
import de.swa.ui.Configuration;
import de.swa.ui.panels.AssetDetailPanel;
import de.swa.ui.panels.AssetListPanel;
import de.swa.ui.panels.RecommendedAssetPanel;
import de.swa.ui.panels.SimilarAssetPanel;

/** class to encapsulate the Select Command **/

public class SelectCommand extends AbstractCommand {
	private File selection;
	
	public SelectCommand(File f) {
		selection = f;
	}
	
	public void execute() {
		Configuration.getInstance().setSelectedAsset(selection);
		AssetListPanel.getCurrentInstance().refresh(false);
		AssetDetailPanel.getCurrentInstance().refresh();
		SimilarAssetPanel.getCurrentInstance().refresh();
		RecommendedAssetPanel.getCurrentInstance().refresh();
	}
}
