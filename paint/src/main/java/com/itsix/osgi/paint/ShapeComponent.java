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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import com.itsix.osgi.shape.SimpleShape;

/**
 * Simple component class used to represent a drawn shape. This component uses a
 * <tt>SimpleShape</tt> to paint its contents.
 **/
public class ShapeComponent extends JComponent {
    private static final long serialVersionUID = 1L;
    private PaintFrame frame;
    private String shapeName;

    /**
     * Construct a component for the specified drawing frame with the specified
     * named shape. The component acquires the named shape from the drawing
     * frame at the time of painting, which helps it account for dynamism.
     * 
     * @param frame
     *            The drawing frame associated with the component.
     * @param shapeName
     *            The name of the shape to draw.
     **/
    public ShapeComponent(PaintFrame frame, String shapeName) {
        this.frame = frame;
        this.shapeName = shapeName;
    }

    /**
     * Paints the contents of the component. The component acquires the named
     * shape from the drawing frame at the time of painting, which helps it
     * account for dynamism.
     * 
     * @param g
     *            The graphics object to use for painting.
     **/
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        SimpleShape shape = frame.getShape(shapeName);
        shape.draw(g2, new Point(getWidth() / 2, getHeight() / 2));
    }

    @Override
    public String getName() {
        return shapeName;
    }

}
