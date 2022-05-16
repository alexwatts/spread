package com.alwa.spread;

import com.alwa.spread.core.Spread;
import com.alwa.spread.exception.SpreaderException;

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
    private List<Consumer<T>> mutatorTemplates;
    private Boolean debug;

    public Spreader() {
    }

    public Spreader<T> factory(Callable<T> factoryTemplate) {
        this.factoryTemplate = factoryTemplate;
        return this;
    }

    public Spreader<T> steps(int steps) {
        this.steps = steps;
        return this;
    }

    public T singular() {
        return this.spread().collect(Collectors.toList()).get(0);
    }

    public Stream<T> spread() {
        validateSpread();
        SpreadUtil.initialiseInjectors(steps);

        if (isDebugMode()) {
            printSplash();
            printSummary();
        }

        if (isDebugMode()) {
            printUnmappedValueMatrix();
        }

        Object[] dataObjects =
            getNonCollectionDataObjects();

        return (Stream<T>)Arrays.stream(dataObjects);
    }

    public Integer getSteps() {
        return steps;
    }

    private Object[] getNonCollectionDataObjects() {
        Object[] dataObjects = new Object[steps];
        for (int i = 0; i < steps; i++) {
            dataObjects[i] = createNextObject();
        }
        return dataObjects;
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
        LOGGER.info("\n");

    }

    private void printUnmappedValueMatrix() {
        List<Spread> injectors = SpreadUtil.injectors;
        LOGGER.info("\n");
        LOGGER.info("Spread will use these data arrays to feed though injectors....\n");
        LOGGER.info(String.format("There are %d injectors....\n", injectors.size()));
        StringBuilder valueMatrix = new StringBuilder();
        valueMatrix.append("\n");

        if (injectors.size() > 0) {
            valueMatrix.append(
                injectors.stream().map(param -> " |----------------Spread----------------| ").collect(Collectors.joining())
            );
            valueMatrix.append("\n");
            IntStream.range(0, steps).forEach(i -> valueMatrix.append(centeredRow(injectors.stream().filter(spread -> spread.getValues().length >= steps).collect(Collectors.toList()), i)));
            LOGGER.info(valueMatrix.toString());
        } else {
            LOGGER.info("Empty injectors, no Spreads....\n");
        }

        LOGGER.info("\n");
    }

    private String centeredRow(List<Spread> factoryParameters, int rowNumber) {
        String centeredRow = factoryParameters.stream()
            .map(spread -> centerString(40, spread.getValues()[rowNumber].toString()))
            .collect(Collectors.joining(" "));
        return centeredRow + "\n";
    }

    public static String centerString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    public Spreader<T> mutator(Consumer<T> setterTemplate) {
        if (mutatorTemplates == null) mutatorTemplates = new ArrayList<>();
        mutatorTemplates.add(setterTemplate);
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

    @SafeVarargs
    public final Spreader<T> mutators(Consumer<T>... setterTemplates) {
        if (mutatorTemplates == null) mutatorTemplates = new ArrayList<>();
        mutatorTemplates.addAll(Arrays.asList(setterTemplates));
        return this;
    }

    private Object applyMutators(T nextObject) {
        if (mutatorTemplates != null && !mutatorTemplates.isEmpty()) {
            for (Consumer<T> mutatorTemplate: this.mutatorTemplates) {
                mutatorTemplate.accept(nextObject);
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


