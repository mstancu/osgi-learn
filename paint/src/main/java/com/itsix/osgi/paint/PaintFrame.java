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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import com.itsix.osgi.paint.api.PaintApi;
import com.itsix.osgi.paint.api.Shape;
import com.itsix.osgi.paint.api.internal.ShapeImpl;
import com.itsix.osgi.shape.SimpleShape;

/**
 * This class represents the main application class, which is a JFrame subclass
 * that manages a toolbar of shapes and a drawing canvas. This class does not
 * directly interact with the underlying OSGi framework; instead, it is injected
 * with the available <tt>SimpleShape</tt> instances to eliminate any
 * dependencies on the OSGi application programming interfaces.
 **/
@org.apache.felix.ipojo.annotations.Component(immediate = true, publicFactory = false)
@Instantiate
@Provides(specifications = PaintApi.class)
public class PaintFrame extends JFrame
        implements MouseListener, MouseMotionListener, PaintApi {
    private static final long serialVersionUID = 1L;
    private static final int SHAPE_SIZE = 54;
    private JToolBar toolbar;
    private String selectedShapeName;
    private JPanel contentPanel;
    private ShapeComponent m_selectedComponent;
    private Map<String, DefaultShape> shapesMap = new HashMap<String, DefaultShape>();
    private ActionListener shapeActionListener = new ShapeActionListener();
    private SimpleShape defaultShape = new DefaultShape();

    /**
     * Default constructor that populates the main window.
     **/
    public PaintFrame() {
        super("PaintFrame");
        System.out.println("Creating paint frame");
        toolbar = new JToolBar("Toolbar");
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(null);
        contentPanel.setMinimumSize(new Dimension(400, 400));
        contentPanel.addMouseListener(this);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setSize(400, 400);
    }

    @Validate
    protected void activate() {
        SwingUtils.invokeAndWait(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    @Invalidate
    protected void deactivate() {
        SwingUtils.invokeLater(new Runnable() {

            public void run() {
                setVisible(false);
                dispose();
            }
        });
    }

    public Collection<Shape> listShapes() {
        List<Shape> shapes = new ArrayList<>();
        Component[] components = contentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof ShapeComponent) {
                ShapeComponent shapeComponent = (ShapeComponent) component;
                Rectangle bounds = component.getBounds();
                shapes.add(new ShapeImpl(shapeComponent.getName(), bounds));
            }
        }
        return shapes;
    }

    public void addShape(Shape shape) {
    }

    /**
     * Injects an available <tt>SimpleShape</tt> into the drawing frame.
     *
     * @param name
     *            The name of the injected <tt>SimpleShape</tt>.
     * @param icon
     *            The icon associated with the injected <tt>SimpleShape</tt>.
     * @param shape
     *            The injected <tt>SimpleShape</tt> instance.
     **/
    @Bind(aggregate = true, optional = true)
    public void bindShape(SimpleShape shape, Map attrs) {
        System.out.println("Binding new shape");
        final DefaultShape delegate = new DefaultShape(shape);
        final String name = (String) attrs.get(SimpleShape.NAME_PROPERTY);
        final Icon icon = (Icon) attrs.get(SimpleShape.ICON_PROPERTY);

        if (name == null || icon == null)
            return;

        shapesMap.put(name, delegate);

        SwingUtils.invokeAndWait(new Runnable() {
            public void run() {
                JButton button = new JButton(icon);
                button.setActionCommand(name);
                button.setToolTipText(name);
                button.addActionListener(shapeActionListener);

                if (selectedShapeName == null) {
                    button.doClick();
                }

                toolbar.add(button);
                toolbar.validate();
                repaint();
            }
        });
    }

    /**
     * Removes a no longer available <tt>SimpleShape</tt> from the drawing
     * frame.
     *
     * @param name
     *            The name of the <tt>SimpleShape</tt> to remove.
     **/
    @Unbind(aggregate = true)
    public void unbindShape(SimpleShape shape, Map attrs) {
        final String name = (String) attrs.get(SimpleShape.NAME_PROPERTY);
        if (name == null)
            return;

        DefaultShape delegate = null;

        synchronized (shapesMap) {
            delegate = shapesMap.remove(name);
        }

        if (delegate != null) {
            delegate.dispose();
            SwingUtils.invokeAndWait(new Runnable() {
                public void run() {
                    if ((selectedShapeName != null)
                            && selectedShapeName.equals(name)) {
                        selectedShapeName = null;
                    }

                    for (int i = 0; i < toolbar.getComponentCount(); i++) {
                        JButton sb = (JButton) toolbar.getComponent(i);
                        if (sb.getActionCommand().equals(name)) {
                            toolbar.remove(i);
                            toolbar.invalidate();
                            validate();
                            repaint();
                            break;
                        }
                    }

                    if ((selectedShapeName == null)
                            && (toolbar.getComponentCount() > 0)) {
                        ((JButton) toolbar.getComponent(0)).doClick();
                    }
                }
            });
        }
    }

    /**
     * This method sets the currently selected shape to be used for drawing on
     * the canvas.
     *
     * @param name
     *            The name of the shape to use for drawing on the canvas.
     **/
    public void selectShape(String name) {
        selectedShapeName = name;
    }

    /**
     * Retrieves the available <tt>SimpleShape</tt> associated with the given
     * name.
     *
     * @param name
     *            The name of the <tt>SimpleShape</tt> to retrieve.
     * @return The corresponding <tt>SimpleShape</tt> instance if available or
     *         <tt>null</tt>.
     **/
    public SimpleShape getShape(String name) {
        SimpleShape shape = shapesMap.get(name);
        if (shape == null) {
            return defaultShape;
        } else {
            return shape;
        }
    }

    /**
     * Implements method for the <tt>MouseListener</tt> interface to draw the
     * selected shape into the drawing canvas.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseClicked(MouseEvent evt) {
        if (selectedShapeName == null) {
            return;
        }

        if (contentPanel.contains(evt.getX(), evt.getY())) {
            ShapeComponent sc = new ShapeComponent(this, selectedShapeName);
            sc.setBounds(evt.getX() - SHAPE_SIZE / 2,
                    evt.getY() - SHAPE_SIZE / 2, SHAPE_SIZE, SHAPE_SIZE);
            contentPanel.add(sc, 0);
            contentPanel.validate();
            contentPanel.repaint(sc.getBounds());
        }
    }

    /**
     * Implements an empty method for the <tt>MouseListener</tt> interface.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseEntered(MouseEvent evt) {
    }

    /**
     * Implements an empty method for the <tt>MouseListener</tt> interface.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseExited(MouseEvent evt) {
    }

    /**
     * Implements method for the <tt>MouseListener</tt> interface to initiate
     * shape dragging.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mousePressed(MouseEvent evt) {
        Component c = contentPanel.getComponentAt(evt.getPoint());
        if (c instanceof ShapeComponent) {
            m_selectedComponent = (ShapeComponent) c;
            contentPanel
                    .setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            contentPanel.addMouseMotionListener(this);
            m_selectedComponent.repaint();
        }
    }

    /**
     * Implements method for the <tt>MouseListener</tt> interface to complete
     * shape dragging.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseReleased(MouseEvent evt) {
        if (m_selectedComponent != null) {
            contentPanel.removeMouseMotionListener(this);
            contentPanel.setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            m_selectedComponent.setBounds(evt.getX() - SHAPE_SIZE / 2,
                    evt.getY() - SHAPE_SIZE / 2, SHAPE_SIZE, SHAPE_SIZE);
            m_selectedComponent.repaint();
            m_selectedComponent = null;
        }
    }

    /**
     * Implements method for the <tt>MouseMotionListener</tt> interface to move
     * a dragged shape.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseDragged(MouseEvent evt) {
        m_selectedComponent.setBounds(evt.getX() - SHAPE_SIZE / 2,
                evt.getY() - SHAPE_SIZE / 2, SHAPE_SIZE, SHAPE_SIZE);
    }

    /**
     * Implements an empty method for the <tt>MouseMotionListener</tt>
     * interface.
     *
     * @param evt
     *            The associated mouse event.
     **/
    public void mouseMoved(MouseEvent evt) {
    }

    /**
     * Simple action listener for shape tool bar buttons that sets the drawing
     * frame's currently selected shape when receiving an action event.
     **/
    private class ShapeActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            selectShape(evt.getActionCommand());
        }
    }

}
