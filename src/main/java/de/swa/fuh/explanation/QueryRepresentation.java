package de.swa.fuh.explanation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.swa.gc.GraphCode;
import de.swa.ui.MMFGCollection;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Query representation and query refinement
 *
 */
public class QueryRepresentation {

	private static JTextPane queryPane;
	private static JButton delete;
	private static Vector<String> deleteFeatures;
	
	/**
	 * 
	 * @param query (query Graph Code)
	 */
	public QueryRepresentation(GraphCode query) {
		deleteFeatures = new Vector<String>();
		GraphCode gc = query;
		boolean isRefined = BooleanHelper.getIsRefined();
		CreateDocumentFromGC doc = new CreateDocumentFromGC(gc, isRefined);
		DefaultStyledDocument document = doc.getDocument();
		queryPane = new JTextPane(document);

		/*
		 * Query refinement through mouseListener on text pane
		 */
		if (BooleanHelper.getRefineButton()) {
			delete = new JButton(
					"<html>To REFINE QUERY select features or objects to delete <p> APPLY and CLOSE</p></html>");
			queryPane.addMouseListener((MouseListener) new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int pos = queryPane.viewToModel2D(e.getPoint());
					try {
						/*
						 * Check for green X on current mouse position if X is clicked set it to color
						 * red get feature by adding characters to word till ",  " save generated
						 * feature word in addDeleteFeatures
						 */
						if (pos != 0 && queryPane.getText(pos, 1).contains("X")
								&& document.getForeground(document.getCharacterElement(pos).getAttributes())
										.equals(Colors.getGreen())) {
							SimpleAttributeSet deleted = new SimpleAttributeSet();
							StyleConstants.setForeground(deleted, Colors.getRed());
							document.setCharacterAttributes(pos, 1, deleted, true);
							String word = "";
							for (int p = pos + 2; p < document.getLength() - 2; p++) {
								char search1 = ',';
								char search2 = ' ';
								try {
									if (queryPane.getText(p, 3).charAt(0) == search1
											&& queryPane.getText(p, 3).charAt(1) == search2
											&& queryPane.getText(p, 3).charAt(2) == search2)
										break;
									word = word + queryPane.getText(p, 1);
								} catch (BadLocationException e1) {
									e1.printStackTrace();
								}
							}
							addDeleteFeatures(word);
						}
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			});
			/*
			 * ActionListener for delete button if clicked execute with refined query
			 */
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (deleteFeatures.size() != 0) {
						GraphCode query = refinedQueryGC(gc, deleteFeatures);
						BooleanHelper.setSelectedOk(false);
						MMFGCollection.getInstance().query(query);
					}
				}
			});
		}
	}

	private static void addDeleteFeatures(String word) {
		deleteFeatures.add(word);
	}

	/**
	 * Generate refined query from original query Graph Code and from vector of
	 * deleted features
	 *
	 * @param GraphCode      query (original query)
	 * @param Vector<String> deletedFeatures (includes all features which should be
	 *                       removed from original query for the refined query)
	 *
	 */
	private static GraphCode refinedQueryGC(GraphCode query, Vector<String> deletedFeatures) {
		GraphCode refinedQuery = new GraphCode();
		Vector<String> dictionary = new Vector<String>();
		// Dictionary for refined query excluding deleted features
		for (String s : query.getDictionary()) {
			if (!deletedFeatures.contains(s))
				dictionary.add(s);
		}
		refinedQuery.setDictionary(dictionary);
		// Get matrix values from original query
		for (String s : dictionary) {
			for (String t : dictionary) {
				int value = query.getValue(query.getDictionary().indexOf(s), query.getDictionary().indexOf(t));
				refinedQuery.setValueForTerms(s, t, value);
			}
		}
		BooleanHelper.setIsRefined(true);
		return refinedQuery;
	}

	public JTextPane getQueryPane() {
		return queryPane;
	}

	public JButton getDeleteButton() {
		return delete;
	}
}
