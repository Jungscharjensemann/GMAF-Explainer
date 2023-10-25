package de.ja.model.editor;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeIO;

import java.io.File;
import java.io.Serializable;

/**
 * Diese Klasse stellt das Datenmodell für ein
 * Element in der Arbeitsfläche bzw. Liste dar.
 */
public class GraphCodeListElement implements Serializable {

    // Mit Graph Code Datei assoziierte Graph Code.
    private final GraphCode graphCode;

    // Namen der Graph Code Datei.
    private String fileName;

    // Assoziierte Datei.
    private File file;

    public GraphCodeListElement(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.graphCode = GraphCodeIO.read(file);
    }

    public GraphCodeListElement(GraphCode graphCode, String name) {
        this.graphCode = graphCode;
        this.fileName = name;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GraphCode getGraphCode() {
        return graphCode;
    }
}
