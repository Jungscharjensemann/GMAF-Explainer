package de.swa.fuh.qrefinement.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.swa.fuh.qrefinement.command.ShowImageRefinementCommand;
import de.swa.fuh.qrefinement.command.ShowTextRefinementCommand;
import de.swa.fuh.qrefinement.observer.ImageObservers;
import de.swa.fuh.qrefinement.observer.TextObservers;
import de.swa.fuh.qrefinement.observer.UpdateImageMarks;
import de.swa.fuh.qrefinement.observer.UpdateTextMarks;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.fuh.qrefinement.ui.datamodel.Rectangle;
import de.swa.fuh.qrefinement.ui.datamodel.TextMark;
import de.swa.gc.GraphCode;
import de.swa.ui.Tools;

/**
 * Panel showing currently selected asset
 * 
 * @author Nicolas Boch
 *
 */
public class RefinementDetailPanel extends JPanel implements ActionListener, UpdateTextMarks, UpdateImageMarks{
	private boolean inMemoryOnly = false;
	private static final double scalefactor = 500.0/1100.0;
	private JEditorPane ep;
	private JLabel l;
	private String content;

	public RefinementDetailPanel(boolean inMemory) {
		if (instance != null)
		{
			ImageObservers.getInstance().unsubscribe(instance);
			TextObservers.getInstance().unsubscribe(instance);
			TextObservers.getInstance().subscribe(this);
			ImageObservers.getInstance().subscribe(this);
		}
		else
		{
			TextObservers.getInstance().subscribe(this);
			ImageObservers.getInstance().subscribe(this);
		}
		inMemoryOnly = inMemory;
		instance = this;
		refresh();
	}

	public RefinementDetailPanel() {
		if (instance != null)
		{
			ImageObservers.getInstance().unsubscribe(instance);
			TextObservers.getInstance().unsubscribe(instance);
			TextObservers.getInstance().subscribe(this);
			ImageObservers.getInstance().subscribe(this);
		}
		else
		{
			TextObservers.getInstance().subscribe(this);
			ImageObservers.getInstance().subscribe(this);
		}
		instance = this;
		refresh();
	}

	public void refresh() {
		setVisible(false);
		removeAll();
		setLayout(new GridLayout(2, 1));
		add(getAssetPanel());
		//add(getGraphCodePanel());
		setVisible(true);
	}

	private static RefinementDetailPanel instance;

	public static RefinementDetailPanel getCurrentInstance() {
		return instance;
	}

	private File f;

	private JPanel getAssetPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		f = RefinementUIModel.getInstance().getSelectedAsset();

		String extension = f.getName();
		extension = extension.substring(extension.lastIndexOf(".") + 1, extension.length());
		extension = extension.toLowerCase();

		if (extension.equals("png") || extension.equals("gif") || extension.equals("jpg") || extension.equals("jpeg")
				|| extension.equals("bmp") || extension.equals("tiff")) {
			ImageIcon ii = new ImageIcon(f.getAbsolutePath());

			Image i = Tools.getScaledInstance(f.getName(), ii.getImage(), 500, false);
			
			Vector<Rectangle> rects = new Vector <Rectangle>();
			double newx, newy, newwidth, newheight = 0.0;
			
			for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
			{
				newx = scalefactor * (double) r.getX();
				newy = scalefactor * (double) r.getY();
				newwidth = scalefactor * (double) r.getWidth();
				newheight = scalefactor * (double) r.getHeight();
				Rectangle newrect = new Rectangle((int) newx, (int) newy, (int) newwidth, (int) newheight, r.getColor());
				rects.add(newrect);
			}
			
			i = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(i, rects);
			
			l = new JLabel(new ImageIcon(i));
			p.add(l);
		} else {
			try {
				RandomAccessFile rf = new RandomAccessFile(f, "r");
				String line = "";
				content = "";
				while ((line = rf.readLine()) != null) {
					content += line + "\n";
				}

				String prettyJson; 
				
				if (FilenameUtils.getExtension(f.getName()).equals("json"))
				{
				JsonObject json = JsonParser.parseString(content).getAsJsonObject();
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				
				prettyJson = gson.toJson(json);
				}
				else
				{
					prettyJson = content;
				}
				

				ep = new JEditorPane();
				ep.setText(prettyJson);
				ep.setEditable(false);
				
				for (TextMark m : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
				{
					if (FilenameUtils.getExtension(f.getName()).equals("json"))
					{
						String originalmark = content.substring(m.getStart(), m.getEnd());
						int start = prettyJson.indexOf(originalmark, m.getStart());
						int end = start + (m.getEnd()-m.getStart());
						
						ep.getHighlighter().addHighlight(start, end, de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(m.getColor()));
					}
					else
					{
						ep.getHighlighter().addHighlight(m.getStart(), m.getEnd(), de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(m.getColor()));
					}
				}
				
				p.add(new JScrollPane(ep));
				rf.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		JButton b = new JButton("Click here to specify relevant / non-relevant parts");
		b.setActionCommand("R");
		b.addActionListener(this);
		
		JPanel q = new JPanel();
		q.setLayout(new BorderLayout());
		q.add(p, "Center");
		JLabel l = new JLabel(f.getName());
		q.add(l, "North");
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout(5,5));
		p2.add(b, "North");
		p2.add(RefinementListTable.getInstance().getPane(), "Center");
		q.add(p2,"South");
		return q;
	}

	private GraphCode gc;

	public GraphCode getGraphCode() {
		return gc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "R")
		{
			//Refinement_UI.getInstance().refresh();
			//CardLayout c = (CardLayout)Refinement_UI.getInstance().getCards().getLayout();
		
			if (de.swa.fuh.qrefinement.qrm.Tools.isImageFile(f))
			{
			//c.show(Refinement_UI.getInstance().getCards(), "Imagepanel");
			new ShowImageRefinementCommand().execute();	
			}
			else
			{
				//c.show(Refinement_UI.getInstance().getCards(),"Textpanel");
				new ShowTextRefinementCommand().execute();
			} 
			System.out.println("Card changed");
			//ImageSelectObjectPanel.getInstance().refresh();
		}
		
	}

