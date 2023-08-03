package de.swa.fuh.qrefinement.ui.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * Container class for detail panel of currently selected asset and table of all relevance marks
 * 
 * @author Nicolas Boch
 *
 */
public class DetailRelevanceMarksPanel extends JPanel {

	private static DetailRelevanceMarksPanel instance;
	private RefinementListTable table;
	private RefinementDetailPanel dpanel;
	
	public static DetailRelevanceMarksPanel getInstance()
	{
		if (instance == null)
		{
			instance = new DetailRelevanceMarksPanel();
		}
		return instance;
	}
	
	private DetailRelevanceMarksPanel ()
	{
		table = RefinementListTable.getInstance();
		dpanel = new RefinementDetailPanel();
		setLayout(new BorderLayout(5,5));
		table.getPane().getViewport().setView(table);
		add(dpanel, "North");
		//add(new JPanel().add(table.getPane()), "South");
		
		
		
	}
	
}