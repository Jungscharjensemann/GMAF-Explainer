package de.swa.gmaf.api;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.jws.WebMethod;
import javax.jws.WebService;

import de.swa.gc.GraphCode;
import de.swa.mmfg.MMFG;

/** GMAF API - SOAP implementation facade **/
@WebService
public interface GMAF_Facade {
	/** returns a new session token **/
	@WebMethod public String getAuthToken(String api_key);

	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromFile(String auth_token, File f);
	
	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromBytes(String auth_token, byte[] bytes, String suffix);
	
	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	@WebMethod public MMFG processAssetFromURL(String auth_token, URL url);
	
	/** sets the classes of the processing plugins (optional) **/
	@WebMethod public void setProcessingPlugins(String auth_token, Vector<String> plugins);
	
	/** returns the collection of MMFGs for a given auth_token **/
	@WebMethod public Vector<MMFG> getCollection(String auth_token);
	
	/** returns a Graph Code for a given MMFG **/
	@WebMethod public GraphCode getOrGenerateGraphCode(String auth_token,MMFG mmfg);
	
	/** returns a list of similar assets for a given Graph Code **/
	@WebMethod public Vector<MMFG> getSimilarAssets(String auth_token, GraphCode gc);
	
	/** returns a list of recommendations for a given Graph Code **/
	@WebMethod public Vector<MMFG> getRecommendedAssets(String auth_token, GraphCode gc);
}
