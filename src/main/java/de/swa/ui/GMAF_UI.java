package de.swa.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.ja.view.ExplainerFrame;
import de.swa.ui.command.CommandHistory;
import de.swa.ui.command.ExportCommand;
import de.swa.ui.command.GMAFConfCommand;
import de.swa.ui.command.OptimizeCommand;
import de.swa.ui.command.PluginConfCommand;
import de.swa.ui.command.ProcessCommand;
import de.swa.ui.command.QueryByExampleCommand;
import de.swa.ui.command.QueryRefinementCommand;
import de.swa.ui.command.ReloadCommand;
import de.swa.ui.panels.AssetDetailPanel;
import de.swa.ui.panels.AssetListPanel;
import de.swa.ui.panels.LogPanel;
import de.swa.ui.panels.QueryPanel;

/** main window of the GMAF UI **/
public class GMAF_UI extends JFrame implements ActionListener {
	private static GMAF_UI instance;
	
	public GMAF_UI() {
		ProgressFrame.getInstance().setProgress(98, "initializing UI");
		setTitle("...::: Generic Multimedia Analysis Framework :::...");
		setJMenuBar(getMenu());
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout(5, 5));
//		content.add(new TreePanel(), "West");
		
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		ProgressFrame.getInstance().setProgress(98, "preparing query panel");
		center.add(new QueryPanel(), "North");
		content.add(createJToolBar(), "North");
		ProgressFrame.getInstance().setProgress(98, "preparing asset details");
		content.add(new AssetDetailPanel(), "East");
		
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		ProgressFrame.getInstance().setProgress(98, "preparing asset list");
		sp.setLeftComponent(new JScrollPane(new AssetListPanel()));
		sp.setRightComponent(new JScrollPane(new LogPanel()));
		sp.setDividerLocation(780);

		center.add(sp, "Center");

		content.add(center, "Center");
		getContentPane().add(content);

		setSize(1800, 1200);
//		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ProgressFrame.getInstance().close();
		ProgressFrame.getInstance().setProgress(98, "UI finished");
		setVisible(true);
		instance = this;
	}
	
	private JMenuBar getMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu prop = new JMenu("Settings");
		JMenu about = new JMenu("About");
		
		mb.add(file);
		mb.add(prop);
		mb.add(about);
		
		prop.add(createJMenuItem("General Settings", "gmafSettings"));
		prop.add(createJMenuItem("Plugin Settings", "pluginSettings"));
		
		file.add(createJMenuItem("Process", "Process"));
		file.add(createJMenuItem("Reload Config", "Reload"));
		file.add(createJMenuItem("Reload UI", "ReloadUI"));
		file.add(createJMenuItem("Optimize", "Optimize"));
		JMenu export = new JMenu("Export");
		export.add(createJMenuItem("GraphML", "EXP:GraphML"));
		export.add(createJMenuItem("HTML", "EXP:HTML"));
		export.add(createJMenuItem("Image", "EXP:Image"));
		export.add(createJMenuItem("Json", "EXP:Json"));
		export.add(createJMenuItem("Neo4J", "EXP:Neo4J"));
		export.add(createJMenuItem("XML", "EXP:XML"));
		export.add(createJMenuItem("GraphCode", "EXP:GraphCode"));
		export.add(createJMenuItem("Logfile", "EXP:Log"));
		export.add(createJMenuItem("Detection-XML", "EXP:Detection-XML"));
		export.add(createJMenuItem("MPEG7", "EXP:MPEG7"));
		export.add(createJMenuItem("RDF", "EXP:RDF"));
		
		try {
			String s = Configuration.getInstance().getCollectionProcessorConfigClass();
			Class c = Class.forName(s);
			JMenuItem mi = (JMenuItem)c.newInstance();
			prop.add(mi);
		}
		catch (Exception x) {
			System.out.println("no configuration found.");
		}

		file.add(export);
		file.addSeparator();
		file.add(createJMenuItem("Exit", "Exit"));
		
		return mb;
	}
	
	private JMenuItem createJMenuItem(String label, String actionCommand) {
		JMenuItem mi = new JMenuItem(label);
		mi.setActionCommand(actionCommand);
		mi.addActionListener(this);
		return mi;
	}

	private Vector<JButton> toolBarButtons = new Vector<JButton>();
	private JToolBar createJToolBar() {
		JToolBar tb = new JToolBar();
		tb.add(createToolBarItem("Process", "Process", "resources/progress_bar.png", true));
		tb.add(createToolBarItem("GraphML", "EXP:GraphML", "resources/graph.png", true));
		tb.add(createToolBarItem("Graph Code", "EXP:GraphCode", "resources/table_selection_block.png", true));
		tb.add(createToolBarItem("Logfile", "EXP:Log", "resources/document_notebook.png", false));
		tb.add(createToolBarItem("Query from GC", "QBE", "resources/tables.png", true));
		tb.add(createToolBarItem("Boundingbox", "BBox", "resources/tab_pane.png", true));
		tb.add(createToolBarItem("Relevance Feedback", "RF", "resources/arrow_loop.png", true));
		tb.add(createToolBarItem("Explainer",
				"Explain",
				Objects.requireNonNull(Objects.requireNonNull(getClass().getClassLoader().getResource("images/explaining-icon-48x48.png")).getPath()), true));
//		tb.setFloatable(false);
//		tb.setRollover(true);
		return tb;
	}
	
	private JComponent createToolBarItem(String text, String action, String icon, boolean onlyWhenSelected) {
		if (action.equals("BBox")) {
			JToggleButton b = new JToggleButton(text, new ImageIcon(icon));
			b.setActionCommand(action);
			b.setToolTipText(text);
			b.addActionListener(this);
			b.setBorderPainted(true);
			b.setSelected(Configuration.getInstance().showBoundingBox());
			return b;
		}
		else {
			JButton b = new JButton(text, new ImageIcon(icon));
			b.setActionCommand(action);
			b.setToolTipText(text);
			b.addActionListener(this);
			b.setBorderPainted(true);
			if (onlyWhenSelected) toolBarButtons.add(b);
			return b;
		}
	}
	
	public static GMAF_UI getInstance() {
		return instance;
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		CommandHistory ch = CommandHistory.getInstance();
		if (s.equals("gmafSettings")) ch.addCommand(new GMAFConfCommand());
		else if (s.equals("pluginSettings")) ch.addCommand(new PluginConfCommand());
		else if (s.equals("Process")) ch.addCommand(new ProcessCommand(Configuration.getInstance().getSelectedAsset()));
		else if (s.equals("Reload")) ch.addCommand(new ReloadCommand());
		else if (s.startsWith("EXP:")) ch.addCommand(new ExportCommand(s));
		else if (s.equals("QBE")) ch.addCommand(new QueryByExampleCommand());
		else if (s.equals("Optmize")) ch.addCommand(new OptimizeCommand());
		else if (s.equals("BBox")) {
			Configuration.getInstance().showBoundingBox(!Configuration.getInstance().showBoundingBox());
			AssetDetailPanel.getCurrentInstance().refresh();
			System.out.println("Refresh BB " + Configuration.getInstance().showBoundingBox());
		}
		else if (s.equals("ReloadUI")) {
			setVisible(false);
			dispose();
			new GMAF_UI();
		}
		else if (s.equals("RF")) ch.addCommand(new QueryRefinementCommand());
		else if (s.equals("Exit")) System.exit(0);
		else if(s.equals("Explain")) new ExplainerFrame();
		
	}
}
