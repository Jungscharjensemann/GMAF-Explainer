package de.swa.gmaf.api;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.Vector;

import javax.jws.WebService;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gmaf.GMAF;
import de.swa.mmfg.MMFG;
import de.swa.ui.MMFGCollection;
import de.swa.ui.command.QueryByExampleCommand;
import de.swa.ui.command.QueryByKeywordCommand;
import de.swa.ui.command.QueryBySPARQLCommand;

@WebService(endpointInterface = "de.swa.gmaf.api.GMAF_UI_Facade")
public class GMAF_UI_FacadeImpl implements GMAF_UI_Facade {
	public String getAuthToken(String api_key) {
		return GMAF_SessionFactory.getInstance().getAuthToken(api_key);
	}

	public String[] queryByKeyword(String auth_token, String keywords) {
		QueryByKeywordCommand qbk = new QueryByKeywordCommand(keywords);
		qbk.setSessionId(auth_token);
		qbk.execute();
		return getCollectionIds(auth_token);
	}

	public String[] queryByExample(String auth_token, String mmfg_id) {
		QueryByExampleCommand qbe = new QueryByExampleCommand(mmfg_id, auth_token);
		qbe.execute();
		return getCollectionIds(auth_token);
	}

	public String[] queryBySPARQL(String auth_token, String query) {
		QueryBySPARQLCommand qbs = new QueryBySPARQLCommand(query);
		qbs.setSessionId(auth_token);
		qbs.execute();
		return getCollectionIds(auth_token);
	}

	public String getPreviewURL(String auth_token, String mmfg_id) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		UUID id = UUID.fromString(mmfg_id);
		MMFG mmfg = coll.getMMFGForId(id);
		return mmfg.getGeneralMetadata().getPreviewUrl().toString();
	}

	public MMFG getMMFGForId(String auth_token, String mmfg_id) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		UUID id = UUID.fromString(mmfg_id);
		return coll.getMMFGForId(id);
	}

	public GraphCode getGraphCodeForMMFG(String auth_token, String mmfg_id) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		UUID id = UUID.fromString(mmfg_id);
		MMFG mmfg = coll.getMMFGForId(id);
		GraphCode gc = GraphCodeGenerator.generate(mmfg);
		return gc;
	}

	public String processAsset(String auth_token, String fileName, byte[] content) {
		GMAF gmaf = GMAF_SessionFactory.getInstance().getGmaf(auth_token);
		try {
			File f = new File("temp/" + fileName);
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(content);
			fout.close();
			MMFG mmfg = gmaf.processAsset(f);

			MMFGCollection coll = MMFGCollection.getInstance(auth_token);
			coll.addToCollection(mmfg);
			return mmfg.getGeneralMetadata().getId().toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String[] getCollectionIds(String auth_token) {
		MMFGCollection coll = MMFGCollection.getInstance(auth_token);
		Vector<MMFG> v = coll.getCollection();
		String[] str = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			str[i] = v.get(i).getGeneralMetadata().getId().toString();
		}
		return str;
	}
}
