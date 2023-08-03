package de.swa.gmaf.extensions.wikidata;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.bordercloud.sparql.Method;
import com.bordercloud.sparql.SparqlClient;
import com.bordercloud.sparql.SparqlResult;

import de.swa.gc.GraphCode;
import de.swa.gmaf.extensions.SemanticExtension;

/** Extension to the Semantic Web for Querying - WORK IN PROGRESS
 * 
 * @author stefan_wagenpfeil
 */

public class SemWebExtension implements SemanticExtension {
	private static final String endpointUrl = "https://query.wikidata.org/sparql";

	/** returns the resulting semantic identifier for a semantic concept (i.e. feature vocabulary term). This method internally creates a SPAR-QL query for an external semantic system **/
	public String getCollectionIdForConcept(String concept) {
		String id = getCollectionURLForConcept(concept);
        id = id.substring(id.lastIndexOf("/") + 1, id.length());
        return id;
	}
	
	public String getCollectionURLForConcept(String concept) {
		try {
	        String querySelect = "SELECT ?item ?itemLabel WHERE {\n" +
	                "  SERVICE wikibase:mwapi {\n" +
	                "      bd:serviceParam wikibase:endpoint \"www.wikidata.org\";\n" +
	                "        wikibase:api \"EntitySearch\";\n" +
	                "        mwapi:search \"" + concept + "\"; \n" +
	                "        mwapi:language \"en\".\n" +
	                "      ?item wikibase:apiOutputItem mwapi:item.\n" +
	                "  }\n" +
	                "  SERVICE wikibase:label {bd:serviceParam wikibase:language \"en\".}\n" +
	                "}\n" +
	                "ORDER BY ?item \n" +
	                "LIMIT 1";
	        
	        URI endpoint = new URI(endpointUrl);
	        SparqlClient sc = new SparqlClient(false);
	        sc.setEndpointRead(endpoint);
	        sc.setMethodHTTPRead(Method.GET);
	        
	        SparqlResult sr = sc.query(querySelect);
	        String id = "" + sr.getModel().getValueAt(0, 0);
	        return id;
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return concept;
	}
	
	public Vector<String> getSynonymNamesForConcept(String concept) {
		Vector<String> result = new Vector<String>();
		try {
	        String querySelect = "SELECT ?item ?itemLabel WHERE {\n" +
	                "  SERVICE wikibase:mwapi {\n" +
	                "      bd:serviceParam wikibase:endpoint \"www.wikidata.org\";\n" +
	                "        wikibase:api \"EntitySearch\";\n" +
	                "        mwapi:search \"" + concept + "\"; \n" +
	                "        mwapi:language \"en\".\n" +
	                "      ?item wikibase:apiOutputItem mwapi:item.\n" +
	                "  }\n" +
	                "  SERVICE wikibase:label {bd:serviceParam wikibase:language \"en\".}\n" +
	                "}\n" +
	                "ORDER BY ?item \n" +
	                "LIMIT 15";
	        
	        URI endpoint = new URI(endpointUrl);
	        SparqlClient sc = new SparqlClient(false);
	        sc.setEndpointRead(endpoint);
	        sc.setMethodHTTPRead(Method.GET);
	        
	        SparqlResult sr = sc.query(querySelect);
        	ArrayList<HashMap<String, Object>> al = sr.getModel().getRows();
        	for (HashMap<String, Object> r : al) {
        		for (String key : r.keySet()) {
        			if (key.equals("itemLabel")) result.add("" + r.get(key));
        		}
        	}

		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}

	public GraphCode getQueryGraphCode(String sparql, GraphCode base) {
		
		return base;
	}
	
	public int[] getRelationShipTypes(String concept1, String concept2) {
		return null;
	}
	
	public static void main(String[] args) {
		SemWebExtension ext = new SemWebExtension();
		String s = ext.getCollectionURLForConcept("cat");
		System.out.println(s);
	}
}
