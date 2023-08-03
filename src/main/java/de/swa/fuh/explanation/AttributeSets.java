package de.swa.fuh.explanation;

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.swa.ui.Configuration;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Implementing different attribute sets
 *
 */
public class AttributeSets {

	private SimpleAttributeSet set;

	AttributeSets(Color color, boolean bold, int fontSize) {
		set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, color);
		StyleConstants.setBold(set, bold);
		StyleConstants.setFontSize(set, fontSize);
	}

	AttributeSets(Color color, boolean bold, boolean italic, boolean underline, int fontSize) {
		set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, color);
		StyleConstants.setBold(set, bold);
		StyleConstants.setItalic(set, italic);
		StyleConstants.setUnderline(set,  underline);
		StyleConstants.setFontSize(set, fontSize);
	}
	//Set for icon display (for images get image thumbnail and scale to 40x40)
	AttributeSets(String assetName) {
		String ext = assetName.substring(assetName.lastIndexOf("."), assetName.length());
		ImageIcon icon = new ImageIcon();
		if (ext.matches(".jpg|.jpeg|.png|.bmp|.gif|.JPG")) {
			String filename = Configuration.getInstance().getThumbnailPath() + File.separatorChar + assetName + "_120.png";
			File file = new File(filename);
			if (file.exists())
				icon = new ImageIcon(filename);
			else
				icon = new ImageIcon(Configuration.getInstance().getCollectionName() + File.separatorChar + assetName);
			Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
			icon = new ImageIcon(image);
			set = new SimpleAttributeSet();
			StyleConstants.setIcon(set, icon);
		} else {
			icon = new ImageIcon("resources/file.png");
			set = new SimpleAttributeSet();
			StyleConstants.setIcon(set, icon);
		}
	}

	public SimpleAttributeSet getSet() {
		return set;
	}
}
