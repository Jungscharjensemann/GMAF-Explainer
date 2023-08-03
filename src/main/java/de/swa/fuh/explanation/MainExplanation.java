package de.swa.fuh.explanation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import de.swa.gc.GraphCode;
import de.swa.ui.query.DefaultQueryExplainer;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         RefinementController class for explainability of information retrieval implemented in
 *         the GMAF within the scope of master thesis
 *
 *         Displays information on the query representation (query Graph Code)
 *         and ranking information of one or two result assets compared to the
 *         query based on the Graph Codes
 */
public class MainExplanation extends DefaultQueryExplainer {
	private JFrame window;
	private JScrollPane scrollPane;

	/**
	 * Displaying query information
	 *
	 * @param gcQuery (query Graph Code)
	 * 
	 */
	public void explain(GraphCode gcQuery) {

		GraphCode queryGC = new GraphCode();
		queryGC = gcQuery;
		window = getWindowFrame();
		QueryRepresentation queryRep = new QueryRepresentation(queryGC);

		// Properties of text pane and add to scroll pane
		JTextPane query = queryRep.getQueryPane();
		query.setEditable(false);
		query.setBackground(Colors.getGray());
		scrollPane = getScrollPane(query);

		/*
		 * Properties of delete button for query refinement and actionListener
		 */
		if (BooleanHelper.getRefineButton()) {
			JButton delete = queryRep.getDeleteButton();
			delete.setBackground(Colors.getDark());
			delete.setForeground(Colors.getBlue());
			delete.setFont(new Font(null, Font.BOLD, 15));
			delete.setBorder(BorderFactory.createBevelBorder(0, Colors.getBlue(), Colors.getBlue()));
			delete.setVisible(true);
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					window.setVisible(false);
					window.dispose();
				}
			});
			window.getContentPane().add(delete, BorderLayout.PAGE_END);
		}
		else {
			window.getContentPane().add(getCloseButton(), BorderLayout.PAGE_END);
		}
	}

	/**
	 * Displaying ranking information of result assets compared to query
	 * 
	 * @param gcQuery      (query Graph Code)
	 * @param result       (result Graph Code)
	 * @param isComparison (false for one result asset, true for comparison of two
	 *                     result assets)
	 */
	public void explain(GraphCode gcQuery, GraphCode result, boolean isComparison) {
		GraphCode queryGC = new GraphCode();
		queryGC = gcQuery;
		GraphCode resultGC = new GraphCode();
		resultGC = result;
		ResultExplanation resultExpl = null;

		/*
		 * Checking if query is null if true display message else properties of text
		 * pane and add to scroll pane
		 */
		if (gcQuery == null) {
			showMessage(
					"<html>Please enter new query or select button for <p> query information when on detail panel.</p></html>");
		} else {
			window = getWindowFrame();
			window.getContentPane().add(getCloseButton(), BorderLayout.PAGE_END);
			resultExpl = new ResultExplanation(queryGC, resultGC, isComparison);
			JTextPane expl = resultExpl.getResultPane();
			expl.setBackground(Colors.getGray());
			scrollPane = getScrollPane(expl);
		}
	}

	/**
	 * Properties of close button and actionListener
	 * 
	 * @return close button
	 */
	public JButton getCloseButton() {
		JButton close = new JButton("CLOSE");
		close.setBackground(Colors.getDark());
		close.setForeground(Colors.getBlue());
		close.setFont(new Font(null, Font.BOLD, 15));
		close.setBorder(BorderFactory.createBevelBorder(1, Colors.getBlue(), Colors.getBlue()));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				window.dispose();
			}
		});
		return close;
	}

	/**
	 * Properties of scroll pane
	 * 
	 * @param textPane (which is added to scrollPane)
	 * @return scroll pane
	 */
	public JScrollPane getScrollPane(JTextPane textPane) {
		scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVisible(true);
		window.getContentPane().add(scrollPane, BorderLayout.CENTER);
		return scrollPane;
	}

	/**
	 * Properties of message window
	 * 
	 * @param m (message to show)
	 * @return window with message
	 */
	public JFrame showMessage(String m) {
		window = new JFrame();
		JLabel message = new JLabel(m);
		message.setOpaque(true);
		message.setBackground(Colors.getBlue());
		message.setFont(new Font(null, Font.ITALIC, 14));
		message.setForeground(Colors.getDark());
		message.setHorizontalAlignment(SwingConstants.CENTER);
		window.getContentPane().add(getCloseButton(), BorderLayout.PAGE_END);
		window.getContentPane().add(message, BorderLayout.CENTER);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setAlwaysOnTop(true);
		window.setTitle("Explanation of information retrieval");
		window.setSize(400, 120);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		return window;
	}

	/**
	 * Properties of display window
	 * 
	 * @return window
	 */
	public JFrame getWindowFrame() {
		window = new JFrame();
		window.getContentPane().setBackground(Colors.getGray());
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setAlwaysOnTop(true);
		window.setTitle("Explanation of information retrieval");
		window.setSize(500, 600);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		return window;
	}
}