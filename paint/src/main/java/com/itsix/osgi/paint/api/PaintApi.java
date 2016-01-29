package com.itsix.osgi.paint.api;

import java.util.Collection;

public interface PaintApi {

    public Collection<Shape> listShapes();

    public void addShape(Shape shape);

    public void deleteShape(Shape shape);

}
