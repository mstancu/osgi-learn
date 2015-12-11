package com.itsix.osgi.paint.rest;

import java.util.Collection;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ops4j.pax.web.service.WebContainer;

import com.itsix.osgi.paint.api.PaintApi;
import com.itsix.osgi.paint.api.Shape;

@Component
@Instantiate
public class RestOsgiManager {

    @Requires(optional = true)
    PaintApi paintApi;

    @Requires(optional = true)
    private WebContainer webContainer;

    @Validate
    public void start() {
        try {
            Collection<Shape> shapes = paintApi.listShapes();
            System.out.println("There are currently " + shapes.size()
                    + " shapes available");
        } catch (RuntimeException e) {
            System.out.println("Paint API is not currently available");
        }
    }

    @Invalidate
    public void stop() {

    }

}
