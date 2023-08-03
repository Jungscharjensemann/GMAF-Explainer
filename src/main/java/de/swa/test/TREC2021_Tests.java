package de.swa.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xmlbeans.impl.common.XmlStreamUtils;

import com.google.common.io.Files;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.GraphCodeIO;
import de.swa.gc.GraphCodeMetric;
import de.swa.gmaf.GMAF;
import de.swa.gmaf.plugin.text.WashingtonPostIndexer;
import de.swa.mmfg.MMFG;

public class TREC2021_Tests {
	private static Hashtable<File, float[]> metricValue = new Hashtable<File, float[]>();

	private static Vector<String> getTopicIds() throws Exception {
		Vector<String> topicIds = new Vector<String>();
		Vector<TrecTopic> topics = parseTopics(new File("trec/TREC_Topics.txt"));
		Vector<String> v = new Vector<String>();
		v.add("de.swa.gmaf.plugin.text.WashingtonPostIndexer");

		for (TrecTopic tt : topics) {
			TrecTopic empty = new TrecTopic();
			empty.setTitle("");
			empty.setNumber("");
			tt.addSubtopics(empty);
			for (TrecTopic subTT : tt.getSubtopics()) {
				System.out.println("Topic: " + tt.getTitle() + ", Subtopic: " + subTT.getTitle());
				System.out.println("ID: " + tt.getDocId());
				topicIds.add(tt.getDocId().trim());
			}
		}
		return topicIds;
	}

