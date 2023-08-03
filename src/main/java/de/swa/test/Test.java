package de.swa.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import de.fuh.fpws2223.SocialMediaProcessor;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.builder.FeatureVectorBuilder;
import de.swa.mmfg.builder.GraphMLFlattener;
import de.swa.mmfg.builder.JsonFlattener;
import de.swa.mmfg.builder.XMLEncodeDecode;

public class Test {
	//  NOTE: add envorionment entry: GOOGLE_APPLICATION_CREDENTIALS -> /Users/stefan_wagenpfeil/eclipse-workspace/gmaf/API_Keys/Diss-0e43fbd15daa.json
	public static void main(String[] args) throws Exception {
		testSocialMediaProcessor("collection/LondonGrammer_WickedGame.youtube");
		System.out.println();
		testSocialMediaProcessor("collection/AxlRoseNovemberRain.twitter");
	}
	
	public static void testSocialMediaProcessor(String fileName){
		System.out.println("Start testSocialMediaProcessor: " + fileName);
		
		File f1 = new File(fileName);
		
		SocialMediaProcessor ap = new SocialMediaProcessor();
		MMFG mmfg = new MMFG();
		
		if (ap.canProcess(fileName)) {
			ap.process(null, f1, null, mmfg);
		}
				
		String result = FeatureVectorBuilder.flatten(mmfg, new XMLEncodeDecode());
		System.out.println(result);
	}
	
	private static void main2(String[] args) throws Exception {
		RandomAccessFile rf = new RandomAccessFile("graph.xml", "r");
		String line = "";
		String content = "";
		while ((line = rf.readLine()) != null) {
			content += line;
		}
		rf.close();
		String s = "";
		
//		MMFG fv = FeatureVectorBuilder.unflatten(content, new XMLEncodeDecode());
		GMAF af = new GMAF();
		
		String testFile = "test_rss.json";
		FileInputStream fs = new FileInputStream(testFile);
		byte[] bytes = fs.readAllBytes();
		MMFG fv = af.processAsset(bytes, testFile, "sw", 5, 50, "test_rss", null);
		
		Vector<Node> n = fv.allNodes;
		int ec = 0;
		for (Node ni : n) {
			ec += ni.getAssetLinks().size();
			ec += ni.getChildNodes().size();
			ec += ni.getCompositionRelationships().size();
			ec += ni.getSemanticRelationships().size();
			ec += ni.getTechnicalAttributes().size();
			ec += ni.getWeights().size();
		}
		
		System.out.println(testFile + " -> " + n.size() + " -> " + ec);
		
		if (true) return;
		
		s = FeatureVectorBuilder.flatten(fv, new GraphMLFlattener());
		
		rf = new RandomAccessFile("graph.graphml", "rw");
		rf.setLength(0);
		rf.writeBytes(s);
		rf.close();
		
//		s = FeatureVectorBuilder.flatten(fv, new GraphCodeGeneratorOld());
//		if (true) return;

		s = FeatureVectorBuilder.flatten(fv,  new JsonFlattener());
		rf = new RandomAccessFile("graph.json", "rw");
		rf.setLength(0);
		rf.writeBytes(s);
		rf.close();
		
		s = FeatureVectorBuilder.flatten(fv, new XMLEncodeDecode());
		rf = new RandomAccessFile("graph.xml", "rw");
		rf.setLength(0);
		rf.writeBytes(s);
		rf.close();
	}
}
