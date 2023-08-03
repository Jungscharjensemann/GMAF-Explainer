package de.swa.fuh.qrefinement.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.swa.fuh.qrefinement.command.AccentuateRectangleCommand;
import de.swa.fuh.qrefinement.command.DeleteRectangleCommand;
import de.swa.fuh.qrefinement.command.RelevanceMarkCommandHistory;
import de.swa.fuh.qrefinement.observer.ImageObservers;
import de.swa.fuh.qrefinement.observer.UpdateImageMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.table.ButtonRenderer;
import de.swa.fuh.qrefinement.ui.table.LabelRenderer;

/**
 * 
 * 
 * Class implementing a table to show an overview of all relevance marks for the image the user currently works on.
 * The removing of relevance marks is also triggered in this table by the user.
 * 
 * @author Nicolas Boch
 *
 */
public class ImageRefinementTable extends JTable implements ListSelectionListener, UpdateImageMarks{

	/**
	 * Scroll pane to place the table in
	 */
	private JScrollPane pane;
	
	/**
	 * File reference to the image file of the image the user currently works on
	 */
	private File f;
	
	/**
	 * class variable for single instance of this class. Implements singleton pattern
	 */
	private static ImageRefinementTable instance;
	
	/**
	 * Table model defining the editable cells
	 */
	private DefaultTableModel model = new DefaultTableModel ()
			{
				public boolean isCellEditable (int row, int column)
				{
					return column == 6;
				}
			};
	private DefaultListSelectionModel listmodel = new DefaultListSelectionModel();
	
	public static ImageRefinementTable getInstance()
	{
		return instance;
	}
	
	public ImageRefinementTable ()
	{
		instance = this;
		f = RefinementUIModel.getInstance().getSelectedAsset();
		ImageObservers.getInstance().subscribe(this);
		model.addColumn("Rect No.");
		model.addColumn("Horizontal start point");
		model.addColumn("Vertical start point");
		model.addColumn("Width");
		model.addColumn("Height");
		model.addColumn("Color");
		model.addColumn("Actions");
		this.setModel(model);
		listmodel.addListSelectionListener(this);
		this.setSelectionModel(listmodel);
		this.getColumn("Color").setCellRenderer(new LabelRenderer());
		this.getColumn("Actions").setCellRenderer(new ButtonRenderer());
		this.getColumn("Actions").setCellEditor(new ButtonEditor());
		pane = new JScrollPane (this);
	}
	
	public void addRectangle (Rectangle r)
	{
		Object [] row = new Object [7];
		String l = "";
		
		if (r.getColor() == Color.RED)
		{
			l = "red";
		
		}
		if (r.getColor() == Color.GREEN)
		{
			l = "green";
		}
		
		row [0] = model.getRowCount() + 1;
		row [1] = r.getX();
		row [2] = r.getY();
		row [3] = r.getWidth();
		row [4] = r.getHeight();
		row [5] = l;
		row [6] = "";
		
		System.out.println(l);
		
		model.addRow(row);
	}
	
	public void clearTable()
	{
		for (int i = model.getRowCount() - 1; i >= 0; i--)
		{
			model.removeRow(i);
		}
		model.setRowCount(0);
	}
	

	public JScrollPane getPane()
	{
		return pane;
	}
	

	public void refresh ()
	{
		if (de.swa.fuh.qrefinement.qrm.Tools.isImageFile(RefinementUIModel.getInstance().getSelectedAsset()))
		{
		clearTable();
		f = RefinementUIModel.getInstance().getSelectedAsset();
		
		for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
		{
			addRectangle(r);
		}
		}
	}

	
	/**
	 * @author Nicolas Boch
	 * 
	 * Nested class implementing edit components for editable table cells (delete button)
	 *
	 */
	private class ButtonEditor extends DefaultCellEditor implements ActionListener
	{
		public ButtonEditor ()
		{
			super(new JCheckBox());
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) { 
			JButton b = new JButton ("Delete");
			b.setBackground(Color.WHITE);
			b.setActionCommand(String.valueOf(row));
			b.addActionListener(this);
			return b;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//RefinementCollection.getInstance().getRelevanceMarks().get(RefinementUIModel.getInstance().getSelectedAsset()).getRectangles().removeElementAt(Integer.parseInt(e.getActionCommand()));
			stopCellEditing();
			RelevanceMarkCommandHistory.getInstance().add(new DeleteRectangleCommand(f, Integer.parseInt(e.getActionCommand())));
			//Refinement_UI.getInstance().refresh();
			
		}
	}
	
	
	/**
	* Hervorhebung des in der Tabelle markierten Rechtecks
	*/
	public void valueChanged (ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting() && getSelectedRow() != -1)
		{
			
			new AccentuateRectangleCommand(f, getSelectedRow()).execute();
		}		 		 
	}

	/**
	 * Observer Pattern. Reaction to notification about added relevance mark.
	 */
	@Override
	public void addMark(File fileRef, Rectangle mark) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			addRectangle(mark);
		}
		
	}

	/**
	 * Observer pattern. Reaction to notification about state change in relevance marks for an image (e.g. deletion of a relevance mark).
	 */
	@Override
	public void updateImageMark(File fileRef) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			clearTable();
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
			{
				addRectangle(r);
			}
		}
		
	}
	
}
