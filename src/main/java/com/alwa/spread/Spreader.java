package com.alwa.spread;

import java.lang.reflect.Field;
import java.util.*;
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
    private List<MutatorTemplateAndParameters<T>> mutatorTemplateAndParameters;
    private List<Spread<T>> factoryParameters;
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
        Object[] dataObjects =
            (isCollection() || isMap()) ?
                getCollectionDataObjects() :
                    getNonCollectionDataObjects();

        return (Stream<T>)Arrays.stream(dataObjects);
    }

    public Integer getSteps() {
        return steps;
    }

    private boolean isCollection() {
        String factoryTemplateClassName = factoryTemplate.getClass().getName();
        return factoryTemplateClassName.startsWith("com.alwa.spread.ListSpread") ||
            factoryTemplateClassName.startsWith("com.alwa.spread.SetSpread");
    }

    private boolean isMap() {
        String factoryTemplateClassName = factoryTemplate.getClass().getName();
        return factoryTemplateClassName.startsWith("com.alwa.spread.MapSpread");
    }

    private Object[] getNonCollectionDataObjects() {
        Object[] dataObjects = new Object[steps];
        for (int i = 0; i < steps; i++) {
            dataObjects[i] = createNextObject();
        }
        return dataObjects;
    }

    private Object[] getCollectionDataObjects() {
        try {
            Object[] dataObjects = new Object[1];
            Callable<T> factoryTemplate = this.factoryTemplate;
            T collectionObject = factoryTemplate.call();
            for (int i = 0; i < steps; i++) {
                applyMutators(collectionObject);
            }
            dataObjects[0] = collectionObject;
            return dataObjects;
        } catch (Exception e) {
            throw new SpreaderException("Exception thrown whilst creating next object", e);
        }
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
        if (factoryParameters == null || factoryParameters.size() == 0) {
            LOGGER.info("No Factory injector.s\n");
        } else {
            LOGGER.info(String.format("Factory injectors :[\n%s\n]\n", factoryParameters.stream().map(Spread::toString).collect(Collectors.joining(" \n"))));
        }
        if (mutatorTemplateAndParameters == null) {
            LOGGER.info("No Mutator injectors.\n");
        } else {
            LOGGER.info(String.format("Mutator injectors :[\n%s\n]\n", mutatorTemplateAndParameters.stream().map(MutatorTemplateAndParameters::toString).collect(Collectors.joining(" \n"))));
        }
    }

    private void printUnmappedValueMatrix() {
        LOGGER.info("\n");
        LOGGER.info("Spread will use these data arrays to feed though factory injectors....\n");
        StringBuilder factoryValueMatrix = new StringBuilder();
        factoryValueMatrix.append("\n");
        if (factoryParameters.size() > 0) {
            factoryValueMatrix.append(
                factoryParameters.stream().map(param -> " |----------------Spread----------------| ").collect(Collectors.joining())
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
                    mutatorTemplateAndParameter.getParameters().stream().map(param -> " |----------------Spread----------------| ").collect(Collectors.joining())
                );
                mutatorValueMatrix.append("\n");
                IntStream.range(0, steps).forEach(i -> mutatorValueMatrix.append(centeredRow(mutatorTemplateAndParameter.getParameters(), i)));
            });
            LOGGER.info(mutatorValueMatrix.toString());
        }
    }

    private String centeredRow(List<Spread<T>> factoryParameters, int rowNumber) {
        String centeredRow = factoryParameters.stream()
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
        Callable<T> factoryTemplate = this.factoryTemplate;
        try {
            T nextObject = factoryTemplate.call();
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
        List<Spread<T>> factoryParameters = new ArrayList<>();
        for (int i = 0; i < factoryArguments.length; i++) {
            factoryArguments[i].setAccessible(true);
            try {
                Object factoryParameter = factoryArguments[i].get(factoryTemplate);
                factoryParameters.addAll(spreadsFromParameter(factoryParameter, factoryTemplate));
            } catch (IllegalAccessException e) {
                throw new SpreaderException("IllegalAccessException when trying to capture Spread parameters", e);
            }
        }
        this.factoryParameters = factoryParameters;
    }

    private List<Spread<T>> spreadsFromParameter(Object parameter, Callable<T> factoryTemplate) {
        List<Spread<T>> spreadList = new ArrayList<>();
        if (parameter instanceof Spread) {
            spreadList.add((Spread<T>)parameter);
        } else {
            spreadList.addAll(getNestedSpreads(parameter));
        }
        return spreadList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Spread<T>> spreadsFromParameter(Object parameter, Object factoryTemplate) {
        List<Spread<T>> spreadList = new ArrayList<>();
        if (parameter instanceof Spread) {
            spreadList.add((Spread<T>)parameter);
        } else {
            getNestedSpreads(parameter);
        }
        return spreadList;
    }

    private List<Spread<T>> getNestedSpreads(Object parameter) {
        List<Spread<T>> spreadList = new ArrayList<>();
        Field[] attributes = parameter.getClass().getDeclaredFields();
        for (Field field: attributes) {
            if (field.getType().isAssignableFrom(Spread.class)) {
                spreadList.add(getSpreadField(field, parameter));
            }
        }
        return spreadList;
    }

    private Spread<T> getSpreadField(Field field, Object parameter) {
        try {
            field.setAccessible(true);
            return (Spread<T>)field.get(parameter);
        } catch (IllegalAccessException e) {
            throw new SpreaderException("There was a problem getting a Spread field when capturing params");
        }
    }

    private void captureMutatorTemplateAndParameters(Field[] mutatorArguments, Consumer<T> mutatorTemplate) {
        List<Spread<T>> mutatorParameters = new ArrayList<>();
        for (int i = 0; i < mutatorArguments.length; i++) {
            mutatorArguments[i].setAccessible(true);
            try {
                Object factoryParameter = mutatorArguments[i].get(mutatorTemplate);
                mutatorParameters.addAll(spreadsFromParameter(factoryParameter, mutatorTemplate));
            } catch (IllegalAccessException e) {
                throw new SpreaderException("IllegalAccessException when trying to capture Spread parameters", e);
            }
        }
        if (mutatorTemplateAndParameters == null) {
            mutatorTemplateAndParameters = new ArrayList<>();
        }
        mutatorTemplateAndParameters.add(new MutatorTemplateAndParameters<>(mutatorTemplate, mutatorParameters));
    }

    private void initialiseFactorySpreads() {
        this.factoryParameters
            .stream()
             .forEach(spread -> spread.init(steps));
    }

    private void initialiseMutatorSpreads() {
        if (mutatorTemplateAndParameters != null) {
            mutatorTemplateAndParameters.stream()
                    .map(MutatorTemplateAndParameters::getParameters)
                    .filter(Objects::nonNull)
                    .forEach(this::initialiseMutatorParams);
        }
    }

    private void initialiseMutatorParams(List<Spread<T>> mutatorParams) {
        mutatorParams
            .stream()
                .forEach(spread -> spread.init(steps));
    }

    private Object applyMutators(T nextObject) {
        if (mutatorTemplateAndParameters != null) {
            for (MutatorTemplateAndParameters<T> mutatorTemplateAndParameters: this.mutatorTemplateAndParameters) {
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


