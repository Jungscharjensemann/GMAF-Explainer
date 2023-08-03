package de.swa.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class QueryResultPanel extends JPanel {
	private static QueryResultPanel currentInstance;
	private JEditorPane editor = new JEditorPane();
	private JScrollPane pane = new JScrollPane();
	private JTable table;
	
	public QueryResultPanel() {
		currentInstance = this;
		setLayout(new BorderLayout());
		editor.setPreferredSize(new Dimension(100, 100));
	
		add(pane, "Center");
	}
	
	public static QueryResultPanel getCurrentInstance() {
		return currentInstance;
	}
	
	public void displayResult(String result) {
		editor.setText(result);
		pane.setViewportView(editor);
	}
	
	public void setTable(Object[][] data,String[] header) {
		table = new JTable(data,header);
		pane.setViewportView(table);
	}
}
