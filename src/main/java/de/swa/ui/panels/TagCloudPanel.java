package de.swa.ui.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.swa.gc.GraphCode;

/** panel that shows the tag cloud **/
public class TagCloudPanel extends JPanel {
	public TagCloudPanel(GraphCode gc, int x, int y) {
		Vector<String> dict = gc.getDictionary();
		Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
		for (String s : dict) {
			int val = 0;
			if (s.equals("root-image")) continue;
			if (s.equals("root-asset")) continue;
			if (s.endsWith("_1")) continue;
			if (s.endsWith("_2")) continue;
			if (s.endsWith("_3")) continue;
			for (String t : dict) {
				try {
					int v = gc.getEdgeValueForTerms(s, t);
					if (v > 0) val ++;
				} 
				catch (Exception ex) {}
			}
			tags.put(s, val);
		}
		Hashtable<String, Integer> topTen = new Hashtable<String, Integer>();
		for (int i = 0; i < 20; i++) {
			String max_val = "";
			int max_i = -1;
			for (String s : tags.keySet()) {
				int val = tags.get(s);
				if (val > max_i) {
					max_i = val;
					max_val = s;
				}
			}
			topTen.put(max_val, max_i);
			tags.remove(max_val);
		}
		paintTags(topTen, x, y);
	}
	
	public BufferedImage getPreviewImage() {
		BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		this.paint(g);  
		return bi;
	}
	
	private void paintTags(Hashtable<String, Integer> tags, int x, int y) {
		setLayout(new GridLayout(1,1));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		for (String tag : tags.keySet()) {
			int val = tags.get(tag);
			JLabel l = new JLabel(tag);
			l.setFont(new Font("Arial", Font.PLAIN, 8 + val));
			p.add(l);
		}
		setPreferredSize(new Dimension(x, y));
		add(p);		
	}
}
