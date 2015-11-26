package com.itsix.osgi.shape.circle;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.itsix.osgi.shape.SimpleShape;

public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(SimpleShape.NAME_PROPERTY, "Circle");
		properties.put(SimpleShape.ICON_PROPERTY, new ImageIcon(context.getBundle().getResource("circle.png")));
		context.registerService(SimpleShape.class.getName(), new Circle(), properties);
	}

	public void stop(BundleContext context) throws Exception {
	}
}
