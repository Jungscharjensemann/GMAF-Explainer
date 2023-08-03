package de.swa.ui.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** panel that shows the logging console **/
public class LogPanel extends JPanel {
	private JEditorPane log = new JEditorPane();
	private static LogPanel currentInstance = new ConsoleLogger();
	
	public LogPanel() {
		log.setPreferredSize(new Dimension(80,80));
		currentInstance = this;
		setLayout(new GridLayout(1,1));
		JScrollPane sp = new JScrollPane(log);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(sp);
	}
	
	public static LogPanel getCurrentInstance() {
		return currentInstance;
	}
	
	public void addToLog(String text) {
		Date d = new Date();
		DateFormat df = DateFormat.getDateInstance();
		DateFormat tf = DateFormat.getTimeInstance();
		
		String logRecord = df.format(d) + " " + tf.format(d) + "   " + text;
		String t = log.getText() + "\n" + logRecord;
		log.setText(t);
		System.out.println(logRecord);
	}
}

class ConsoleLogger extends LogPanel {
	public void addToLog(String text) {
		Date d = new Date();
		DateFormat df = DateFormat.getDateInstance();
		DateFormat tf = DateFormat.getTimeInstance();
		
		String logRecord = df.format(d) + " " + tf.format(d) + "   " + text;
		System.out.println(logRecord);
	}
}
