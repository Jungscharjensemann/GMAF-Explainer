package de.swa.fuh.qrefinement.command;

import java.awt.Color;
import java.io.File;

import javax.swing.UIManager;

import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.panels.ImageRefinementLabel;
import de.swa.fuh.qrefinement.ui.panels.ImageRefinementTable;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementDetailPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementPanel;
import de.swa.fuh.qrefinement.ui.panels.TextEditor;
import de.swa.fuh.qrefinement.ui.panels.TextRefinementTable;



/**
 * 
 * 
 * This is a command class implementing command pattern. This command selects an asset and emphasized selection in UI
 * 
 * @author Nicolas Boch
 *
 */
public class SelectCommand implements UndoableCommand {

	private File oldSelection;
	private File newSelection;
	
	public SelectCommand (File f)
	{
		this.newSelection = f;
	}
	
	@Override
	public void execute() {
		oldSelection = RefinementUIModel.getInstance().getSelectedAsset();
		
		if (oldSelection.getAbsolutePath().equals(newSelection.getAbsolutePath()))
		{
			throw new IllegalStateException ("Asset already selected");
		}
		RefinementUIModel.getInstance().setSelectedAsset(newSelection);
		for (RefinementPanel p : RefinementAssetListPanel.getCurrentInstance().getButtons())
		{
				if (p.getFile().getAbsolutePath().equals(newSelection.getAbsolutePath()))
				{
					p.getButton().setBackground(Color.LIGHT_GRAY);
					//p.grabFocus();
				}
				
					
				else
				{
				p.getButton().setBackground(UIManager.getColor ( "Button.background" ));
				}
		}
		
		RefinementDetailPanel.getCurrentInstance().refresh();
		ImageRefinementLabel.getInstance().refresh();
		ImageRefinementTable.getInstance().refresh();
		TextEditor.getInstance().refresh();
		TextRefinementTable.getInstance().refresh();

	}

	@Override
	public void undo() {
		RefinementUIModel.getInstance().setSelectedAsset(oldSelection);
		for (RefinementPanel p : RefinementAssetListPanel.getCurrentInstance().getButtons())
		{
				if (p.getFile().getAbsolutePath().equals(oldSelection.getAbsolutePath()))
				{
					p.getButton().setBackground(Color.LIGHT_GRAY);
				}
				
					
				else
				{
				p.getButton().setBackground(UIManager.getColor ( "Button.background" ));
				}
		}
		
		RefinementDetailPanel.getCurrentInstance().refresh();
		ImageRefinementLabel.getInstance().refresh();
		ImageRefinementTable.getInstance().refresh();
		TextEditor.getInstance().refresh();
		TextRefinementTable.getInstance().refresh();

	}

	@Override
	public void redo() {
		RefinementUIModel.getInstance().setSelectedAsset(newSelection);
		for (RefinementPanel p : RefinementAssetListPanel.getCurrentInstance().getButtons())
		{
				if (p.getFile().getAbsolutePath().equals(newSelection.getAbsolutePath()))
				{
					p.getButton().setBackground(Color.LIGHT_GRAY);
				}
								
				else
				{
				p.getButton().setBackground(UIManager.getColor ( "Button.background" ));
				}
		}
		
		RefinementDetailPanel.getCurrentInstance().refresh();
		ImageRefinementLabel.getInstance().refresh();
		ImageRefinementTable.getInstance().refresh();
		TextEditor.getInstance().refresh();
		TextRefinementTable.getInstance().refresh();

	}

}
