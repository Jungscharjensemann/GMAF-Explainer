package de.swa.fuh.qrefinement.logic;

import de.swa.gc.GraphCode;

/**
 * 
 * 
 * Interface to be implemented by concrete similarity metrics to compare GCs during query execution. This is an implementation of Strategy Pattern.
 * 
 *  @author Nicolas Boch
 *
 */
public interface Querying {
 public void query (GraphCode gcQuery);
}
