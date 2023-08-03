package de.swa.mmfg.builder;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Vector;

import de.swa.fuh.mpeg7.Mpeg7Converter;
import de.swa.gmaf.plugin.fusion.UnionFeatureFusion;
import de.swa.mmfg.MMFG;

/** exports or imports a MMFG as XML or from XML **/
public class Mpeg7IO implements Flattener, Unflattener {
	public String flatten(MMFG fv) {	
		XMLEncodeDecode xml = new XMLEncodeDecode();
		String mmfgXMLFile = "temp/mmfg_" + System.currentTimeMillis() + ".mmfg";
		String mmfgXMLContent = xml.flatten(fv);
		
		try {
			RandomAccessFile rf = new RandomAccessFile(mmfgXMLFile, "rw");
			rf.writeBytes(mmfgXMLContent);
			rf.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		
		Mpeg7Converter Mpeg7Converter = new Mpeg7Converter("schema/Map_MMFG.csv", "schema/Map_DMPEG7.csv");
		List<String> final_export_file_paths = Mpeg7Converter.export_mmfg(mmfgXMLFile, "temp/dmpeg7_");
		
		return final_export_file_paths.get(0);
	}
	
	public MMFG unflatten(String xml) {
		Mpeg7Converter Mpeg7Converter = new Mpeg7Converter("schema/Map_MMFG.csv", "schema/Map_DMPEG7.csv");
		List<String> final_file_paths = Mpeg7Converter.import_mpeg7(xml, "temp/mmfg_");
		
		XMLEncodeDecode dec = new XMLEncodeDecode();
		MMFG mv = new MMFG();
		Vector<MMFG> mpeg7Parts = new Vector<MMFG>();
		for (String s : final_file_paths) {
			
			StringBuffer sb = new StringBuffer();
			try {
				RandomAccessFile rf = new RandomAccessFile(s, "r");
				String line = "";
				while ((line = rf.readLine()) != null) sb.append(line);
				rf.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			
			MMFG partMmfg = dec.unflatten(sb.toString());
			mpeg7Parts.add(partMmfg);
		}
		UnionFeatureFusion uff = new UnionFeatureFusion();
		uff.optimize(mv, mpeg7Parts);
		return mv;
	}
	
	public String getFileExtension() {
		return "xml";
	}
	public String endFile() { return ""; }
	public String startFile() { return ""; }

}
