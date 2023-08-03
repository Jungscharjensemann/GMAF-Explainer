package de.swa.fuh.qrefinement.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.swa.fuh.qrefinement.command.SelectCommand;
import de.swa.fuh.qrefinement.command.SelectQueryCommandHistory;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.mmfg.MMFG;
import de.swa.ui.Tools;

/**
 * Thumbnail of an asset in the collection
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementPanel extends JPanel implements ActionListener{
	
	private String fileName;
	private MMFG mmfg;
	private JButton tb;
	private File f;
	private boolean selected;
	private boolean showTempSimilarity = false;
	private ButtonGroup group;

	public RefinementPanel (MMFG m)
	{
		this(m,false);
	}
	
	public RefinementPanel(MMFG m, boolean showTempSim)
	{
		mmfg = m;
		showTempSimilarity = showTempSim;
		f = m.getGeneralMetadata().getFileReference();
		fileName = f.getName();
		File sel = RefinementUIModel.getInstance().getSelectedAsset();

		selected = false;
		if (sel != null && f.getAbsolutePath().equals(sel.getAbsolutePath()))
			selected = true;

		String fname = fileName;
		String ext = fname.substring(fname.lastIndexOf("."), fname.length());
		if (fname.length() > 20) {
			fname = fname.substring(0, 10) + ".." + ext;
		}

		setLayout(new BorderLayout());
		JLabel name = new JLabel(fname, JLabel.CENTER);

		add(name, "North");
		Image newimg = new ImageIcon("resources/document_orientation_portrait.png").getImage();
		if (ext.equalsIgnoreCase(".wapo")) {
			newimg = new ImageIcon("resources/document_attachment.png").getImage();
		}
		else if (!ext.equalsIgnoreCase(".json")) {
			ImageIcon icon = new ImageIcon(f.getAbsolutePath());
			newimg = Tools.getScaledInstance(f.getName(), icon.getImage(), 120, false);
		}
		
		tb = new JButton(new ImageIcon(newimg));
		tb.addActionListener(this);
		add(tb, "Center");
		tb.setOpaque(true);
		if (selected)
			tb.setBackground(Color.LIGHT_GRAY);

		JPanel icons = new JPanel();
		icons.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		group = new ButtonGroup();
		
		JRadioButton relevant = new JRadioButton("relevant");
		relevant.setActionCommand("rel");
		JRadioButton notrelevant = new JRadioButton("not relevant");
		notrelevant.setActionCommand("nrel");
		JRadioButton dc = new JRadioButton("neutral");
		dc.setActionCommand("neu");
		group.add(relevant);
		group.add(notrelevant);
		group.add(dc);
		icons.add(relevant);
		icons.add(notrelevant);
		icons.add(dc);

		add(icons, "South");
	}

	public ButtonGroup getButtonGroup ()
	{
		return group;
	}
	
	public File getFile ()
	{
		return f;
	}
	
	public MMFG getMMFG ()
	{
		return mmfg;
	}
	
	public JButton getButton()
	{
		return tb;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		SelectQueryCommandHistory.getInstance().add(new SelectCommand(f));
		
	}
}
