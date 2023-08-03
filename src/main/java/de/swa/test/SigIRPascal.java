package de.swa.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import de.swa.gmaf.GMAF;
import de.swa.gmaf.plugin.googlevision.LabelDetection;
import de.swa.gmaf.plugin.googlevision.ObjectDetection;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class SigIRPascal {
	public static void main(String[] args) throws Exception {
		new SigIRPascal();
	}

	private static final String[] defaultPlugins = new String[] { LabelDetection.class.toString(),
			ObjectDetection.class.toString() };

	public SigIRPascal() throws Exception {
		File images = new File("/Users/stefan_wagenpfeil/Downloads/VOCdevkit_2/VOC2006/PNGImages");
		File sets = new File("/Users/stefan_wagenpfeil/Downloads/VOCdevkit_2/VOC2006/ImageSets");

		Hashtable<String, Vector<String>> data = new Hashtable<String, Vector<String>>();
		Hashtable<String, Result> results = new Hashtable<String, Result>();

		for (File fi : sets.listFiles()) {
			String fileName = fi.getName();
			if (fileName.indexOf("_") > 0) {
				String objectCategory = fileName.substring(0, fileName.indexOf("_"));
				Vector<String> objects = new Vector<String>();
				if (data.contains(objectCategory))
					objects = data.get(objectCategory);

				RandomAccessFile rf = new RandomAccessFile(fi, "r");
				String line = "";
				while ((line = rf.readLine()) != null) {
					// 000032 -1
					// 000034 -1
//					if (line.endsWith(" -1")) continue;
					String fix = line.substring(0, line.indexOf(" ")).trim();
					File fx = new File("/Users/stefan_wagenpfeil/Downloads/VOCdevkit_2/VOC2006/PNGImages/" + fix + ".png");
					if (fx.exists()) {
						if (!objects.contains(line)) {
							objects.add(line);

							Result r = new Result();
							if (results.get(objectCategory) != null)
								r = results.get(objectCategory);
							if (line.indexOf(" 1") > 0)
								r.hits++;
							else if (line.indexOf(" 0") > 0)
								r.hits++;
							results.put(objectCategory, r);
						}
					}
					else {
						System.out.println("File not found " + line);
					}
				}

				data.put(objectCategory, objects);
			}
		}

		Vector<String> categories = new Vector<String>();
		for (String s : data.keySet()) {
			categories.add(s);
		}

		GMAF gmaf = new GMAF();
		Vector<String> notFound = new Vector<String>();

		int count = 0;
		for (File fi : images.listFiles()) {
			count++;
			String fName = fi.getName();
			fName = fName.substring(0, fName.indexOf("."));
			FileInputStream fin = new FileInputStream(fi);
			byte[] bytes = fin.readAllBytes();

			File fx = new File("/Users/stefan_wagenpfeil/Downloads/VOCdevkit_2/VOC2006/Gmaf/" + fi.getName() + ".txt");
			Vector<Node> nodes = new Vector<Node>();
			if (fx.exists()) {
				String line = "";
				RandomAccessFile rr = new RandomAccessFile(fx, "r");
				while ((line = rr.readLine()) != null) {
					nodes.add(new Node(line, null));
				}
			} else {
				MMFG fv = gmaf.processAsset(bytes, fi.getName(), "sw", 0, 100, fi.getName(), fi);
				nodes = fv.allNodes;
			}
			RandomAccessFile rfx = new RandomAccessFile(
					"/Users/stefan_wagenpfeil/Downloads/VOCdevkit_2/VOC2006/Gmaf/" + fi.getName() + ".txt", "rw");
			Vector<String> detected = new Vector<String>();
			for (Node n : nodes) {
				// detected object
				String s = n.getName().toLowerCase();

				s = getSynonym(s);
				if (categories.contains(s)) {
					Result r = new Result();
					if (results.get(s) != null)
						r = results.get(s);
					Vector<String> v = data.get(s);
					if (!detected.contains(s)) {
						detected.add(s);
						if (v.contains(fName + "  1"))
							r.true_pos++;
						else if (v.contains(fName + "  0"))
							r.true_pos++;
						else if (v.contains(fName + " -1")) {
							r.false_pos++;
//							System.out.println("FP: " + fName + " " + s);
						}
					}
					results.put(s, r);
				}
				else if (!notFound.contains(s)) notFound.add(s);

				rfx.writeBytes(s + "\n");
			}
			rfx.close();
		}

		System.out.println("STATISTICS: ");
		System.out.println("====================");
		Collections.sort(categories);
		for (String cat : categories) {
			Result r = results.get(cat);
			float prec = (float) (r.true_pos + r.false_pos) / (float) r.hits;
			float rec = (float) (r.true_pos) / (float) r.hits;
			System.out.println(cat + " & " + r.hits + " & " + (r.true_pos + r.false_pos) + " & " + r.true_pos + " & "
					+ r.false_pos + " & " + prec + " & " + rec);
		}
		
		System.out.println("not found: ");
//		for (String s : notFound) System.out.println(s);
	}
	
	private String getSynonym(String s) {
		if (s.equals("cattle")) return "cow";
		if (s.equals("vehicle")) return "bus";
		if (s.equals("passenger"))return "person";
//		if (s.equals("land vehicle")) return "car";
		if (s.equals("motorcycle")) return "motorbike";
		if (s.equals("motocross")) return "motorbike";
		if (s.startsWith("bicycle")) return "bicycle";
//		if (s.startsWith("car")) return "car";
		if (s.startsWith("horse")) return "horse";
		return s;
	}

	class Result {
		public int true_pos = 0;
		public int false_pos = 0;
		public int false_neg = 0;
		public int hits = 0;
	}
}
