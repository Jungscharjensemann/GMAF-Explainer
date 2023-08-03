package de.swa.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.swa.fuh.explanation.InfoButton;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.SemanticGraphCode;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.GraphCodeRenderer;
import de.swa.ui.GraphCodeTableModel;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Tools;
import de.swa.ui.Configuration;

/** panel that shows asset details **/
public class AssetDetailPanel extends JPanel {
	private boolean inMemoryOnly = false;

	public AssetDetailPanel(boolean inMemory) {
		inMemoryOnly = inMemory;
		instance = this;
		refresh();
	}

	public AssetDetailPanel() {
		instance = this;
		refresh();
	}

	public void refresh() {
		setVisible(false);
		removeAll();
		setLayout(new GridLayout(2, 1));
		add(getAssetPanel());
		add(getGraphCodePanel());
		setVisible(true);
	}

	private static AssetDetailPanel instance;

	public static AssetDetailPanel getCurrentInstance() {
		return instance;
	}

	private File f;

	private JPanel getAssetPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		f = Configuration.getInstance().getSelectedAsset();

		String name = "...";
		if (f != null) {
			String extension = f.getName();
			extension = extension.substring(extension.lastIndexOf(".") + 1, extension.length());
			extension = extension.toLowerCase();
			name = f.getName();

			if (extension.equals("png") || extension.equals("gif") || extension.equals("jpg")
					|| extension.equals("jpeg") || extension.equals("bmp") || extension.equals("tiff")) {
				ImageIcon ii = new ImageIcon(f.getAbsolutePath());

				Image i = Tools.getScaledInstance(f.getName(), ii.getImage(), 500,
						Configuration.getInstance().showBoundingBox());
				JLabel l = new JLabel(new ImageIcon(i));
				p.add(l);
			} else {
				try {
					RandomAccessFile rf = new RandomAccessFile(f, "r");
					String line = "";
					String content = "";
					while ((line = rf.readLine()) != null) {
						content += line + "\n";
					}

					String prettyFormat = content;
					try {
						JsonParser parser = new JsonParser();
						JsonObject json = parser.parse(content).getAsJsonObject();

						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						String prettyJson = gson.toJson(json);
						prettyFormat = prettyJson;
					} catch (Exception ex) {

					}

					JEditorPane ep = new JEditorPane();
					ep.setText(prettyFormat);
					p.add(new JScrollPane(ep));
					rf.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
		JPanel q = new JPanel();
		q.setLayout(new BorderLayout());
		q.add(p, "Center");
		JLabel l = new JLabel(name);
		q.add(l, "North");
		return q;
	}

	private GraphCode gc;

	public GraphCode getGraphCode() {
		return gc;
	}

	private JPanel getGraphCodePanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));

		MMFG mmfg = null;
		if (inMemoryOnly) {
			try {
				FileInputStream fs = new FileInputStream(f);
				byte[] bytes = fs.readAllBytes();
				mmfg = new GMAF().processAsset(bytes, f.getName(), "sw", 5, 500, f.getName(), f);
				this.gc = GraphCodeGenerator.generate(mmfg);
			} catch (Exception x) {
				x.printStackTrace();
			}
		} else {
			mmfg = MMFGCollection.getInstance().getMMFGForFile(f);
			this.gc = MMFGCollection.getInstance().getOrGenerateGraphCode(mmfg);
		}

//		gc = new GraphCode();
//		String[] str = new String[] {"age", "Nulliparous", "MIP", "bmi", "VDP", "APD", "blood-pressure", "Sectra type", "SNP", "version", "BI-RADS II", "Parity", "HRT use", "validity", "risk", "BI-RADS c", "HHUS", "ABUS", "tissue dens", "detec", "Par1", "SiemensV3", "SW1", "OP", "usr", "weight", "height", "sex", "prgn"};
//		str = new String[] {"age", "Nulliparous", "MIP", "bmi", "VDP", "APD", "blood-pressure", "Sectra type", "SNP", "version", "BI-RADS II", "Parity", "HRT use", "validity", "risk", "BI-RADS c", "HHUS", "ABUS", "tissue dens", "detec", "Par1", "SiemensV3", "SW1", "OP", "usr", "weight", "height", "sex", "prgn"};
//		int[] stv = new int[] {58, 1, 3, 29, 3, 2, 127, 4, 2, 3, 8, 2, 1, 90, 43, 2,3,2,6,1,2,3,2,64,5,3,3,22, 67,4,23,4,3,7,43,2,33,7,8,6,4,3,3,2};
//		Vector<String> dict = new Vector<String>();
//		for (String s : str) dict.add(s);
//		gc.setDictionary(dict);
//		for (int i = 0; i < str.length; i++) gc.setValue(i, i, stv[i]);
//		for (int i = 0; i < str.length; i++) {
//			for (int j = 0; j < str.length; j++) {
//				if (j == i) continue;
//				int val = (int)(Math.random() * 8);
//				val = val - 4;
//				if (val < 0) val = 0;
//				gc.setValue(i, j, val);
//			}
//		}

