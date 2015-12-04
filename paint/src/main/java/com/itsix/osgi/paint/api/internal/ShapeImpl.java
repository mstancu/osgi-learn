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
        this.x = bounds.x;
        this.y = bounds.y;
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

}
