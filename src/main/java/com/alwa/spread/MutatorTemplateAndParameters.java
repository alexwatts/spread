package com.alwa.spread;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MutatorTemplateAndParameters<T> {

    private final Consumer<T> mutatorTemplate;
    private final Spread<T>[] parameters;

    public MutatorTemplateAndParameters(Consumer<T> mutatorTemplate, Spread<T>[] parameters) {
        this.mutatorTemplate = mutatorTemplate;
        this.parameters = parameters;
    }

    public Consumer<T> getMutatorTemplate() {
        return mutatorTemplate;
    }

    public Spread<T>[] getParameters() {
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