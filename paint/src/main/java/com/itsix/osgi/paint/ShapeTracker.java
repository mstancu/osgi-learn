/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.itsix.osgi.paint;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.itsix.osgi.shape.SimpleShape;

/**
 * Extends the <tt>BundleTracker</tt> to create a tracker for
 * <tt>SimpleShape</tt> extensions. The tracker is responsible for listening for
 * <tt>SimpleShape</tt> extensions and informing the application about the
 * availability of shapes. This tracker forces all notifications to be processed
 * on the Swing event thread to avoid synchronization and redraw issues.
 **/
public class ShapeTracker extends ServiceTracker {
	// Flag indicating an added shape.
	private static final int ADDED = 1;
	// Flag indicating a modified shape.
	private static final int MODIFIED = 2;
	// Flag indicating a removed shape.
	private static final int REMOVED = 3;
	// The bundle context used for tracking.
	private BundleContext context;
	// The application object to notify.
	private PaintFrame frame;

	/**
	 * Constructs a tracker that uses the specified bundle context to track
	 * services and notifies the specified application object about changes.
	 * 
	 * @param context
	 *            The bundle context to be used by the tracker.
	 * @param frame
	 *            The application object to notify about extension changes.
	 **/
	public ShapeTracker(BundleContext context, PaintFrame frame) {
		super(context, SimpleShape.class.getName(), null);
		this.context = context;
		this.frame = frame;
	}

	/**
	 * Overrides the <tt>ServiceTracker</tt> functionality to inform the
	 * application object about the added service.
	 * 
	 * @param ref
	 *            The service reference of the added service.
	 * @return The service object to be used by the tracker.
	 **/
	public Object addingService(ServiceReference ref) {
		SimpleShape shape = new DefaultShape(context, ref);
		processShapeOnEventThread(ADDED, ref, shape);
		return shape;
	}

	/**
	 * Overrides the <tt>ServiceTracker</tt> functionality to inform the
	 * application object about the modified service.
	 * 
	 * @param ref
	 *            The service reference of the modified service.
	 * @param svc
	 *            The service object of the modified service.
	 **/
	public void modifiedService(ServiceReference ref, Object svc) {
		processShapeOnEventThread(MODIFIED, ref, (SimpleShape) svc);
	}

	/**
	 * Overrides the <tt>ServiceTracker</tt> functionality to inform the
	 * application object about the removed service.
	 * 
	 * @param ref
	 *            The service reference of the removed service.
	 * @param svc
	 *            The service object of the removed service.
	 **/
	public void removedService(ServiceReference ref, Object svc) {
		processShapeOnEventThread(REMOVED, ref, (SimpleShape) svc);
		((DefaultShape) svc).dispose();
	}

	/**
	 * Processes a received service notification from the
	 * <tt>ServiceTracker</tt>, forcing the processing of the notification onto
	 * the Swing event thread if it is not already on it.
	 * 
	 * @param action
	 *            The type of action associated with the notification.
	 * @param ref
	 *            The service reference of the corresponding service.
	 * @param shape
	 *            The service object of the corresponding service.
	 **/
	private void processShapeOnEventThread(int action, ServiceReference reference, SimpleShape shape) {
		if ((context.getBundle(0).getState() & (Bundle.STARTING | Bundle.ACTIVE)) == 0) {
			return;
		}

		try {
			if (SwingUtilities.isEventDispatchThread()) {
				processShape(action, reference, shape);
			} else {
				SwingUtilities.invokeAndWait(new BundleRunnable(action, reference, shape));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Actually performs the processing of the service notification. Invokes the
	 * appropriate callback method on the application object depending on the
	 * action type of the notification.
	 * 
	 * @param action
	 *            The type of action associated with the notification.
	 * @param ref
	 *            The service reference of the corresponding service.
	 * @param bundle
	 *            The service object of the corresponding service
	 **/
	private void processShape(int action, ServiceReference ref, SimpleShape shape) {
		String name = (String) ref.getProperty(SimpleShape.NAME_PROPERTY);
		switch (action) {
		case MODIFIED:
			frame.removeShape(name);
			// Will fall through to the ADDED case and reload the shape.
		case ADDED:
			Icon icon = (Icon) ref.getProperty(SimpleShape.NAME_PROPERTY);
			frame.addShape(name, icon, shape);
			break;

		case REMOVED:
			frame.removeShape(name);
			break;
		}
	}

	/**
	 * Simple class used to process service notification handling on the Swing
	 * event thread.
	 **/
	private class BundleRunnable implements Runnable {
		private int action;
		private ServiceReference reference;
		private SimpleShape shape;

		/**
		 * Constructs an object with the specified action, service reference,
		 * and service object for processing on the Swing event thread.
		 * 
		 * @param action
		 *            The type of action associated with the notification.
		 * @param ref
		 *            The service reference of the corresponding service.
		 * @param shape
		 *            The service object of the corresponding service.
		 **/
		public BundleRunnable(int action, ServiceReference reference, SimpleShape shape) {
			this.action = action;
			this.reference = reference;
			this.shape = shape;
		}

		/**
		 * Calls the <tt>processBundle()</tt> method.
		 **/
		public void run() {
			processShape(action, reference, shape);
		}
	}
}
