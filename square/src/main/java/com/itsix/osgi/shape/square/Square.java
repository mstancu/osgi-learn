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
package com.itsix.osgi.shape.square;

import com.itsix.osgi.shape.SimpleShape;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

@Component(immediate = true)
@Provides
public class Square implements SimpleShape {

	@ServiceProperty(name = SimpleShape.NAME_PROPERTY)
	String m_name = "Square";

	@ServiceProperty(name = SimpleShape.ICON_PROPERTY)
	ImageIcon m_icon = new ImageIcon(this.getClass().getResource("square.png"));

	/**
	 * Implements the <tt>SimpleShape.draw()</tt> method for painting the shape.
	 * 
	 * @param g2
	 *            The graphics object used for painting.
	 * @param p
	 *            The position to paint the triangle.
	 **/
	public void draw(Graphics2D g2, Point p) {
		int x = p.x - 25;
		int y = p.y - 25;
		GradientPaint gradient = new GradientPaint(x, y, Color.BLUE, x + 50, y, Color.WHITE);
		g2.setPaint(gradient);
		g2.fill(new Rectangle2D.Double(x, y, 50, 50));
		BasicStroke wideStroke = new BasicStroke(2.0f);
		g2.setColor(Color.black);
		g2.setStroke(wideStroke);
		g2.draw(new Rectangle2D.Double(x, y, 50, 50));
	}
}
