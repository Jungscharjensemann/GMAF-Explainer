package de.swa.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.swa.fuh.explanation.InfoButton;
import de.swa.fuh.explanation.MouseListenerForChosenAsset;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Tools;
import de.swa.ui.Configuration;
import de.swa.ui.command.CommandHistory;
import de.swa.ui.command.SelectCommand;

/** panel that shows the MMFG **/
public class MMFGPanel extends JPanel implements ActionListener {
	private String fileName;
	private MMFG mmfg;
	private JButton tb;
	private File f;
	private boolean selected;
	private boolean showTempSimilarity = false;
	private boolean isEnabled = true;
	
	public MMFGPanel(MMFG m) {
		this(m, false);
	}
	
	public MMFGPanel(MMFG m, boolean showTempSim) {
		this(m, showTempSim, true);
	}

	public MMFGPanel(MMFG m, boolean showTempSim, boolean isEnabled) {
		showTempSimilarity = showTempSim;
		f = m.getGeneralMetadata().getFileReference();
		fileName = f.getName();
		File sel = Configuration.getInstance().getSelectedAsset();

		selected = false;
		if (sel != null && f.getAbsolutePath().equals(sel.getAbsolutePath()))
			selected = true;

		String fname = fileName;
		int idx = MMFGCollection.getInstance().getIndexForAsset(m);
		String ext = fname.substring(fname.lastIndexOf("."), fname.length());
		if (fname.length() > 20) {
			fname = fname.substring(0, 10) + ".." + ext;
		}

		setLayout(new BorderLayout());
		JLabel name = new JLabel("[" + idx + "] " + fname, JLabel.CENTER);

		add(name, "North");
		Image newimg = new ImageIcon("resources/document_orientation_portrait.png").getImage();
		if (ext.equalsIgnoreCase(".wapo")) {
			newimg = new ImageIcon("resources/document_attachment.png").getImage();
		}
		else if (ext.equalsIgnoreCase(".xml")) {
			newimg = new ImageIcon("resources/document_tag.png").getImage();
		}
		else if (ext.equalsIgnoreCase(".json")) {
			newimg = new ImageIcon("resources/document_center_vertical.png").getImage();
		}
		else if (ext.equalsIgnoreCase(".docx")) {
			newimg = new ImageIcon("resources/text_align_left.png").getImage();
		}
		else if (ext.equalsIgnoreCase(".txt")) {
			newimg = new ImageIcon("resources/document_text.png").getImage();
		}
		else {
			ImageIcon icon = new ImageIcon(f.getAbsolutePath());
			newimg = Tools.getScaledInstance(f.getName(), icon.getImage(), 120, false);
		}
		
		if (!isEnabled) newimg = GrayFilter.createDisabledImage(newimg);
		tb = new JButton(new ImageIcon(newimg));
		tb.addActionListener(this);
		add(tb, "Center");
		tb.setOpaque(true);
		if (selected)
			tb.setBackground(Color.LIGHT_GRAY);

		JPanel icons = new JPanel();
		icons.setLayout(new FlowLayout(FlowLayout.LEFT));
		File fx = new File(Configuration.getInstance().getMMFGRepo() + File.separatorChar + fileName + ".mmfg");
		if (fx.exists()) {
			icons.add(new JLabel(new ImageIcon("resources/graph_claw.png")));
		}
		fx = new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + fileName + ".gc");
		if (fx.exists()) {
			icons.add(new JLabel(new ImageIcon("resources/table.png")));
		}
		
		InfoButton.getInfoButtonJPanel(icons, fileName);
		MouseListenerForChosenAsset.getMouseListenerForChosenAsset(tb, fileName);

		float[] fsim = m.getTempSimilarity();
//		if (showTempSimilarity) fsim = m.getTempSimilarity();
		if (fsim == null)
			fsim = new float[] { 0f, 0f, 0f };

		JLabel sim = new JLabel("Sim: " + trim(fsim[0]) + " | " + trim(fsim[1]) + " | " + trim(fsim[2]));
		sim.setFont(new Font("Arial", Font.PLAIN, 8));
		icons.add(sim);
		add(icons, "South");
	}

	private String trim(float f) {
		String fs = "" + f;
		if (fs.length() > 4)
			fs = fs.substring(0, 4);
		return fs;
	}

	public void actionPerformed(ActionEvent e) {
		CommandHistory.getInstance().addCommand(new SelectCommand(f));
	}
}
