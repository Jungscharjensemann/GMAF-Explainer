package de.swa.fuh.microsoft.model.GMAF;

import javax.xml.bind.annotation.XmlElement;

/**
 * bounding box class, which represents bounding box element in GMAF XML
 */
public class BoundingBox
{
    private int x;
    private int y;
    private int width;
    private int height;
    
    public BoundingBox(int _x, int _y, int _height, int _width){
        this.setX(_x);
        this.setY(_y);
        this.setHeight(_height);
        this.setWidth(_width);
    }
    
    public int getX()
    {
        return x;
    }
    
    @XmlElement
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getY()
    {
        return y;
    }
    
    @XmlElement
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    @XmlElement
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
}
