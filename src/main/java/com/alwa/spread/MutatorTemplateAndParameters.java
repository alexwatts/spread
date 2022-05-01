package com.alwa.spread;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MutatorTemplateAndParameters<T> {

    private final Consumer<T> mutatorTemplate;
    private final List<Spread<T>> parameters;

    public MutatorTemplateAndParameters(Consumer<T> mutatorTemplate, List<Spread<T>> parameters) {
        this.mutatorTemplate = mutatorTemplate;
        this.parameters = parameters;
    }

    public Consumer<T> getMutatorTemplate() {
        return mutatorTemplate;
    }

    public List<Spread<T>> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("MutatorTemplateAndParameters{" +
            "mutatorTemplate=" + mutatorTemplate +
            ", parameters=[\n    %s\n]" +
        "}", parameters.stream().map(Spread::toString).collect(Collectors.joining(" \n")));
    }
}