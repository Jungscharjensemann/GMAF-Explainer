package de.swa.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

public class SigIRImageSelector {
	public static void main(String[] args) throws Exception {
//		createDataset();
		filterDataset(new String[] {"dog", "dogs"});
		filterDataset(new String[] {"man", "men", "guy", "boy", "male"});
		filterDataset(new String[] {"golf", "golfer", "golfing"});
		filterDataset(new String[] {"guitar", "playing music", "guitars", "banjo", "bass"});
		filterDataset(new String[] {"bicycle", "bike"});
	}
	
	private static void filterDataset(String... keys) throws Exception {
		RandomAccessFile test = new RandomAccessFile("sigir/meta/meta.csv", "r");
		Vector<String> result = new Vector<String>();
		String line = "";
		int count = 0;
		while ((line = test.readLine()) != null) {
			String img = line.substring(0, line.indexOf(".jpg"));
			String descr = line;
			for (String key : keys) {
				if (descr.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
					if (!result.contains(img)) {
						result.add(img);
						count++;
					}
				}
			}
		}
		
		int hitCount = 0;
		int positive = 0;
		int negative = 0;
		Vector<String> hits = new Vector<String>();
		
		File f = new File("sigir/gc_flickr");
		File[] fs = f.listFiles();
		for (File fi : fs) {
			RandomAccessFile rfi = new RandomAccessFile(fi, "r");
			while ((line = rfi.readLine()) != null) {
				for (String key : keys) {
					if (line.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
						String fileName = fi.getName();
						fileName = fileName.substring(0, fileName.indexOf(".jpg"));
						if (!hits.contains(fileName)) {
							hitCount ++;
							if (result.contains(fileName)) positive ++;
							else {
								negative ++;
								System.out.println("TN: " + key + " -> " + fileName);
							}
							hits.add(fileName);
						}
					}
				}
			}
		}
		for (String key : keys) {
			System.out.print(key + " ");
		}
		System.out.println(":");
		System.out.println("Count: " + count);
		System.out.println("Hit  : " + hitCount);
		System.out.println("Pos  : " + positive);
		System.out.println("Neg  : " + negative); 
		System.out.println("Prec : " + (positive / hitCount));
		System.out.println("Rec  : " + (positive / count));
		
		System.out.println("");
	}
	
	
	private static void createDataset() throws Exception {
		String[] keywords = new String[] {"elephant", "dog", "tennis", "red", "bicycle", "cellphone", "music", "beach", "golf"};
		RandomAccessFile rf = new RandomAccessFile("/Users/stefan_wagenpfeil/Downloads/flickr30k_images/results.csv", "r");
		
		RandomAccessFile test = new RandomAccessFile("sigir/meta/meta.csv", "rw");
		test.setLength(0);
		
		String line = "";
		int count = 0;
		while ((line = rf.readLine()) != null) {
			boolean copy = false;
			String keyword = "";
			String sline = line.toLowerCase();
			for (String s : keywords) {
				if (sline.indexOf(s) > 0) {
					copy = true;
					keyword = s;
					break;
				}
			}
			
			if (copy) {
				test.writeBytes(line + "\r\n");
				String name = line.substring(0, line.indexOf('|'));
				System.out.println(keyword + " -> " + name);
				File f = new File("/Users/stefan_wagenpfeil/eclipse-workspace/gmaf/sigir/dataset/" + name);
				if (!f.exists()) {
					count ++;
					String cmd = "cp /Users/stefan_wagenpfeil/Downloads/flickr30k_images/flickr30k_images/flickr30k_images/" + name + " /Users/stefan_wagenpfeil/eclipse-workspace/gmaf/sigir/dataset/" + name;
					System.out.println(cmd);
					Process p = Runtime.getRuntime().exec(cmd);
					int i = p.waitFor();
				}
				if (count >= 1000) return;
			}
		}
	}
}
