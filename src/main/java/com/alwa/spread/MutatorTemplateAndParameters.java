package com.alwa.spread;

import java.util.function.Consumer;

public class MutatorTemplateAndParameters<T> {

    private Consumer<T> mutatorTemplate;
    private Spread[] parameters;

    public MutatorTemplateAndParameters(Consumer<T> mutatorTemplate, Spread[] parameters) {
        this.mutatorTemplate = mutatorTemplate;
        this.parameters = parameters;
    }

    public Consumer<T> getMutatorTemplate() {
        return mutatorTemplate;
    }

    public Spread[] getParameters() {
        return parameters;
    }

}
