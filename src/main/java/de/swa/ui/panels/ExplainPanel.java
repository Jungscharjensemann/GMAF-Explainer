package de.swa.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import de.swa.gc.ExplainableGraphCode;
import de.swa.gc.GraphCode;
import de.swa.gmaf.extensions.Explainable;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.extension.Explainer;
import de.swa.mmfg.extension.esmmfg.ESMMFG;
import de.swa.mmfg.extension.smmfg.SMMFG;

/** panel that shows the tag cloud **/
public class ExplainPanel extends JPanel implements ActionListener {
	private JToolBar toolbar = new JToolBar();
	private JTextArea text = new JTextArea();
	private JComboBox<String> levelOfDetail = new JComboBox(new String[]{"1", "2", "3", "4", "5", "10", "100"});
	private JComboBox<String> languageLevel = new JComboBox(new String[] {"Simple", "Normal", "Complex"});
	private Explainable explainable;
	
	public ExplainPanel(MMFG mmfg) {
		explainable = new ESMMFG(new SMMFG(mmfg));
		init();
	}
	
	public ExplainPanel(GraphCode gc) {
		explainable = new ExplainableGraphCode(gc);
		init();
	}
	
	private void init() {
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolbar.add(new JLabel("  LOD:"));
		toolbar.add(levelOfDetail);
		toolbar.add(new JLabel("   Language:"));
		toolbar.add(languageLevel);
		toolbar.add(new JLabel("  "));
		JButton refr = new JButton("Apply");
		toolbar.add(refr);
		refr.addActionListener(this);
		text.setLineWrap(true);

		setLayout(new BorderLayout());
		add(toolbar, "North");
		add(new JScrollPane(text), "Center");
		refresh();
	}	
	
	private void refresh() {
		int lod = 1;
		int lang = 1;
		try { lod = Integer.parseInt(levelOfDetail.getSelectedItem().toString()); } catch (Exception x) {}
		try { lang = languageLevel.getSelectedIndex(); } catch (Exception x) {}
		String explanation = Explainer.explain(explainable, lod, lang);
		text.setText(explanation);
	}
	
	public void actionPerformed(ActionEvent e) {
		refresh();
	}
}
