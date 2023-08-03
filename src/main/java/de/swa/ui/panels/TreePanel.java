package de.swa.ui.panels;

import java.io.File;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import de.swa.ui.Configuration;

/** panel that shows the tree of multiple collections **/
public class TreePanel extends JPanel implements TreeSelectionListener {
	public TreePanel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("GMAF");
		DefaultMutableTreeNode coll = new DefaultMutableTreeNode(Configuration.getInstance().getCollectionName());
		root.add(coll);
		
		Vector<String> folders = Configuration.getInstance().getCollectionPaths();
		for (String s : folders) {
			File f = new File(s);
			DefaultMutableTreeNode di = new DefaultMutableTreeNode(f.getName());
			coll.add(di);
		}

		JTree tree = new JTree(root);
		tree.addTreeSelectionListener(this);
		
		add(new JScrollPane(tree));
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		String item = e.getPath().getLastPathComponent().toString();
		System.out.println("Tree selection " + item);
	}
}
