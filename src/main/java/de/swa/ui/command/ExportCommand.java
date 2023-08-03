package de.swa.ui.command;

import java.awt.Desktop;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.swa.gc.GraphCodeIO;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.builder.DetectionExporter;
import de.swa.mmfg.builder.Flattener;
import de.swa.mmfg.builder.GraphMLFlattener;
import de.swa.mmfg.builder.HTMLFlattener;
import de.swa.mmfg.builder.ImageFlattener;
import de.swa.mmfg.builder.JsonFlattener;
import de.swa.mmfg.builder.Mpeg7IO;
import de.swa.mmfg.builder.Neo4JFlattener;
import de.swa.mmfg.builder.RDFFlattener;
import de.swa.mmfg.builder.XMLEncodeDecode;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;
import de.swa.ui.panels.LogPanel;

/** class to encapsulate the Export Command **/

public class ExportCommand extends AbstractCommand {
	private String type;
	
	public ExportCommand(String export) {
		type = export;
	}
	
	public void execute() {
		Flattener f = null;
		if (type.equals("EXP:GraphML")) f = new GraphMLFlattener();
		else if (type.equals("EXP:HTML")) f = new HTMLFlattener();
		else if (type.equals("EXP:Image")) f = new ImageFlattener();
		else if (type.equals("EXP:Json")) f = new JsonFlattener();
		else if (type.equals("EXP:Neo4J")) f = new Neo4JFlattener();
		else if (type.equals("EXP:XML")) f = new XMLEncodeDecode();
		else if (type.equals("EXP:GraphCode")) f = new GraphCodeIO();
		else if (type.equals("EXP:Detection-XML")) f = new DetectionExporter();
		else if (type.equals("EXP:MPEG7")) f = new Mpeg7IO();
		else if (type.equals("EXP:RDF")) f = new RDFFlattener();

		String header = f.startFile();
		String footer = f.endFile();
		boolean singleFiles = false;
		if (header.equals("") && footer.equals("")) singleFiles = true;

		if (f != null) {
			Vector<MMFG> toProcess = Configuration.getInstance().getSelectedItems();
			if (!singleFiles) toProcess = MMFGCollection.getInstance().getCollection();
			if (toProcess.size() == 0) {
				JOptionPane.showMessageDialog(null, "No asset selected.");
				return;
			}

			RandomAccessFile rf = null;
			try {
				if (!singleFiles) {
					rf = new RandomAccessFile(Configuration.getInstance().getExportFolder() + File.separatorChar + "export." + f.getFileExtension(), "rw");
					rf.setLength(0);
					rf.writeBytes(header);
				}
				for (MMFG m : toProcess) {
					String s = f.flatten(m);
					System.out.println(s);
					try {
						File fc = new File(Configuration.getInstance().getExportFolder() + File.separatorChar + m.getGeneralMetadata().getFileName() + "." + f.getFileExtension());
						if (singleFiles) {
							rf = new RandomAccessFile(fc, "rw");
							rf.setLength(0);
							rf.writeBytes(s);
						}
						LogPanel.getCurrentInstance().addToLog("exported " + m.getGeneralMetadata().getFileName() + " to " + f.getFileExtension());
						if (singleFiles) Desktop.getDesktop().open(fc);
					}
					catch (Exception x) {
						x.printStackTrace();
					}
				}
				if (!singleFiles) {
					rf.writeBytes(footer);
					LogPanel.getCurrentInstance().addToLog("exported complete file to to " + Configuration.getInstance().getExportFolder() + File.separatorChar + "export." + f.getFileExtension());
					Desktop.getDesktop().open(new File(Configuration.getInstance().getExportFolder() + File.separatorChar + "export." + f.getFileExtension()));
				}
			}
			catch (Exception x) {}
		}
	}
}
