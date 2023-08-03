package de.swa.mmfg.builder;

import java.io.File;
import java.util.Vector;

import de.swa.mmfg.Location;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class RDFFlattener implements Flattener {
	public String flatten(MMFG fv) {
		//SemWebExtension ext = new SemWebExtension();
		
		StringBuffer sb = new StringBuffer();
		Vector<Location> loc = fv.getLocations();
		Boolean image = false;
		String localNs = "http://local/";
		sb.append("<?xml version=\"1.0\"?>\n"
				+ "    <rdf:RDF\n"
				+ "        xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "        xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+ "        xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "        xmlns:local=\""+localNs+"\"\n"
				+ "        xml:base=\""+new File(System.getProperty("user.dir")).toURI()+"\">\n"
				+ "        \n"
				+ "			<owl:Ontology rdf:ID=\""+loc.get(0).getName()+"\"/>\n"
				+ "        <owl:Class rdf:about=\""+localNs+"MMFG\"/>\n"
				+ "        <owl:Class rdf:about=\""+localNs+"Node\"/>\n"
				+ "        <owl:Class rdf:about=\""+localNs+"Annotation_Anchor\"/>\n"
				+ "        <owl:Class rdf:about=\""+localNs+"Relationship\"/>");
		
		for (Node n : fv.allNodes) {
			String vocabularyTerm = n.getName().replace(" ","_").replace("\n","_");
			if(vocabularyTerm.equals("Root-Image")) image = true;
			//String semanticId = ext.getCollectionIdForConcept(vocabularyTerm);
			sb.append("			<owl:Class rdf:about=\""+localNs + vocabularyTerm + "\">\n"
					+ "            <rdfs:subClassOf rdf:resource=\""+localNs+"Node\"/>\n"
					+ "            <rdfs:isDefinedBy rdf:resource=\"https://dbpedia.org/page/" + vocabularyTerm + "\"/>\n");
			if(vocabularyTerm.equals("Root-Image") || vocabularyTerm.equals("Root-Asset")) {
				for (Location l : loc) {
					sb.append("            <rdfs:label>"+ l.getName() +"</rdfs:label>\n");
				}				
			}
				
			sb.append("        </owl:Class>\n\n");
		}
			
			sb.append("		<owl:DatatypeProperty rdf:about=\""+localNs+"contains\">\n"
					+ "         <rdfs:subClassOf rdf:resource=\""+localNs+"Relationship\"/>\n");
			if(image) sb.append("<rdfs:domain rdf:resource=\""+localNs+"Root-Image\"/>\n");
				else sb.append("<rdfs:domain rdf:resource=\""+localNs+"Root-Asset\"/>\n");
			for (Node n : fv.allNodes) {
				String vocabularyTerm = n.getName().replace(" ","_").replace("\n","_");
				if(!vocabularyTerm.equals("Root-Image") && !vocabularyTerm.equals("Root-Asset")) {
					sb.append("<rdfs:range rdf:resource=\""+localNs+vocabularyTerm+"\"/>");
				}
			}				
			sb.append("        </owl:DatatypeProperty>\n\n");
		
		for (Node n : fv.allNodes) {
			String parentVocabularyTerm = n.getName().replace(" ","_").replace("\n","_");
			sb.append("			<owl:DatatypeProperty rdf:about=\""+localNs+"relatesTo"+parentVocabularyTerm+"\">\n"
					+ "            <rdfs:subClassOf rdf:resource=\""+localNs+"Relationship\"/>\n"
					+ "            <rdfs:domain rdf:resource=\""+localNs + parentVocabularyTerm + "\"/>\n");
			for (Node ni : n.getChildNodes()) {
				String childVocabularyTerm = ni.getName().replace(" ","_").replace("\n","_");
				sb.append("            <rdfs:range rdf:resource=\""+localNs + childVocabularyTerm + "\"/>\n");
			}
			sb.append("        </owl:DatatypeProperty>\n\n");
		}

		sb.append("</rdf:RDF>\n");
		return sb.toString();
	}

	public String endFile() {
		return "";
	}

	public String getFileExtension() {
		return "xml";
	}

	public String startFile() {
		return "";
	}
}
