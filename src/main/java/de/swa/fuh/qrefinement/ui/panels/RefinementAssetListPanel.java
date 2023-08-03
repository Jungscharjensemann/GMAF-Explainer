package de.swa.fuh.qrefinement.ui.panels;

import java.awt.GridLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JPanel;

import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.mmfg.MMFG;

/**
 * Container class for thumbnails of all assets in collection
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementAssetListPanel extends JPanel {
	private static RefinementAssetListPanel currentInstance;
	private Vector<RefinementPanel> buttons;
	
	public static RefinementAssetListPanel getCurrentInstance() {
		return currentInstance;
	}
	
	public RefinementAssetListPanel ()
	{
		currentInstance = this;
		refresh();
	}
	
	public void refresh() {
		if (buttons != null)
		{
			buttons.clear();
		}
		else
		{
			buttons = new Vector<RefinementPanel>();
		}
		
		setAssetButtons();
		
		int rows = (int)(buttons.size() / 5);
		
		setVisible(false);
		removeAll();
		setLayout(new GridLayout(rows + 1, 5, 3, 3));
		
		for (RefinementPanel mp : buttons) add(mp);
		setVisible(true);
	}
	
	public Vector<RefinementPanel> getButtons()
	{
		return buttons;
	}
	
	public void resort ()
	{
		Collections.sort(buttons, new Comparator<RefinementPanel>() {
			public int compare (RefinementPanel rf1, RefinementPanel rf2){
				int pos1 = RefinementCollection.getInstance().getCollection().indexOf(rf1.getMMFG());
				int pos2 = RefinementCollection.getInstance().getCollection().indexOf(rf2.getMMFG());
				
				return pos1 - pos2;
			}
		});
		setVisible(false);
		removeAll();
		for (RefinementPanel mp : buttons) add(mp);
		setVisible(true);
		
	}
	
	private void setAssetButtons()
	{
		Vector<MMFG> collection = RefinementCollection.getInstance().getCollection();
		
		for (MMFG m : collection)
		{
			RefinementPanel mp = new RefinementPanel(m);
			buttons.add(mp);
		}
	}
	
}
