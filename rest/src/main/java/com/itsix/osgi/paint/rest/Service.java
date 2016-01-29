package com.itsix.osgi.paint.rest;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
        PaintApi api = getPaintApi();
        return api.listShapes();
    }

    @Path("shapes")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addShape(ShapeDTO shapeDTO) {
        PaintApi api = getPaintApi();
        api.addShape(shapeDTO);
    }

    @Path("shapes")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteShape(@QueryParam("name") String name,
            @QueryParam("x") int x, @QueryParam("y") int y) {
        ShapeDTO shapeDTO = new ShapeDTO(name, x, y);
        PaintApi api = getPaintApi();
        api.deleteShape(shapeDTO);
    }

    private PaintApi getPaintApi() {
        return (PaintApi) context.getAttribute(PaintApi.class.getName());
    }

}
