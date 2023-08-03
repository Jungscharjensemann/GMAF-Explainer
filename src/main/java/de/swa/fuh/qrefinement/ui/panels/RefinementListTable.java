package de.swa.fuh.qrefinement.ui.panels;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.swa.fuh.qrefinement.command.SelectCommand;
import de.swa.fuh.qrefinement.command.SelectQueryCommandHistory;
import de.swa.fuh.qrefinement.observer.ImageObservers;
import de.swa.fuh.qrefinement.observer.TextObservers;
import de.swa.fuh.qrefinement.observer.UpdateImageMarks;
import de.swa.fuh.qrefinement.observer.UpdateTextMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;

/**
 * Table showing all relevance marks that are currently set
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementListTable extends JTable implements ListSelectionListener, UpdateTextMarks, UpdateImageMarks {

	private static RefinementListTable instance;
	private JScrollPane pane;
	private DefaultTableModel model = new DefaultTableModel ();
	private DefaultListSelectionModel listmodel = new DefaultListSelectionModel();
	private Vector<File> files = new Vector <File> ();
	
	public static RefinementListTable getInstance()
	{
		if (instance == null)
		{
			instance = new RefinementListTable ();
		}
		return instance;
	}
	
	private RefinementListTable ()
	{
		ImageObservers.getInstance().subscribe(this);
		TextObservers.getInstance().subscribe(this);
		model.addColumn("Asset");
		model.addColumn("No. relevant marks");
		model.addColumn("No. non relevant marks");
		listmodel.addListSelectionListener(this);
		
		this.setModel(model);
		this.setSelectionModel(listmodel);
		refresh();
		pane = new JScrollPane (this);
	}
	
	public JScrollPane getPane()
	{
		return pane;
	}
	
	public void refresh ()
	{
		model.setRowCount(0);
		files.clear();
		Object [] row = new Object [3];
		int posmark = 0;
		int negmark = 0;
		
		for (File f : RefinementCollection.getInstance().getRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles().size() > 0)
			{
				row[0] = f.getName();
				
				for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
				{
					if (r.getColor() == Color.RED)
					{
						negmark++;
					}
					if (r.getColor() == Color.GREEN)
					{
						posmark++;
					}
				}
				
				row[1] = posmark;
				row[2] = negmark;
				
				model.addRow(row);
				files.add(f);
			}
		}
		
		posmark = 0;
		negmark = 0;
		
		for (File f : RefinementCollection.getInstance().getTextRelevanceMarks().keySet())
		{
			if (RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks().size() > 0)
			{
				row[0] = f.getName();
				
				for (TextMark r : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
				{
					if (r.getColor() == Color.RED)
					{
						negmark++;
					}
					if (r.getColor() == Color.GREEN)
					{
						posmark++;
					}
				}
				
				row[1] = posmark;
				row[2] = negmark;
				
				model.addRow(row);
				files.add(f);
			}
		}	
		
	}
	
	public void valueChanged (ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting() && getSelectedRow() != -1)
		{
			File file = files.get(getSelectedRow());
			
			
			SelectQueryCommandHistory.getInstance().add(new SelectCommand(file));
			/*
			RefinementUIModel.getInstance().setSelectedAsset(file);
			
			for (RefinementPanel p : RefinementAssetListPanel.getCurrentInstance().getButtons())
			{
					if (p.getFile() == RefinementUIModel.getInstance().getSelectedAsset())
					{
						p.getButton().doClick();
					}
					
			}
			*/
			
		}
	}

	private void addFirstMarkForTextFile (File f, TextMark m)
	{
		Object [] row = new Object [3];
		
		row[0] = f.getName();
		
		if (m.getColor() == Color.GREEN)
		{
			row[1] = 1;
			row[2] = 0;
		}
		else
		{
			row[1] = 0;
			row[2] = 1;
		}
		
		model.addRow(row);
	}
	
	private void addFirstMarkForImageFile(File f, Rectangle r)
	{
		Object [] row = new Object [3];
		
		row[0] = f.getName();
		
		if (r.getColor() == Color.GREEN)
		{
			row[1] = 1;
			row[2] = 0;
		}
		else
		{
			row[1] = 0;
			row[2] = 1;
		}
		
		model.addRow(row);
	}
	
	@Override
	public void addMark(File fileRef, TextMark mark) {
		if (!files.contains(fileRef))
		{
			addFirstMarkForTextFile (fileRef, mark);
			files.add(fileRef);
		}
		else
		{
			int index = files.indexOf(fileRef);
			
			if (mark.getColor() == Color.GREEN)
			{
				int oldValue = (int)model.getValueAt(index, 1);
				model.setValueAt(++oldValue, index, 1);
			}
			else
			{
				int oldValue = (int)model.getValueAt(index, 2);
				model.setValueAt(++oldValue, index, 2);
			}
		}
		
	}

	@Override
	public void updateTextMark(File fileRef) {
		int index = files.indexOf(fileRef);
		if (index != -1)
		{
		int oldposmarks = (int)model.getValueAt(index, 1);
		int oldnegmarks = (int)model.getValueAt(index,2);
		int sumoldmarks = oldposmarks + oldnegmarks;
		int newposmarks = 0;
		int newnegmarks = 0;
		
		
		if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().size() != sumoldmarks)
		{
			if (RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks().size() == 0)
			{
				files.remove(fileRef);
				model.removeRow(index);
			}
			else
			{
				for (TextMark m : RefinementCollection.getInstance().getTextRelevanceMarks().get(fileRef).getTextMarks())
				{
					if (m.getColor() == Color.GREEN)
					{
						newposmarks++;
					}
					else
					{
						newnegmarks++;
					}
				}
				
				if (newposmarks != oldposmarks)
				{
					model.setValueAt(newposmarks, index, 1);
				}
				if (newnegmarks != oldnegmarks)
				{
					model.setValueAt(newnegmarks, index, 2);
				}
			}
		}
		}
	}

	@Override
	public void addMark(File fileRef, Rectangle mark) {
		if (!files.contains(fileRef))
		{
			addFirstMarkForImageFile (fileRef, mark);
			files.add(fileRef);
		}
		else
		{
			int index = files.indexOf(fileRef);
			
			if (mark.getColor() == Color.GREEN)
			{
				int oldValue = (int)model.getValueAt(index, 1);
				model.setValueAt(++oldValue, index, 1);
			}
			else
			{
				int oldValue = (int)model.getValueAt(index, 2);
				model.setValueAt(++oldValue, index, 2);
			}
		}
		
	}

	@Override
	public void updateImageMark(File fileRef) {
		int index = files.indexOf(fileRef);
		if (index != -1)
		{
		int oldposmarks = (int)model.getValueAt(index, 1);
		int oldnegmarks = (int)model.getValueAt(index,2);
		int sumoldmarks = oldposmarks + oldnegmarks;
		int newposmarks = 0;
		int newnegmarks = 0;
		
		
		if (RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().size() != sumoldmarks)
		{
			if (RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles().size() == 0)
			{
				files.remove(fileRef);
				model.removeRow(index);
			}
			else
			{
				for (Rectangle m : RefinementCollection.getInstance().getRelevanceMarks().get(fileRef).getRectangles())
				{
					if (m.getColor() == Color.GREEN)
					{
						newposmarks++;
					}
					else
					{
						newnegmarks++;
					}
				}
				
				if (newposmarks != oldposmarks)
				{
					model.setValueAt(newposmarks, index, 1);
				}
				if (newnegmarks != oldnegmarks)
				{
					model.setValueAt(newnegmarks, index, 2);
				}
			}
		}
		}
		
	}
	
}
