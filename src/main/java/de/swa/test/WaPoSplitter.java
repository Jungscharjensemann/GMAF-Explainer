package de.swa.test;

import java.io.File;
import java.io.RandomAccessFile;

public class WaPoSplitter {
	public static void main(String[] args) throws Exception {
		File f = new File("/Users/stefan_wagenpfeil/Desktop/Trec/topics.txt");
		RandomAccessFile rfx = new RandomAccessFile(f, "r");
		String line = "";
		int idx = 0;
		int folder = 1;
		while ((line = rfx.readLine()) != null) {
			if (idx % 1000 == 0) {
				folder ++;
				File fx = new File("/Users/stefan_wagenpfeil/Desktop/Trec/split/" + folder);
				fx.mkdirs();
			}
			idx ++;
			RandomAccessFile out = new RandomAccessFile("/Users/stefan_wagenpfeil/Desktop/Trec/split/" + folder + "/post_" + idx + ".wapo", "rw");
			out.writeBytes(line);
			out.close();
		}
		rfx.close();
	}
}

