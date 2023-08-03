package de.swa.fuh.microsoft.model.GMAF;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

import de.swa.fuh.microsoft.model.ImageDetectionLogger;

/**
 * GMAF Xml class, with represents GMAF XML objext
 * source to parse into XML file
 */
@XmlRootElement(name = "gmaf-data")
public class GMAFXml {
    private static final String FILESUFFIX = ".xml";

    private String           fileName;
    private String           date;
    private List<GMAFObject> gmafObjects;
    
    public GMAFXml(){}
    
    public GMAFXml(String _fileName, List<GMAFObject> _gmafObjects){
        this.setFileName(_fileName);
        this.setDate(DateTime.now().toString("dd.MM.yyyy"));
        this.setGmafObjects(_gmafObjects);
    }
    
    public String getFileName()
    {
        return fileName + FILESUFFIX;
    }
    
    @XmlElement(name = "file")
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getDate()
    {
        return date;
    }
    
    @XmlElement(name = "date")
    public void setDate(String date)
    {
        this.date = date;
    }
    
    @XmlElementWrapper(name = "objects")
    
    public List<GMAFObject> getGmafObjects()
    {
        return gmafObjects;
    }
    
    @XmlElement(name = "object")
    public void setGmafObjects(List<GMAFObject> gmafObjects)
    {
        this.gmafObjects = gmafObjects;
    }
    
    //methods
    public String GmafObjToXmlString(){
        String xmlString = null;
    
        try {
            StringWriter sw = new StringWriter();
            JAXB.marshal(this, sw);
            xmlString = sw.toString();
        }catch(Exception ex){
            ImageDetectionLogger.LogError("Probleme beim Erstellen eines XML String aus einem GMAF Objekt", ex);
        }
        
        return xmlString;
    }
}
