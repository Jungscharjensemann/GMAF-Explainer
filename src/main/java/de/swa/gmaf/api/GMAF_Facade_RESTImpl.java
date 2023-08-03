package de.swa.gmaf.api;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

import javax.jws.WebMethod;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gc.GraphCodeIO;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.command.QueryByExampleCommand;
import de.swa.ui.command.QueryByKeywordCommand;
import de.swa.ui.command.QueryBySPARQLCommand;

/** implementation of the GMAF REST API **/
@Path("/gmaf")  
public class GMAF_Facade_RESTImpl /*extends ResourceConfig*/ {
	public GMAF_Facade_RESTImpl() {
//		packages("...");
	}
	
	/** returns a new session token **/
	@GET
    @Path("/{api-key}")  
	public String getAuthToken(
			@PathParam("api-key") String api_key) {
		
		String uuid = UUID.randomUUID().toString();
		sessions.put(uuid, new GMAF());
		return uuid;
	}

	/** returns a Graph Code for a given MMFG **/
	@POST
    @Path("/{auth-token}/{mmfg}")  
	@Produces("application/json")
	public String getOrGenerateGraphCode(
			@PathParam("auth-token") String auth_token, 
			@PathParam("mmfg") MMFG mmfg) {
		
		GraphCode gc = GraphCodeGenerator.generate(mmfg);
		String json = GraphCodeIO.asJson(gc);
		return json;
	}


	// data structures to hold sessions
	private Hashtable<String, GMAF> sessions = new Hashtable<String, GMAF>();
	private Hashtable<String, String> errorMessages = new Hashtable<String, String>();
	
	// returns a GMAF_Facade_SOAPImpl for a given API-Key
	@POST
    @Path("/{session}/{api-key}")  
	@Produces("application/json")
	private GMAF getSession(@PathParam("api-key") String api_key) {
		if (sessions.contains(api_key)) return sessions.get(api_key);
		else throw new RuntimeException("no valid API key");
	}

	
	/** processes an asset with the GMAF Core and returns the calculated MMFG **/
	public MMFG processAsset(String auth_token, @FormParam("file") File f) {
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
	@WebMethod public MMFG processAsset(String auth_token, byte[] bytes, String suffix) {
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
	@POST
    @Path("/{process-asset}/{mmfg}")  
	@Produces("application/json")
	public MMFG processAsset(@PathParam("auth-token") String auth_token, @PathParam("url") String surl) {
		try {
			URL url = new URL(surl);
			URLConnection uc = url.openConnection();
			byte[] bytes = uc.getInputStream().readAllBytes();
			String suffix = url.toString();
			suffix = suffix.substring(suffix.lastIndexOf(".") + 1, suffix.length());
			return processAsset(auth_token, bytes, suffix);
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
	@POST
    @Path("/{get-collection}")  
	@Produces("application/json")
	@WebMethod public Vector<MMFG> getCollection(@PathParam("auth-token") String auth_token) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getCollection();
	}
		
	/** returns a list of similar assets for a given Graph Code **/
	@POST
    @Path("/{get-similar-assets}")  
	@Produces("application/json")
	@WebMethod public Vector<MMFG> getSimilarAssets(@PathParam("auth-token") String auth_token, @PathParam("gc") GraphCode gc) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getSimilarAssets(gc);
	}
	
	/** returns a list of recommendations for a given Graph Code **/
	@POST
    @Path("/{get-recommended-assets}")  
	@Produces("application/json")
	@WebMethod public Vector<MMFG> getRecommendedAssets(@PathParam("auth-token") String auth_token, @PathParam("gc") GraphCode gc) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		return coll.getRecommendedAssets(gc);
	}
	
	@POST
    @Path("/{get-last-error}")  
	@Produces("application/json")
	@WebMethod public String getLastError(@PathParam("auth-token") String auth_token) {
		return errorMessages.get(auth_token);
	}
	
	@POST
    @Path("/{query-by-keyword}")  
	@Produces("application/json")
	@WebMethod public String[] queryByKeyword(@PathParam("auth-token") String auth_token, @PathParam("keywords") String keywords) {
		QueryByKeywordCommand qbk = new QueryByKeywordCommand(keywords);
		qbk.setSessionId(auth_token);
		qbk.execute();
		return getCollectionIds(auth_token);
	}

	@POST
    @Path("/{query-by-example}")  
	@Produces("application/json")
	@WebMethod public String[] queryByExample(@PathParam("auth-token") String auth_token, @PathParam("mmfg-id") String mmfg_id) {
		QueryByExampleCommand qbe = new QueryByExampleCommand(mmfg_id, auth_token);
		qbe.execute();
		return getCollectionIds(auth_token);
	}

	@POST
    @Path("/{query-by-sparql}")  
	@Produces("application/json")
	@WebMethod public String[] queryBySPARQL(@PathParam("auth-token") String auth_token, @PathParam("query") String query) {
		QueryBySPARQLCommand qbs = new QueryBySPARQLCommand(query);
		qbs.setSessionId(auth_token);
		qbs.execute();
		return getCollectionIds(auth_token);
	}
	
	@POST
    @Path("/{get-collection-ids}")  
	@Produces("application/json")
	@WebMethod public String[] getCollectionIds(@PathParam("auth-token") String auth_token) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		Vector<MMFG> v = coll.getCollection();
		String[] str = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			str[i] = v.get(i).getGeneralMetadata().getId().toString();
		}
		return str;
	}
}
