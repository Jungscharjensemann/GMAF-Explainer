package de.swa.gmaf.api;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

import javax.jws.WebMethod;
import javax.jws.WebService;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;

/** implementation of the GMAF SOAP API **/
@WebService(endpointInterface = "de.swa.gmaf.api.GMAF_Facade")
public class GMAF_Facade_SOAPImpl implements GMAF_Facade {
	// data structures to hold sessions
	private Hashtable<String, String> errorMessages = new Hashtable<String, String>();
	
	// returns a GMAF_Facade_SOAPImpl for a given API-Key
	private GMAF getSession(String api_key) {
		return GMAF_SessionFactory.getInstance().getGmaf(api_key);
	}
	
	/** returns a new session token **/
	@WebMethod public String getAuthToken(String api_key) {
		System.out.println("Auth-Token: " + api_key);
		return GMAF_SessionFactory.getInstance().getAuthToken(api_key);
	}
	
	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromFile(String auth_token, File f) {
		try {
			return getSession(auth_token).processAsset(f);
		}
		catch (Exception x) {
			x.printStackTrace();
			errorMessages.put(auth_token, x.getMessage());
		}
		return null;
	}
	
	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromBytes(String auth_token, byte[] bytes, String suffix) {
		try {
			File f = File.createTempFile("gmaf", suffix);
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(bytes);
			fout.flush();
			fout.close();
			return getSession(auth_token).processAsset(f);
		}
		catch (Exception x) {
			x.printStackTrace();
			errorMessages.put(auth_token, x.getMessage());
		}
		return null;
	}

	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromURL(String auth_token, URL url) {
		try {
			URLConnection uc = url.openConnection();
			byte[] bytes = uc.getInputStream().readAllBytes();
			String suffix = url.toString();
			suffix = suffix.substring(suffix.lastIndexOf(".") + 1, suffix.length());
			return processAssetFromBytes(auth_token, bytes, suffix);
		}
		catch (Exception x) {
			x.printStackTrace();
			errorMessages.put(auth_token, x.getMessage());
		}
		return null;
	}
	
	/** sets the classes of the processing plugins (optional) **/
	@WebMethod public void setProcessingPlugins(String auth_token, Vector<String> plugins) {
		getSession(auth_token).setProcessingPlugins(plugins);
	}
	
	/** returns the collection of MMFGs for a given auth_token **/
	@WebMethod public Vector<MMFG> getCollection(String auth_token) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getCollection();
	}
	
	/** returns a Graph Code for a given MMFG **/
	@WebMethod public GraphCode getOrGenerateGraphCode(String auth_token, MMFG mmfg) {
		return GraphCodeGenerator.generate(mmfg);
	}
	
	/** returns a list of similar assets for a given Graph Code **/
	@WebMethod public Vector<MMFG> getSimilarAssets(String auth_token, GraphCode gc) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getSimilarAssets(gc);
	}
	
	/** returns a list of recommendations for a given Graph Code **/
	@WebMethod public Vector<MMFG> getRecommendedAssets(String auth_token, GraphCode gc) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getRecommendedAssets(gc);
	}
	
	@WebMethod public String getLastError(String auth_token) {
		return errorMessages.get(auth_token);
	}
}
