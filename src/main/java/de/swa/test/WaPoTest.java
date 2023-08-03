package de.swa.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.GraphCodeIO;
import de.swa.gmaf.GMAF;
import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.builder.XMLEncodeDecode;

public class WaPoTest {
	public static void main(String[] args) throws Exception {
		GeneralDictionary d = GeneralDictionary.getInstance();
		GMAF gmaf = new GMAF();
		MMFG mmfg = gmaf.processAsset(new File("/Users/stefan_wagenpfeil/Desktop/post_9143.wapo"));
		XMLEncodeDecode xml = new XMLEncodeDecode();
		FileOutputStream fout = new FileOutputStream(new File("/Users/stefan_wagenpfeil/Desktop/post_9143.wapo.xml"));
		PrintWriter out = new PrintWriter(fout);
		out.println(xml.flatten(mmfg));
		out.close();

		GraphCode gc = GraphCodeGenerator.generate(mmfg);
		GraphCodeIO.write(gc, new File("/Users/stefan_wagenpfeil/Desktop/post_9143.wapo.gc"));
		System.out.println(gc);
		
//		for (GraphCode gc_i : gc.getCollectionElements()) System.out.println(gc_i);
//		
//		GraphCode union = GraphCodeCollection.getUnion(gc.getCollectionElements());
//		GraphCode summ = GraphCodeCollection.getSummaryGraphCode(gc, 20);
//		
//		System.out.println("UNION: " + union);
//		System.out.println("SUMMARY: " + summ);
	}
}
