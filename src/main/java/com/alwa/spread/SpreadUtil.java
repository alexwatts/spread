package com.alwa.spread;

import com.alwa.spread.annotations.Dynamic;
import com.alwa.spread.annotations.Embed;
import com.alwa.spread.annotations.In;
import com.alwa.spread.core.*;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.alwa.spread.SpreadValidator.validateCumulativeSpread;
import static org.reflections.scanners.Scanners.FieldsAnnotated;

public class SpreadUtil {

    public static List<Spread> injectors;
    public static Map<Spread, Embed> embedContainers;
    public static Map<Spread, Dynamic> dynamicSpreads;

    public static void initPackage(Object container, String packageToScan) {
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackage(packageToScan)
                .setScanners(FieldsAnnotated));

        Set<Field> injectorFields = reflections.get(FieldsAnnotated.with(In.class).as(Field.class));
        Set<Field> embedFields = reflections.get(FieldsAnnotated.with(Embed.class).as(Field.class));
        Set<Field> dynamicFields = reflections.get(FieldsAnnotated.with(Dynamic.class).as(Field.class));
        captureInjectors(container, injectorFields, embedFields, dynamicFields);
    }

    private static void captureInjectors(Object container, Set<Field> fields, Set<Field> embedFields, Set<Field> dynamicFields) {
        List<Spread> injectorList = new ArrayList<>();
        for (Field field: fields) {
            Spread injectorField = getInjectorFieldOrNull(container, field);
            Field embedField = getEmbedFieldOrNull(field, embedFields);
            if (injectorField != null) {
                injectorList.add(injectorField);
                if (embedField != null) {
                    Embed embed = Arrays.stream(embedField.getDeclaredAnnotationsByType(Embed.class)).collect(Collectors.toList()).get(0);
                    if (embedContainers == null) embedContainers = new HashMap<>();
                    embedContainers.put(injectorField, embed);
                }
                Field dynamicField = getDynamicFieldOrNull(field, dynamicFields);
                if (dynamicField != null) {
                    if (dynamicSpreads == null) dynamicSpreads = new HashMap<>();
                    dynamicSpreads.put(injectorField, dynamicField.getAnnotation(Dynamic.class));
                }
            }
        }
        injectors = injectorList;
    }

    private static Field getEmbedFieldOrNull(Field field, Set<Field> embedFields) {
        return embedFields.stream().filter(embedField -> embedField.equals(field)).findFirst().orElse(null);
    }

    private static Field getDynamicFieldOrNull(Field field, Set<Field> dynamicFields) {
        return dynamicFields.stream().filter(embedField -> embedField.equals(field)).findFirst().orElse(null);
    }

    private static Spread getInjectorFieldOrNull(Object container, Field field) {
        try {
            field.setAccessible(true);
            return (Spread) field.get(container);
        } catch (Exception e) {
            return null;
        }
    }

    public static void initialiseInjectors(int steps) {
        injectors
            .forEach(
                injector ->
                    initialiseInjector(steps, injector)
            );
    }

    private static void initialiseInjector(int steps, Spread injector) {
        if (embedContainers != null &&
            embedContainers.get(injector) != null) {
            injector.init(embedContainers.get(injector).steps());
        } else {
            injector.init(steps);
        }
    }

    private static <T> Spread<T> spread(T seedOrExamples) {
        return new Spread<>(null, null, seedOrExamples);
    }

    public static <T> Spread<T> initial(T seed) {
        return spread(seed);
    }

    public static <T> Spread<T> cumulative(
        BigDecimal example,
        RoundingMode roundingMode) {
       return cumulativeSpread(example, roundingMode);
    }

    public static <T> Spread<T> cumulative(T example) {
        validateCumulativeSpread(example);
        return cumulativeSpread(example);
    }

    public static <T>Spread<T> fixed(T example) {
        return fixedSpread(example);
    }

    public static <T>Spread<T> complexType(Spreader complexTypeTemplate) {
        return new ComplexSpread<>(null, null, complexTypeTemplate);
    }

    private static <T> Spread<T> cumulativeSpread(T seed) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(T seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, RoundingMode roundingMode) {
        return new CumulativeSpread<>(null, null, roundingMode, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, roundingMode, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(BigDecimal seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(Double seed, RoundingMode roundingMode) {
        return new CumulativeSpread<>(null, null, roundingMode, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(Double seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, roundingMode, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(Double seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(Float seed, RoundingMode roundingMode) {
        return new CumulativeSpread<>(null, null, roundingMode, BigDecimal.valueOf(0.01), seed);
    }

    private static <T> Spread<T> cumulativeSpread(Float seed, RoundingMode roundingMode, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, roundingMode, fractionalAtom, seed);
    }

    private static <T> Spread<T> cumulativeSpread(Float seed, BigDecimal fractionalAtom) {
        return new CumulativeSpread<>(null, null, RoundingMode.DOWN, fractionalAtom, seed);
    }

    private static <T> Spread<T> fixedSpread(T seed) {
        return new FixedSpread<>(null, null, seed);
    }

    public static <T> Spread<T> sequence(T... examples) {
        return new SequenceSpread<>(null, null, examples);
    }

    public static <T> Spread<T> sequence(Spread<T>... spreads) {
        return new SequenceSpread<>(null, null, spreads);
    }

    public static <T> Spread<T> sequence(List<Spread<T>> spreads) {
        return new SequenceSpread<>(null, null, spreads.toArray());
    }

    public static <T> Spread<T> custom(Supplier<T> functionToCall) {
        return new CustomSpread<>(null, null, functionToCall);
    }

    public static  <T> Spread<T> related(Spread<T> related) {
        return new RelatedSpread<>(null, null, related);
    }

}
