package com.itsix.osgi.paint.rest;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.http.HttpContext;

import com.itsix.osgi.paint.api.PaintApi;
import com.itsix.osgi.paint.api.Shape;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Component
@Instantiate
public class RestOsgiManager {

    private static final Integer LOAD_ON_STARTUP = 1;
    private static final Boolean ASYNC_SUPPORTED = true;

    @Requires(optional = true)
    PaintApi paintApi;

    @Requires(optional = true)
    private WebContainer webContainer;

    //    private HttpContext defaultHttpContext;
    private ServletContainer servletContainer;

    @Validate
    public void start() {
        try {
            Collection<Shape> shapes = paintApi.listShapes();
            System.out.println("There are currently " + shapes.size()
                    + " shapes available");
        } catch (RuntimeException e) {
            System.out.println("Paint API is not currently available");
        }

        HttpContext defaultHttpContext = webContainer
                .createDefaultHttpContext();
        servletContainer = new ServletContainer();
        Dictionary<String, String> jerseyInitParams = new Hashtable<String, String>();
        jerseyInitParams.put("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        jerseyInitParams.put(
                "com.sun.jersey.config.property.resourceConfigClass",
                "com.sun.jersey.api.core.ClassNamesResourceConfig");
        String classNames = "com.itsix.osgi.paint.rest.Service";
        jerseyInitParams.put("com.sun.jersey.config.property.classnames",
                classNames);
        jerseyInitParams.put("com.sun.jersey.config.feature.DisableWADL",
                "true");
        try {
            webContainer.registerServlet(servletContainer,
                    new String[] { "/paint/*" }, jerseyInitParams,
                    LOAD_ON_STARTUP, ASYNC_SUPPORTED, defaultHttpContext);
            servletContainer.getServletContext()
                    .setAttribute(PaintApi.class.getName(), paintApi);
        } catch (ServletException e) {
        }
    }

    @Invalidate
    public void stop() {
        servletContainer.getServletContext()
                .removeAttribute(PaintApi.class.getName());
        webContainer.unregisterServlet(servletContainer);
    }

}
