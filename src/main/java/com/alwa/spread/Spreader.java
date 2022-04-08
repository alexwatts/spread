package com.alwa.spread;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Spreader<T> {

    private int steps;
    private Callable<T> factoryTemplate;
    private List<MutatorTemplateAndParameters> mutatorTemplateAndParameters;
    private Spread[] factoryParameters;

    public Spreader() {
    }

    public Spreader<T> factory(Callable<T> factoryTemplate) {
        captureFactorySpreads(factoryTemplate);
        this.factoryTemplate = factoryTemplate;
        return this;
    }

    public Spreader<T> steps(int steps) {
        this.steps = steps;
        return this;
    }

    public Stream<T> spread() {
        validateSpread();
        initialiseFactorySpreads();
        initialiseMutatorSpreads();
        Object[] dataObjects = new Object[steps];
        for (int i = 0; i < steps; i++) {
            dataObjects[i] = createNextObject();
        }
        return (Stream<T>)Arrays.stream(dataObjects);
    }

    private void validateSpread() {
        validateFactoryTemplate();
    }

    private Object createNextObject() {
        Callable factoryTemplate = this.factoryTemplate;
        try {
            T nextObject = (T)factoryTemplate.call();
            return applyMutators(nextObject);
        } catch (Exception e) {
            throw new SpreaderException("Exception thrown whilst creating next object", e);
        }
    }

    private void captureFactorySpreads(Callable<T> factoryTemplate) {
        this.factoryTemplate = factoryTemplate;
        captureFactoryParameters(factoryTemplate.getClass().getDeclaredFields(), factoryTemplate);
    }

    public Spreader<T> mutator(Consumer<T> setterTemplate) {
        captureMutatorTemplateAndParameters(setterTemplate.getClass().getDeclaredFields(), setterTemplate);
        return this;
    }

    @SafeVarargs
    public final Spreader<T> mutators(Consumer<T>... setterTemplates) {
        for (Consumer<T> setterTemplate: setterTemplates) {
            captureMutatorTemplateAndParameters(setterTemplate.getClass().getDeclaredFields(), setterTemplate);
        }
        return this;
    }

    private void captureFactoryParameters(Field[] factoryArguments, Callable<T> factoryTemplate) {
        Spread[] factoryParameters = new Spread[factoryArguments.length];
        for (int i = 0; i < factoryArguments.length; i++) {
            factoryArguments[i].setAccessible(true);
            try {
                Object factoryParameter = factoryArguments[i].get(factoryTemplate);
                factoryParameters[i] = spreadFromParameter(factoryParameter);
            } catch (IllegalAccessException e) {
                throw new SpreaderException("IllegalAccessException when trying to capture Spread parameters", e);
            }
        }
        this.factoryParameters = factoryParameters;
    }

    private Spread<T> spreadFromParameter(Object parameter) {
        if (parameter instanceof Spread) {
            return (Spread<T>)parameter;
        } else {
            return new FixedSpread<>(parameter);
        }
    }

    private void captureMutatorTemplateAndParameters(Field[] mutatorArguments, Consumer<T> mutatorTemplate) {
        Spread[] mutatorParameters = new Spread[mutatorArguments.length];
        for (int i = 0; i < mutatorArguments.length; i++) {
            mutatorArguments[0].setAccessible(true);
            try {
                Object factoryParameter = mutatorArguments[0].get(mutatorTemplate);
                mutatorParameters[i] = spreadFromParameter(factoryParameter);
            } catch (IllegalAccessException e) {
                throw new SpreaderException("IllegalAccessException when trying to capture Spread parameters", e);
            }
        }
        if (mutatorTemplateAndParameters == null) {
            mutatorTemplateAndParameters = new ArrayList<>();
        }
        mutatorTemplateAndParameters.add(new MutatorTemplateAndParameters(mutatorTemplate, mutatorParameters));
    }

    private void initialiseFactorySpreads() {
        Arrays.stream(this.factoryParameters)
                .forEach(spread -> spread.init(steps));
    }

    private void initialiseMutatorSpreads() {
        if (mutatorTemplateAndParameters != null) {
            mutatorTemplateAndParameters.stream()
                    .map(MutatorTemplateAndParameters::getParameters)
                    .forEach(mutatorParams -> initialiseMutatorParams(mutatorParams));
        }
    }

    private void initialiseMutatorParams(Spread[] mutatorParams) {
        Arrays.stream(mutatorParams)
                .forEach(spread -> spread.init(steps));
    }

    private Object applyMutators(T nextObject) {
        if (mutatorTemplateAndParameters != null) {
            for (MutatorTemplateAndParameters mutatorTemplateAndParameters: this.mutatorTemplateAndParameters) {
                mutatorTemplateAndParameters.getMutatorTemplate().accept(nextObject);
            }
        }
        return nextObject;
    }

    private void validateFactoryTemplate() {
        if (factoryTemplate == null) {
            String message = "Spreader spread() failure, missing factory. You may need to add a factory to call a constructor, or a factory method, to create instances.";
            throw new SpreaderException(message);
        }
    }

}


