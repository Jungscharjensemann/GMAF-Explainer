package de.swa.gmaf.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

import de.swa.gc.GraphCode;
import de.swa.mmfg.MMFG;

@WebService
public interface GMAF_UI_Facade {
	@WebMethod public String getAuthToken(String api_key);
	@WebMethod public String[] queryByKeyword(String auth_token, String keywords);
	@WebMethod public String[] queryByExample(String auth_token, String mmfg_id);
	@WebMethod public String[] queryBySPARQL(String auth_token, String query);
	@WebMethod public String getPreviewURL(String auth_token, String mmfg_id);
	@WebMethod public MMFG getMMFGForId(String auth_token, String mmfg_id);
	@WebMethod public GraphCode getGraphCodeForMMFG(String auth_token, String mmfg_id);
	@WebMethod public String processAsset(String auth_token, String fileName, byte[] content);
	@WebMethod public String[] getCollectionIds(String auth_token);
}
