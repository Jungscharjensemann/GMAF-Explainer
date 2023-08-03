package de.swa.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.swa.ui.panels.QueryResultPanel;

public class QueryResultFrame extends JFrame {
	private static QueryResultFrame currentInstance;
	
	public QueryResultFrame() {
		currentInstance = this;
		setSize(500, 250);
		//setUndecorated(true);
		
		setTitle("SPARQL Query Results");
		setLayout(new BorderLayout());
		
		QueryResultPanel content = new QueryResultPanel();
		getContentPane().add(content);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setLocation(400, 200);
		setVisible(true);
	}
	
	public void close() {
		setVisible(false);
	}
	
	public static QueryResultFrame getInstance() {
		return currentInstance;
	}

}
