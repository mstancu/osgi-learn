package com.itsix.osgi.paint.api;

public interface Subscriber {
    
    public void shapeAdded(Shape shape);
    public void shapeModified(Shape shape);
}
