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
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.swa.fuh.qrefinement.command.ChangeTextMarkCommand;
import de.swa.fuh.qrefinement.command.DeleteTextMarkCommand;
import de.swa.fuh.qrefinement.command.RelevanceMarkCommandHistory;
import de.swa.fuh.qrefinement.observer.TextObservers;
import de.swa.fuh.qrefinement.observer.UpdateTextMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.fuh.qrefinement.ui.table.ButtonRenderer;
import de.swa.fuh.qrefinement.ui.table.EditTextField;
import de.swa.fuh.qrefinement.ui.table.LabelRenderer;

/**
 * Table showing relevance marks that are set for the currently edited text asset
 * 
 * @author Nicolas Boch
 *
 */
public class TextRefinementTable extends JTable implements ListSelectionListener, UpdateTextMarks{
	private JScrollPane pane;
	private File f;
	private static TextRefinementTable instance;
	private DefaultTableModel model = new DefaultTableModel ()
			{
				public boolean isCellEditable (int row, int column)
				{
					return column == 1 || column == 3;
				}
			};
	private DefaultListSelectionModel listmodel = new DefaultListSelectionModel();
	
	
	public static TextRefinementTable getInstance ()
	{
		return instance;
	}
	
	public TextRefinementTable ()
	{
		instance = this;
		TextObservers.getInstance().subscribe(this);
		model.addColumn("Mark No.");
		model.addColumn("Text");
		model.addColumn("Color");
		model.addColumn("Actions");
		setModel(model);
		listmodel.addListSelectionListener(this);
		setSelectionModel(listmodel);
		getColumn("Mark No.").setMaxWidth(70);
		getColumn("Text").setCellEditor(new TextEditore());
		getColumn("Text").setMinWidth(250);
		getColumn("Color").setCellRenderer(new LabelRenderer());
		getColumn("Color").setMaxWidth(50);
		getColumn("Actions").setCellRenderer(new ButtonRenderer());
		getColumn("Actions").setCellEditor(new ButtonEditor());
		getColumn("Actions").setMaxWidth(70);
		pane = new JScrollPane (this);
	}
	
	public void addTextMark (TextMark m)
	{
		Object [] row = new Object [4];
		
		String l = "";
		String text = "";
		
		if (m.getColor() == Color.RED)
		{
			l = "red";
		}
		else
		{
		if (m.getColor() == Color.GREEN)
		{
			l = "green";
		}
		}
		
		text = TextEditor.getInstance().getText();
		text = text.substring(m.getStart(), m.getEnd());
		
		row [0] = model.getRowCount() + 1;
		row [1] = text;
		row [2] = l;
		row [3] = "";
		
		model.addRow(row);
	}
	
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
			
			stopCellEditing();
			RelevanceMarkCommandHistory.getInstance().add(new DeleteTextMarkCommand(f, Integer.parseInt(e.getActionCommand())));
			
		}
	}
	
	private class TextEditore extends DefaultCellEditor implements CellEditorListener
	{
		private int rowe;
		private EditTextField tf;
		
		public TextEditore ()
		{
			super(new EditTextField());
			tf = (EditTextField) getComponent();
			addCellEditorListener(this);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) { 
			tf.setText((String) value);
			rowe = row;
			
			tf.setRow(rowe);
			
			return (JTextField) tf;
		}
		
		@Override
		public void editingStopped(ChangeEvent e) {
			System.out.println("Editing stopped");
			TextMark m = RefinementCollection.getInstance().getTextRelevanceMarks().get(RefinementUIModel.getInstance().getSelectedAsset()).getTextMarks().get(rowe);
			
			String s = tf.getText();
			
			String ref = TextEditor.getInstance().getText().substring(m.getStart(), m.getEnd());
			
			RelevanceMarkCommandHistory.getInstance().add(new ChangeTextMarkCommand(f,rowe, m.getStart() + ref.indexOf(s), m.getStart() + ref.indexOf(s) + s.length()));		
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			// TODO Auto-generated method stub
			
		}
		
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
		if (!de.swa.fuh.qrefinement.qrm.Tools.isImageFile(RefinementUIModel.getInstance().getSelectedAsset()))
		{
			f = RefinementUIModel.getInstance().getSelectedAsset();
			clearTable ();
			for (TextMark m : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
			{
				addTextMark(m);
			}
		}
	}
	
	public void valueChanged (ListSelectionEvent e)
	{
		
	}

	@Override
	public void addMark(File fileRef, TextMark mark) {
		if (f!= null)
		{
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			addTextMark(mark);
		}
		}
		
	}

	@Override
	public void updateTextMark(File fileRef) {
		if (f != null)
		{
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			clearTable();
			for (TextMark mark : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
			{
				addTextMark(mark);
			}
		}
		}
		
	}
	
}
