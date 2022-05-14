package com.alwa.spread;

public class EmbeddedCollection {

    private final Object container;
    private final int steps;

    public EmbeddedCollection(Object container, int steps) {
        this.container = container;
        this.steps = steps;
    }

    public Object getContainer() {
        return container;
    }

    public int getSteps() {
        return steps;
    }

}