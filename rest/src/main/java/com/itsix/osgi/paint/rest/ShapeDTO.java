package com.itsix.osgi.paint.rest;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.itsix.osgi.paint.api.Shape;

public class ShapeDTO implements Shape {

    private int x;
    private int y;
    private String name;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public int getHeight() {
        return 0;
    }

    @Override
    @JsonIgnore
    public int getWidth() {
        return 0;
    }

}
