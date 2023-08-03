package de.swa.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.swa.ui.command.ClearQueryCommand;
import de.swa.ui.command.CommandHistory;
import de.swa.ui.command.QueryByKeywordCommand;
import de.swa.ui.command.QueryBySPARQLCommand;
import de.swa.ui.command.QueryInfoCommand;

/** panel that shows the query field **/
public class QueryPanel extends JPanel implements ActionListener {
	private JEditorPane editor = new JEditorPane();
	private static QueryPanel currentInstance;
	
	public QueryPanel() {
		currentInstance = this;
		setLayout(new BorderLayout());
		editor.setPreferredSize(new Dimension(100, 100));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(3,1));
		JButton clear = new JButton(new ImageIcon("resources/document_selection.png"));
		clear.setActionCommand("Clear");
		JButton exec = new JButton(new ImageIcon("resources/gearwheels.png"));
		exec.setActionCommand("Execute");
		JButton info = new JButton(new ImageIcon("resources/information.png"));
		info.setToolTipText("Press this button to get query information.");
		info.setActionCommand("Information");
		clear.addActionListener(this);
		exec.addActionListener(this);
		info.addActionListener(this);
		buttons.add(clear);
		buttons.add(info);
		buttons.add(exec);
		
		JLabel q = new JLabel("Query   ");
		add(q, "West");
		add(new JScrollPane(editor), "Center");
		add(buttons, "East");
	}
	
	public static QueryPanel getCurrentInstance() {
		return currentInstance;
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("Clear")) CommandHistory.getInstance().addCommand(new ClearQueryCommand());
		else if (s.equals("Execute")) {
			String command = editor.getText();
			if(command.startsWith("SELECT") || command.startsWith("ASK") || command.startsWith("CONSTRUCT") || command.startsWith("DESCRIBE") || command.startsWith("PREFIX")) {
				CommandHistory.getInstance().addCommand(new QueryBySPARQLCommand(command));
			} else {
				CommandHistory.getInstance().addCommand(new QueryByKeywordCommand(command));
			}
		}
		else if (s.equals("Information")) CommandHistory.getInstance().addCommand(new QueryInfoCommand());
	}
	
	public void setText(String s) {
		editor.setText(s);
	}
}
