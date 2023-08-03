package de.swa.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatDarkLaf;

import de.swa.gmaf.GMAF;
import de.swa.ui.panels.AssetDetailPanel;
import de.swa.ui.panels.LogPanel;

/** main window for the Asset Viewer **/
public class AssetViewer extends JFrame implements ActionListener {
	private File asset;
	private Vector<JCheckBox> plugins = new Vector<JCheckBox>();
	private GMAF gmaf = new GMAF();
	JPanel content = new JPanel();

	public AssetViewer() {
		FlatDarkLaf.install();

		JMenu file = new JMenu("File");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem exit = new JMenuItem("Exit");
		open.addActionListener(this);
		exit.addActionListener(this);
		file.add(open);
		file.add(exit);
		JMenuBar mb = new JMenuBar();
		mb.add(file);
		setJMenuBar(mb);

		setLayout(new BorderLayout());
		add(getPlugins(), "West");
		reload();
		
		setSize(1000, 1200);
		setVisible(true);
	}
	
	private JPanel getPlugins() {
		JPanel p = new JPanel();
		Vector<String> ps = gmaf.getPlugins();
		p.setLayout(new GridLayout(20, 1));
		for (String s : ps) {
			String label = s.substring(s.lastIndexOf(".") + 1, s.length());
			JCheckBox cb = new JCheckBox(label);
			p.add(cb);
			cb.setActionCommand(s);
			cb.addActionListener(this);
			if (s.indexOf("Object") > 0) cb.setSelected(true);
			plugins.add(cb);
		}
		return p;
	}

	private void reload() {
		content.setVisible(false);
		content.removeAll();
		content.setLayout(new GridLayout(1,1));
		AssetDetailPanel adp = new AssetDetailPanel(true);
		content.add(adp);
		content.setVisible(true);
		add(content, "Center");
		add(new LogPanel(), "South");
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("Exit")) System.exit(0);
		else if (cmd.equals("Open")) {
			JFileChooser fc = new JFileChooser();
			int i = fc.showOpenDialog(this);
			asset = fc.getSelectedFile();
			Configuration.getInstance().setSelectedAsset(asset);
			reload();
		}
		else {
			// Plugin Selection
			Vector<String> selectedPlugins = new Vector<String>();
			for (JCheckBox cb : plugins) {
				if (cb.isSelected()) {
					selectedPlugins.add(cmd);
				}
			}
			gmaf.setProcessingPlugins(selectedPlugins);
			reload();
		}
	}
	
	public static void main(String[] args) {
		new AssetViewer();
	}
}
