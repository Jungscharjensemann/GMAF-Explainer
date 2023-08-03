package de.swa.test;

import java.io.RandomAccessFile;
import java.net.URL;

import de.swa.mmfg.CompositionRelationship;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import de.swa.mmfg.SemanticRelationship;
import de.swa.mmfg.builder.Flattener;
import de.swa.mmfg.builder.RDFFlattener;
import de.swa.mmfg.builder.XMLEncodeDecode;

public class NicoEngelhardt {
	public static void main(String[] args) throws Exception {
		MMFG mmfg = new MMFG();
		Node dbRoot = new Node("DB-Root", mmfg);
		Node person = new Node("Person", mmfg);
		dbRoot.addChildNode(person);
		person.addSemanticRelationship(new SemanticRelationship(new URL("http://rdf.de/Patient"), "Patient"));
		person.addChildNode(new Node("loc:Email", "mm@test.de", mmfg));
		person.addChildNode(new Node("loc:WebSite", "www.test.de", mmfg));
		person.addChildNode(new Node("ID", "987987", mmfg));
		person.addChildNode(new Node("Name", "Anna Mustermann", mmfg));
		person.addChildNode(new Node("Adresse", "Heimstrasse 21", mmfg));
		person.addChildNode(new Node("GebDatum", "23.01.1970", mmfg));
		
		Node untersuchung = new Node("Untersuchung", mmfg);
		person.addCompositionRelationship(new CompositionRelationship(CompositionRelationship.RELATION_RELATED_TO, untersuchung));
		person.addChildNode(untersuchung);
		
		Node mammo = new Node("Mammographie", mmfg);
		untersuchung.addChildNode(mammo);
		Node rtg = new Node("loc:Röntgenbild", "http://test.de/bild.jpg", mmfg);
		mammo.addChildNode(rtg);
		
		mammo.addChildNode(new Node("Merkmal 1", "Wert 1", mmfg));
		mammo.addChildNode(new Node("Merkmal 2", "Wert 2", mmfg));
		mammo.addChildNode(new Node("Merkmal 3", "Wert 3", mmfg));
		mammo.addChildNode(new Node("Merkmal 4", "Wert 4", mmfg));
		
		
		Flattener f = new XMLEncodeDecode();
		f = new RDFFlattener();
		String xml = f.flatten(mmfg);
		RandomAccessFile rf = new RandomAccessFile("/Users/stefan_wagenpfeil/Desktop/sampleXML.xml", "rw");
		rf.writeBytes(xml);
		rf.close();
	}
}
