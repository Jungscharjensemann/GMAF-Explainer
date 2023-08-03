package de.swa.gc.processing;

import java.util.Vector;

import de.swa.gc.GraphCode;

public class AppleMetalProcessor extends CollectionProcessor {
	static {
		System.loadLibrary("gclib.so");
	}

	public native void preloadIndex(Vector<GraphCodeMeta> collection);
	public native void setQueryObject(GraphCode gc);
	public native void execute();
	public native Vector<GraphCodeMeta> getResultList();
}
