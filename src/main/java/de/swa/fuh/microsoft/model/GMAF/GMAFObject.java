package de.swa.fuh.microsoft.model.GMAF;

import javax.xml.bind.annotation.XmlElement;

/**
 * GMAF object class, which represents gmaf object element in GMAF XML
 */
public class GMAFObject {
    private String      term;
    private BoundingBox boundingBox;
    private double probability;
    
    public GMAFObject(String _term, int _xPosition, int _yPosition, int _width, int _height, double _probability){
        this.setTerm(_term);
        this.setBoundingBox(new BoundingBox(_xPosition, _yPosition, _width, _height));
        this.setProbability(_probability);
    }
    
    public String getTerm()
    {
        return term;
    }
    
    public void setTerm(String term)
    {
        this.term = term;
    }
    
    @XmlElement(name = "bounding-box")
    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }
    
    public void setBoundingBox(BoundingBox boundingBox)
    {
        this.boundingBox = boundingBox;
    }
    
    public double getProbability()
    {
        return probability;
    }
    
    public void setProbability(double probability)
    {
        this.probability = probability;
    }
}
