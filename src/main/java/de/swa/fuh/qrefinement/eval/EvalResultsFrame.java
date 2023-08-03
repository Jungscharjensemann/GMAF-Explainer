package de.swa.fuh.qrefinement.eval;

import java.io.File;
import java.io.RandomAccessFile;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.swa.ui.GMAF_UI;

import java.awt.GridLayout;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;

/**
 * This class implements a frame to show results of the eval process to the user
 * 
 * @author Nicolas Boch
 *
 */
public class EvalResultsFrame extends JFrame {
	
	public EvalResultsFrame (File initIter, File lastIter)
	{
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.X_AXIS));
		setTitle("Eval Results");
		setLocationRelativeTo(GMAF_UI.getInstance());
		 
		
		JEditorPane init = new JEditorPane ();
		init.setEditable(false);
		InitTextPane(init, initIter);
		
		JEditorPane last = new JEditorPane();
		last.setEditable(false);
		InitTextPane(last, lastIter);
		
		JLabel initLabel = new JLabel ("Initial Trec Results");
		JLabel lastLabel = new JLabel ("Trec Results after last iteration");
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.add(initLabel, "North");
		left.add(new JScrollPane(init), "Center");
		
		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(lastLabel, "North");
		right.add(new JScrollPane(last), "Center");
		
		add(left);
		add(right);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	}
	
	private void InitTextPane (JEditorPane p, File f)
	{
		try
		{
		p.setContentType("text/plain");

		RandomAccessFile rf = new RandomAccessFile (f, "r");
		
		
		p.setText("");
		String line = "";
		String content = "";
		while ((line = rf.readLine()) != null) {
			content += line + "\n";
		}
		p.setText(content);
		rf.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
