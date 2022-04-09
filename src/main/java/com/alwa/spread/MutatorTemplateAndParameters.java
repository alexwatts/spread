package com.alwa.spread;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return String.format("MutatorTemplateAndParameters{" +
            "mutatorTemplate=" + mutatorTemplate +
            ", parameters=[\n    %s\n]" +
        "}", Arrays.stream(parameters).map(Spread::toString).collect(Collectors.joining(" \n")));
    }
}