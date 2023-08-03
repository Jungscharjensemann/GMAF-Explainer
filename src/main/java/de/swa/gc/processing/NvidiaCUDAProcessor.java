package de.swa.gc.processing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;

public class NvidiaCUDAProcessor extends CollectionProcessor {
	private String host = "cudaknecht.fritz.box";
	private int port = 4711;
	private StringBuffer result = null;
	private GraphCodeMeta queryGc;
	private Vector<GraphCodeMeta> collection;

	public void execute() {
		try {
			Socket socket = new Socket(host, 4711);
			// PrintWriter out = new PrintWriter(s.getOutputStream());
			PrintStream out = new PrintStream(socket.getOutputStream());

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("Query by Example: " + queryGc.getFileName());
			out.print("Query by Example: " + queryGc.getFileName());
			result = new StringBuffer();
			String line = in.readLine();
			while (!line.equals("metrics complete")) {
				line = in.readLine();
				System.out.println(line);
				result.append(line);
			}
			// Close our streams
			in.close();
			out.close();
			socket.close();

			String strJson = result.toString().replace("metrics complete", "");

			Gson json = new Gson();
			Vector<Object> elements = json.fromJson(strJson, Vector.class);
			for (Object o : elements) {
				Map<String, Object> m = (Map<String, Object>) o;
				String name = ((String) m.get("gc_filename"));
				Float similarity = ((Float) m.get("similarity"));
				Float recommendation = ((Float) m.get("recommendation"));
				Float inferencing = ((Float) m.get("inferencing"));
				// System.out.println(name + " " + similarity + " " + recommendation + " " +
				// inferencing);

				for (GraphCodeMeta gcm : collection) {
					if (gcm.getFileName().equals(name)) {
						float[] metric = { similarity, recommendation, inferencing };
						gcm.setMetric(metric);
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setQueryObject(GraphCodeMeta graphCodeMeta) {
		queryGc = graphCodeMeta;
	}

	@Override
	public Vector<GraphCodeMeta> getResultList() {
		if (result != null) {
			return collection;
		}
		return null;
	}

	@Override
	public void preloadIndex(Vector<GraphCodeMeta> collection) {
		this.collection = collection;

	}

	public void setHost(String s) {
		this.host = s;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