	@Override
	public void addMark(File fileRef, TextMark mark) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			try
			{
			if (FilenameUtils.getExtension(f.getName()).equals("json"))
			{
				String originalmark = content.substring(mark.getStart(), mark.getEnd());
				int start = ep.getText().indexOf(originalmark, mark.getStart());
				int end = start + (mark.getEnd()-mark.getStart());
				
				ep.getHighlighter().addHighlight(start, end, de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(mark.getColor()));
			}
			else
			{
				ep.getHighlighter().addHighlight(mark.getStart(), mark.getEnd(), de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(mark.getColor()));
			}
			}
			catch (BadLocationException ble)
			{
				ble.printStackTrace();
			}
		}
		
	}

	@Override
	public void updateTextMark(File fileRef) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
			ep.getHighlighter().removeAllHighlights();
			try
			{
				for (TextMark m : RefinementCollection.getInstance().getTextRelevanceMarks().get(f).getTextMarks())
				{
					if (FilenameUtils.getExtension(f.getName()).equals("json"))
					{
						String originalmark = content.substring(m.getStart(), m.getEnd());
						int start = ep.getText().indexOf(originalmark, m.getStart());
						int end = start + (m.getEnd()-m.getStart());
						
						ep.getHighlighter().addHighlight(start, end, de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(m.getColor()));
					}
					else
					{
						ep.getHighlighter().addHighlight(m.getStart(), m.getEnd(), de.swa.fuh.qrefinement.qrm.Tools.getHighlighter(m.getColor()));
					}
				}
			}
			catch (BadLocationException ble)
			{
				ble.printStackTrace();
			}
		}
		
	}

	@Override
	public void addMark(File fileRef, Rectangle mark) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
		Image i = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 500, false);
		
		Vector<Rectangle> rects = new Vector <Rectangle>();
		double newx, newy, newwidth, newheight = 0.0;
		
		for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
		{
			newx = scalefactor * (double) r.getX();
			newy = scalefactor * (double) r.getY();
			newwidth = scalefactor * (double) r.getWidth();
			newheight = scalefactor * (double) r.getHeight();
			Rectangle newrect = new Rectangle((int) newx, (int) newy, (int) newwidth, (int) newheight, r.getColor());
			rects.add(newrect);
		}
		
		i = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(i, rects);
		
		l.setIcon(new ImageIcon(i));
		}
	}

	@Override
	public void updateImageMark(File fileRef) {
		if (fileRef.getAbsolutePath().equals(f.getAbsolutePath()))
		{
		Image i = Tools.getScaledInstance(f.getName(), new ImageIcon(f.getAbsolutePath()).getImage(), 500, false);
		
		Vector<Rectangle> rects = new Vector <Rectangle>();
		double newx, newy, newwidth, newheight = 0.0;
		
		for (Rectangle r : RefinementCollection.getInstance().getRelevanceMarks().get(f).getRectangles())
		{
			newx = scalefactor * (double) r.getX();
			newy = scalefactor * (double) r.getY();
			newwidth = scalefactor * (double) r.getWidth();
			newheight = scalefactor * (double) r.getHeight();
			Rectangle newrect = new Rectangle((int) newx, (int) newy, (int) newwidth, (int) newheight, r.getColor());
			rects.add(newrect);
		}
		
		i = de.swa.fuh.qrefinement.qrm.Tools.drawRectsOnImage(i, rects);
		
		l.setIcon(new ImageIcon(i));
		}
	}
		
	}


