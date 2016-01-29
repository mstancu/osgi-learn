package com.itsix.osgi.paint.api.internal;

import java.awt.Rectangle;

import com.itsix.osgi.paint.api.Shape;

public class ShapeImpl implements Shape {

    private String name;
    private int x;
    private int y;
    private int width;
    private int height;

    public ShapeImpl(String name, Rectangle bounds) {
        this.name = name;
        this.x = bounds.x + bounds.width / 2;
        this.y = bounds.y + bounds.height / 2;
        this.width = bounds.width;
        this.height = bounds.height;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Shape) {
            Shape other = (Shape) obj;
            return getName().equals(other.getName()) && getX() == other.getX()
                    && getY() == other.getY();
        }
        return false;
    }

}
