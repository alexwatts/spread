package com.alwa.spread;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Spreader<T> {

    private static final Logger LOGGER = Logger.getLogger(Spreader.class.getName());

    private Integer steps;
    private Callable<T> factoryTemplate;
    private List<MutatorTemplateAndParameters> mutatorTemplateAndParameters;
    private Spread[] factoryParameters;
    private Boolean debug;

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
        if (isDebugMode()) {
            printSplash();
            printSummary();
        }
        validateSpread();
        initialiseFactorySpreads();
        initialiseMutatorSpreads();
        if (isDebugMode()) {
            printUnmappedValueMatrix();
        }
        Object[] dataObjects = new Object[steps];
        for (int i = 0; i < steps; i++) {
            dataObjects[i] = createNextObject();
        }
        return (Stream<T>)Arrays.stream(dataObjects);
    }

    private void printSplash() {
        String message ="\n" +
                        "   ____                             __\n" +
                        "  / __/   ___   ____ ___  ___ _ ___/ /\n" +
                        "  _\\ \\   / _ \\ / __// -_)/ _ `// _  / \n" +
                        " /___/  / .__//_/   \\__/ \\_,_/ \\_,_/  \n" +
                        "       /_/                            \n";
        LOGGER.info(message);
    }

    private void printSummary() {
        LOGGER.info("\n");
        LOGGER.info(String.format("Factory template :[%s]\n", factoryTemplate));
        if (factoryParameters == null || factoryParameters.length == 0) {
            LOGGER.info(String.format("No Factory injector.s\n"));
        } else {
            LOGGER.info(String.format("Factory injectors :[\n%s\n]\n", Arrays.stream(factoryParameters).map(Spread::toString).collect(Collectors.joining(" \n"))));
        }
        if (mutatorTemplateAndParameters == null) {
            LOGGER.info(String.format("No Mutator injectors.\n"));
        } else {
            LOGGER.info(String.format("Mutator injectors :[\n%s\n]\n", mutatorTemplateAndParameters.stream().map(MutatorTemplateAndParameters::toString).collect(Collectors.joining(" \n"))));
        }
    }

    private void printUnmappedValueMatrix() {
        LOGGER.info("\n");
        LOGGER.info("Spread will use these data arrays to feed though factory injectors....\n");
        StringBuilder factoryValueMatrix = new StringBuilder();
        factoryValueMatrix.append("\n");
        if (factoryParameters.length > 0) {
            factoryValueMatrix.append(
                Arrays.stream(factoryParameters).map(param -> " |----------------Spread----------------| ").collect(Collectors.joining())
            );
            factoryValueMatrix.append("\n");
            IntStream.range(0, steps).forEach(i -> factoryValueMatrix.append(centeredRow(factoryParameters, i)));
            LOGGER.info(factoryValueMatrix.toString());
        } else {
            LOGGER.info("Empty factory injectors, no Spreads....\n");
        }

        LOGGER.info("\n");
        LOGGER.info("Spread will use these data arrays to feed though mutator methods....\n");

        StringBuilder mutatorValueMatrix = new StringBuilder();
        mutatorValueMatrix.append("\n");
        if (mutatorTemplateAndParameters != null && !mutatorTemplateAndParameters.isEmpty()) {
            mutatorTemplateAndParameters.forEach(mutatorTemplateAndParameter -> {
                LOGGER.info(String.format("Mutator....[{%s}]\n", mutatorTemplateAndParameter.getMutatorTemplate()));
                mutatorValueMatrix.append(
                    Arrays.stream(mutatorTemplateAndParameter.getParameters()).map(param -> " |----------------Spread----------------| ").collect(Collectors.joining())
                );
                mutatorValueMatrix.append("\n");
                IntStream.range(0, steps).forEach(i -> mutatorValueMatrix.append(centeredRow(mutatorTemplateAndParameter.getParameters(), i)));
            });
            LOGGER.info(mutatorValueMatrix.toString());
        }
    }

    private String centeredRow(Spread[] factoryParameters, int rowNumber) {
        String centeredRow = Arrays.stream(factoryParameters)
            .map(spread -> centerString(40, spread.getValues()[rowNumber].toString()))
            .collect(Collectors.joining(" "));
        return centeredRow + "\n";
    }

    public static String centerString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    public Spreader<T> mutator(Consumer<T> setterTemplate) {
        captureMutatorTemplateAndParameters(setterTemplate.getClass().getDeclaredFields(), setterTemplate);
        return this;
    }

    public Spreader<T> debug() {
        this.debug = true;
        return this;
    }

    private boolean isDebugMode() {
        return debug != null && debug;
    }

    private void validateSpread() {
        validateFactoryTemplate();
        validateSteps();
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

    private void validateSteps() {
        if (steps == null) {
            String message = "Spreader spread() failure, missing steps. You may need to add a step definition to define how many objects to spread.";
            throw new SpreaderException(message);
        } else if (steps <= 0) {
            String message = "Spreader spread() failure, steps Invalid. Steps must be defined as a positive integer and defines how many objects to spread. Invalid Steps: [%s]";
            throw new SpreaderException(String.format(message, steps));
        }
    }

}