		JTabbedPane tp = new JTabbedPane();
		tp.addTab("Similar", new SimilarAssetPanel());
		tp.addTab("Recommendation", new RecommendedAssetPanel());
		tp.addTab("Explanation", new ExplainPanel(mmfg));

		JTabbedPane gctp = new JTabbedPane();
		tp.addTab("Graph Code", gctp);
		if (gc.isCollection()) {
			GraphCode union = GraphCodeCollection.getCollectionGraphCode(gc);
			GraphCode summary = GraphCodeCollection.getSummaryGraphCode(gc, 5);
			gctp.addTab("Union GC", getGraphCodePanel(union));
			gctp.addTab("Summary GC", getGraphCodePanel(summary));
			gctp.addTab("Semantic GC", getGraphCodePanel(new SemanticGraphCode(summary)));
		} else {
			gctp.addTab("GC", getGraphCodePanel(gc));
			gctp.addTab("SGC", getGraphCodePanel(new SemanticGraphCode(gc)));
		}
		gctp.addTab("FRGC", getGraphCodePanel(GraphCodeCollection.getFeatureRelevantGraphCode(gc)));
		gctp.addTab("ESGC", new ExplainPanel(gc));
		try {
			TagCloudPanel tcp = new TagCloudPanel(gc, 500, 500);
			gctp.addTab("Tags", tcp);

			File tn = new File(
					Configuration.getInstance().getThumbnailPath() + File.separatorChar + f.getName() + "_120.png");
			System.out.println("tn: " + tn.getAbsolutePath());
			if (!tn.exists()) {
				BufferedImage bi = tcp.getPreviewImage();
				try {
					ImageIO.write(bi, "png", tn);
					System.out.println("Thumbnail for Text written");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception x) {
		}

		InfoButton.getInfoButtonJTabbedPane(tp, f);

		p.add(tp);
		return p;
	}

	private JPanel getGraphCodePanel(GraphCode tempGc) {
		JPanel p = new JPanel();
		JTable graphcode = new JTable();

		try {
			graphcode.setModel(new GraphCodeTableModel(tempGc));
			graphcode.getTableHeader().setDefaultRenderer(new GraphCodeRenderer(true));
			graphcode.setDefaultRenderer(String.class, new GraphCodeRenderer(false));
			graphcode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			graphcode.getColumnModel().getColumn(0).setWidth(50);
			JTableHeader th = graphcode.getTableHeader();
			th.setPreferredSize(new Dimension(500, 80));

			for (int i = 1; i < graphcode.getColumnModel().getColumnCount(); i++)
				graphcode.getColumnModel().getColumn(i).setMaxWidth(12);
			p.setLayout(new GridLayout(1, 1));
			JScrollPane sp = new JScrollPane(graphcode);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			p.add(sp);
		} catch (Exception x) {
			x.printStackTrace();
		}

		return p;
	}
}