	private static Hashtable<String, GraphCode> getGCs(boolean fromCache) {
		Hashtable<String, GraphCode> data = new Hashtable<String, GraphCode>();
		if (fromCache) {
			try {
				ObjectInputStream oin = new ObjectInputStream(new FileInputStream(new File("trec/data.ser")));
				data = (Hashtable<String, GraphCode>) oin.readObject();
				System.out.println("loaded " + data.size() + " elements from cache.");
				return data;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		addToData(new File("/Users/stefan_wagenpfeil/Downloads/WashingtonPost.v4/split/"), data);

		try {
			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(new File("trec/data.ser")));
			oout.writeObject(data);
			oout.flush();
			oout.close();
			System.out.println("written " + data.size() + " elements to cache.");
		} catch (Exception x) {
			x.printStackTrace();
		}
		return data;
	}

	private static int c = 0;

	private static void addToData(File f, Hashtable<String, GraphCode> data) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (File fi : fs) {
				addToData(fi, data);
			}
		} else {
			if (f.getName().endsWith(".gc")) {
				try {
					GraphCode gc = GraphCodeIO.read(f);
					data.put(f.getAbsolutePath(), gc);
					Thread.sleep(5);
				} catch (Exception ex) {
					System.out.println("GC Error " + ex);
				}
				c++;

				if (c % 1000 == 0)
					System.out.println(c + " GCs added");
			}
		}
	}

	/*
	 * OutputSubmissions should be standard TREC format, that is,trec_eval results
	 * file format: 1 Q0 2707e25a-cfaf-11e6-a87f-b917067331bb 1 37.5 myrun 1 Q0
	 * 513673ee-d003-11e6-b8a2-8c2a61b0436f 2 33.2 myrun ... 1 Q0
	 * f8ded480-cdef-11e6-b8a2-8c2a61b0436f 99 0.5 myrun 2 Q0
	 * 350e3d74-cf94-11e6-a87f-b917067331bb 1 55.2 myrun ... Systems may retrieve up
	 * to 100 documents per topic. The first field is the topic id ("<num>" inthe
	 * topic), the second field is a literal "Q0", the third field is the document
	 * ID of the linkeddocument, the fourth field is the rank (ignored), the fifth
	 * field is the score, and the sixth field is the runtag.
	 * 
	 * Note that trec_eval sorts by descendingscore and breaks ties using document
	 * IDs.For “subtopics” runs, the topic field should be XXX.y,where ‘XXX’ is the
	 * topic number, and ‘y’ isthe subtopic number
	 */

	private static void performTopicRun(String runtag) throws Exception {
		metricValue.clear();
		Vector<TrecTopic> topics = parseTopics(new File("trec/TREC_Topics.txt"));
		GMAF gmaf = new GMAF();
		Vector<String> v = new Vector<String>();
		v.add("de.swa.gmaf.plugin.text.WashingtonPostIndexer");

		int t_index = 0;
		Hashtable<String, GraphCode> gcs = getGCs(true);
		RandomAccessFile rf = new RandomAccessFile("trec/run_" + runtag + ".txt", "rw");
		rf.setLength(0);

		for (TrecTopic tt : topics) {
			t_index++;
			TrecTopic empty = new TrecTopic();
			empty.setTitle("");
			empty.setNumber("");
			tt.addSubtopics(empty);
			int t_sub_index = 0;
			for (TrecTopic subTT : tt.getSubtopics()) {
				System.out.println("Topic: " + tt.getTitle() + ", Subtopic: " + subTT.getTitle());
				System.out.println("ID: " + tt.getDocId());
				try {
					File folder = new File("trec/topic_source");
//					File folder = new File("/Users/stefan_wagenpfeil/Downloads/WashingtonPost.v4/split/");
					Vector<String> docIds = new Vector<String>();
					docIds.add(tt.getDocId());
					File base = getFileForID(docIds, folder);
					if (base == null)
						continue;
					System.out.println("Base: " + base.getName());

					Vector<String> vadd = new Vector<String>();
					vadd.add(tt.getTitle());
					vadd.add(tt.getNarration());
					vadd.add(tt.getDescription());
					vadd.add(subTT.getTitle());
					WashingtonPostIndexer.additionalSentences = vadd;

					MMFG fv = gmaf.processAsset(base);
					GraphCode gcQuery = GraphCodeGenerator.generate(fv);

					Vector<File> result = new Vector<File>();
					check(gcQuery, gcs, result);
					System.out.println("Total: " + result.size() + " similar files.");
					Collections.sort(result, new Comparator() {
						public int compare(Object o1, Object o2) {
							File f1 = (File) o1;
							File f2 = (File) o2;

							float[] sim1 = metricValue.get(f1);
							float[] sim2 = metricValue.get(f2);
							Float i1 = (float) (sim1[0] * 10 + sim1[1] * 20 + sim1[2] * 30);
							Float i2 = (float) (sim2[0] * 10 + sim2[1] * 20 + sim2[2] * 30);
							return i2.compareTo(i1);
						}
					});

					int max = 100;
					max = Math.min(max, result.size());

					float score_factor = 10f / 7932f;
					for (int i = 0; i < max; i++) {
						float[] sim = metricValue.get(result.get(i));
						System.out.println("Result: " + result.get(i).getName() + " -> " + sim[0] + "/" + sim[1]);
						float score = 1 - score_factor * i;

						String num = tt.getNumber() + "." + t_sub_index;

						rf.writeBytes(
								num + " Q0 " + getDocIDForFile(result.get(i)) + " 1 " + score + " " + runtag + "\r\n");
					}
				} catch (Exception x) {
					x.printStackTrace();
					System.out.println("Exception: " + x);
				}
				t_sub_index++;
				return;
			}
		}
	}

	private static void performBackgroundLinkingRun(String runtag) throws Exception {
		metricValue.clear();
		Vector<TrecTopic> topics = parseTopics(new File("trec/TREC_Topics.txt"));
		GMAF gmaf = new GMAF();
		Vector<String> v = new Vector<String>();
		v.add("de.swa.gmaf.plugin.text.WashingtonPostIndexer");

		Hashtable<String, GraphCode> gcs = getGCs(true);
		RandomAccessFile rf = new RandomAccessFile(
				"trec/run_" + runtag + ".txt", "rw");
		rf.setLength(0);

		for (TrecTopic tt : topics) {
			try {
				File folder = new File("trec/topic_source");
				Vector<String> docIds = new Vector<String>();
				docIds.add(tt.getDocId());
				File base = getFileForID(docIds, folder);
				if (base == null) {
					System.out.println("no file found for " + docIds.get(0));
					continue;
				}
				System.out.println("Num : " + tt.getNumber());
				System.out.println("Base: " + base.getName());

				Vector<String> vadd = new Vector<String>();
				vadd.add(tt.getTitle());
				vadd.add(tt.getNarration());
				vadd.add(tt.getDescription());
				WashingtonPostIndexer.additionalSentences = vadd;

				MMFG fv = gmaf.processAsset(base);
				
				GraphCode gcQuery = GraphCodeGenerator.generate(fv);
				System.out.println("GCT : " + gcQuery.getDictionary().size());

				Vector<File> result = new Vector<File>();
				check(gcQuery, gcs, result);
				System.out.println("Tot : " + result.size());
				Collections.sort(result, new Comparator() {
					public int compare(Object o1, Object o2) {
						File f1 = (File) o1;
						File f2 = (File) o2;

						float[] sim1 = metricValue.get(f1);
						float[] sim2 = metricValue.get(f2);
						Float i1 = (float) (sim1[0] * 10 + sim1[1] * 20 + sim1[2] * 30);
						Float i2 = (float) (sim2[0] * 10 + sim2[1] * 20 + sim2[2] * 30);
						return i2.compareTo(i1);
					}
				});

				int max = 100;
				max = Math.min(max, result.size());

				float score_factor = 10f / 7932f;
				for (int i = 0; i < max; i++) {
					float[] sim = metricValue.get(result.get(i));
					float score = 1 - score_factor * i;
					String num = tt.getNumber();
					String doc = getDocIDForFile(result.get(i));
					rf.writeBytes(num + " Q0 " + doc + " 1 " + score + " " + runtag + "\r\n");
					System.out.println("         " + num + " " + doc);
				}
				System.out.println("Num : " + tt.getNumber() + " finished.");				
			} catch (Exception x) {
				x.printStackTrace();
				System.out.println("Exception: " + x);
			}
		}
	}

	private static String getDocIDForFile(File f) {
		if (f.getName().endsWith(".gc")) {
			String path = f.getAbsolutePath();
			path = path.substring(0, path.lastIndexOf(".gc"));
			f = new File(path);
		}
		try {
			RandomAccessFile rf = new RandomAccessFile(f, "r");
			String line = rf.readLine();
			// {"id": "fba962e0-3b72-11e5-b34f-4e0a1e3a3bf9"
			line = line.substring(8, line.indexOf(","));
			line = line.replace("\"", "");
			line = line.replace(",", "");
			line = line.trim();
			return line;
		} catch (Exception x) {
			x.printStackTrace();
		}
		return f.getName();
	}

	private static File getFileForID(Vector<String> docIds, File base) throws Exception {
//		System.out.println("getFileForID " + docId + " " + base.getAbsolutePath());
		if (base.isDirectory()) {
			File[] fs = base.listFiles();
			for (File fi : fs) {
//				System.out.println("Processing Folder  " + fi.getName());
				File f = getFileForID(docIds, fi);
				if (f != null)
					return f;
			}
		} else {
			if (!base.getName().endsWith("wapo"))
				return null;
			RandomAccessFile rf = new RandomAccessFile(base, "r");
			try {
//				System.out.println("RF: " + base.getName());
				String content = "";
				while ((content = rf.readLine()) != null) {
					// System.out.println("Content: " + content);
					for (String docId : docIds) {
						if (content.indexOf(docId.trim()) > 0) {
							System.out.println("Found Document for ID " + docId + " -> " + base.getAbsolutePath());
//							Files.copy(base, new File("/Users/stefan_wagenpfeil/Desktop/Trec/" + base.getName()));
							return base;
						}
					}
				}
			} catch (Exception x) {
				System.out.println("Exception reading file " + x);
			}
			rf.close();
		}
		return null;
	}

	private static void check(GraphCode queryGC, Hashtable<String, GraphCode> data, Vector<File> result) {
		for (String s : data.keySet()) {
			GraphCode gci = data.get(s);
			float[] sim = GraphCodeMetric.calculateSimilarity(queryGC, gci);
			if (sim[0] > 0.1f) {
				result.add(new File(s));
				metricValue.put(new File(s), sim);
//				System.out.println("Similar File: " + s);
			}
		}
	}

	private static void generateGraphCode(File f, GMAF gmaf) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			Arrays.sort(fs, new Comparator() {
				public int compare(Object o1, Object o2) {
					File f1 = (File) o1;
					File f2 = (File) o2;
					String name1 = f1.getName();
					String name2 = f2.getName();
					try {
						Integer i1 = Integer.parseInt(name1);
						Integer i2 = Integer.parseInt(name2);
						return i2.compareTo(i1);
					} catch (Exception x) {
					}
					return name2.compareTo(name1);
				}
			});
			for (File fi : fs) {
				generateGraphCode(fi, gmaf);
			}
		} else {
			try {
				if (f.getName().endsWith(".wapo")) {
					File fout = new File(f.getAbsolutePath() + ".gc");
					if (!fout.exists()) {
						MMFG fv = gmaf.processAsset(f);
						GraphCode gc = GraphCodeGenerator.generate(fv);
						GraphCodeIO.write(gc, fout);
						System.out.println(
								"wrote GraphCode " + fout.getAbsolutePath() + " -> " + gc.getDictionary().size());
					}
				}
			} catch (Exception x) {
				System.out.println("Exception: " + x);
			}
		}
	}

	private static void clean(File f) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (File fi : fs)
				clean(fi);
		} else {
			if (f.getName().endsWith(".gc"))
				f.delete();
		}
	}

	private static Vector<TrecTopic> parseTopics(File topicFile) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		TrecTopicParser ttp = new TrecTopicParser();
		sp.parse(topicFile, ttp);
		Vector<TrecTopic> topics = ttp.getTopics();
		return topics;
	}


	public static void main(String[] args) throws Exception {
		try {
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(new File("trec/data.ser")));
			Hashtable<String, GraphCode> data = (Hashtable<String, GraphCode>) oin.readObject();
			int i = 0;
			for (String s : data.keySet()) {
				if (data.get(s).getDictionary().contains("coyote")) {
					System.out.println(s);
				}
//				String fn = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("."));
//				GraphCodeIO.write(data.get(s), new File("/Users/stefan_wagenpfeil/Desktop/gc_collection/" + fn + ".json"));
//				i++;
//				System.out.print(".");
				if (i % 100 == 0) System.out.println();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		if (true) return;

		
//		clean(new File("/Users/stefan_wagenpfeil/Downloads/WashingtonPost.v4/split/"));
		GMAF gmaf = new GMAF();
		Vector<String> v = new Vector<String>();
		v.add("de.swa.gmaf.plugin.text.WashingtonPostIndexer");
		gmaf.setProcessingPlugins(v);
//		generateGraphCode(new File("/Users/stefan_wagenpfeil/Downloads/WashingtonPost.v4/split/"), gmaf);

//		Vector<String> ids = getTopicIds();
//		getGCs(true);
		performTopicRun("FUH_News_ST");
//		performBackgroundLinkingRun("FUH_News_BG");
	}
}
