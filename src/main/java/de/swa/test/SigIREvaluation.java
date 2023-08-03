package de.swa.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import de.swa.CSVLogWriter;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.builder.FeatureVectorBuilder;
import de.swa.mmfg.builder.GraphMLFlattener;
import de.swa.mmfg.builder.Neo4JFlattener;

public class SigIREvaluation {
	public static void evaluateGC(String inputFile, String gc_folder) {
		try {
			RandomAccessFile rf = new RandomAccessFile(inputFile, "r");
			String line = "";
			String master = "";
			Vector<String> toCompare = new Vector<String>();
			while ((line = rf.readLine()) != null) {
				if (line.indexOf("{name:'N_Root_Image") > 0 && line.indexOf("Root_Image_IMG") > 0) {
					String image = line.substring(18, line.indexOf(":"));
					image = image.replace('_', '.');
					if (line.indexOf("N_Root_Image_1") > 0) {
						master = image;
					}
					else {
						toCompare.add(image);
					}
				}
			}
//			GraphCodeComparatorOld.compare(master, toCompare, gc_folder);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public static void evaluate(String source, String mmfvg, String gc) {
		File[] fs = new File(source).listFiles();
		int[] maxs = new int[] {1000};
		
		GMAF gmaf = new GMAF();
		Neo4JFlattener neo = new Neo4JFlattener();
		
		for (File fi : fs) {
			if (fi.getName().startsWith(".")) continue;
			try {
				FileInputStream fin = new FileInputStream(fi);
				byte[] bytes = fin.readAllBytes();
				
				for (int max : maxs) {
					System.out.println("processing " + fi.getName() + " / " + max);
					long start = System.currentTimeMillis();

					MMFG fv = gmaf.processAsset(bytes, fi.getName(), "sw", 3, max, fi.getName(), fi);
					String neo4j = FeatureVectorBuilder.flatten(fv, neo);
					String graphML = FeatureVectorBuilder.flatten(fv, new GraphMLFlattener());
					
					long gml_start = System.currentTimeMillis();
					RandomAccessFile rf = new RandomAccessFile(mmfvg + fi.getName() + "_" + max + ".graphml", "rw");
					rf.setLength(0);
					rf.writeBytes(graphML);
					rf.close();

					long gc_start = System.currentTimeMillis();
//					String s = FeatureVectorBuilder.flatten(fv, new GraphCodeGeneratorOld(fi.getName() + "_" + max + ".bmp", gc));
					long end = System.currentTimeMillis();
					
					CSVLogWriter.getInstance().log(fi.getName(), max, (gml_start - start), (gc_start - gml_start), (end-gc_start), (end - start), "processed");
				}
			}
			catch (Exception x) {
				x.printStackTrace();
			}
		}
		
		try {
			RandomAccessFile rf = new RandomAccessFile(mmfvg + "cipher.neo4j", "rw");
			rf.setLength(0);
			String neo4j = neo.getCipherScript();
			rf.writeBytes(neo4j);
			rf.close();
		}
		catch (Exception ex) {}
	}
	
	public static void evaluatePrecision() {
		
	}
	
	
	
	public static void main(String[] args) {
//		evaluate("sigir/dataset_flickr", "sigir/mmfvg_flickr/", "sigir/gc_flickr/");
//		evaluate("sigir/dataset_div2k", "sigir/mmfvg_div2k/", "sigir/gc_div2k/");
		evaluateGC("cipher_tmp_h10.neo4j", "sigir/gc_div2k");
		
//		Vector<String> tc = new Vector<String>();
//		tc.add("00000_TA1.jpeg");
//		GraphCodeComparator.compare("00000_TA1.jpeg", tc, "sigir/gc_div2k");
//		evaluatePrecision();
		
		if (true) return;
		System.out.println("F10");
		evaluateGC("cipher_tmp_f10.neo4j", "sigir/gc_div2k");
		System.out.println("F10");
		evaluateGC("cipher_tmp_f10.neo4j", "sigir/gc_div2k");
		System.out.println("F20");
		evaluateGC("cipher_tmp_f20.neo4j", "sigir/gc_div2k");
		System.out.println("F30");
		evaluateGC("cipher_tmp_f30.neo4j", "sigir/gc_div2k");
		System.out.println("F40");
		evaluateGC("cipher_tmp_f40.neo4j", "sigir/gc_div2k");
		System.out.println("F50");
		evaluateGC("cipher_tmp_f50.neo4j", "sigir/gc_div2k");
		System.out.println("D10");
		evaluateGC("cipher_tmp_d10.neo4j", "sigir/gc_div2k");
		System.out.println("D20");
		evaluateGC("cipher_tmp_d20.neo4j", "sigir/gc_div2k");
		System.out.println("D30");
		evaluateGC("cipher_tmp_d30.neo4j", "sigir/gc_div2k");
		System.out.println("D40");
		evaluateGC("cipher_tmp_d40.neo4j", "sigir/gc_div2k");
		System.out.println("D50");
		evaluateGC("cipher_tmp_d50.neo4j", "sigir/gc_div2k");
	}
}
