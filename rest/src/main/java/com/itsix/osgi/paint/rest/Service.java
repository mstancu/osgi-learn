package com.itsix.osgi.paint.rest;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.annotate.JsonView;

import com.itsix.osgi.paint.api.PaintApi;
import com.itsix.osgi.paint.api.Shape;

@Path("/")
public class Service {

    @Context
    ServletContext context;

    @JsonView(Shape.class)
    @GET
    @Path("shapes")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Shape> listShapes() {
        PaintApi api = (PaintApi) context
                .getAttribute(PaintApi.class.getName());
        return api.listShapes();
    }

}
