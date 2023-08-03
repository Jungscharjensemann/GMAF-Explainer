package de.swa.test;

import com.google.common.collect.Sets;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;
import de.swa.gc.GraphCodeIO;
import org.apache.jena.riot.other.G;

import java.io.File;
import java.util.*;

public class SymmetricDifference {

    public static void main(String[] args) {
        GraphCode gc1 = GraphCodeIO.read(new File("graphcodes/2fe648dd4f0e085538c0fdd9de387360.png.gc"));
        GraphCode gc2 = GraphCodeIO.read(new File("graphcodes/542538.jpg.gc"));

        System.out.println("Dic-Gc1: " + gc1.getDictionary());
        System.out.println("Dic-Gc2: " + gc2.getDictionary());

        //GraphCode subtract = GraphCodeCollection.subtract(gc1, gc2);
        //System.out.println(subtract);

        //symmetricDifference(gc1, gc2);
        //intersection(gc1, gc2);

        difference(gc1, gc2);
    }

    static Sets.SetView<?> symmetricDifference(GraphCode gc1, GraphCode gc2) {
        return Sets.symmetricDifference(new HashSet<>(gc1.getDictionary()), new HashSet<>(gc2.getDictionary()));
        //System.out.println("Symmetric Difference: " + setView);
    }

    static Sets.SetView<?> intersection(GraphCode gc1, GraphCode gc2) {
        return Sets.intersection(new HashSet<>(gc1.getDictionary()), new HashSet<>(gc2.getDictionary()));
        //System.out.println("Intersection: " + setView);
    }

    static void difference(GraphCode... graphCodes) {
        GraphCode gcUnion = GraphCodeCollection.getUnion(new Vector<>(List.of(graphCodes)));

        Set<?> dics = new HashSet<>(gcUnion.getDictionary());
        for(int i = 0; i < graphCodes.length; i++) {
            dics.retainAll(graphCodes[i].getDictionary());
        }

        Sets.SetView diff = Sets.difference(new HashSet<>(gcUnion.getDictionary()), dics);

        System.out.println("Difference: " + diff);


    }
}
