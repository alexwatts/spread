# Spread
Spread is an ergonomic helper library for building test objects quickly and fluently without needing to write or generate builders.
Spread tries to preserve full control of the generated objects whilst also doing what you would expect where things are unspecified.

Spread can make very readable tests quickly, because it allows for isolated named definitions that can be reused and shared and injected into test objects without writing methods to handle sequence and binding

It also has some useful features that allow you to define target values with a cumulative total, and supports nesting of Complex types as well as nested Collection/Map types

You can use it in tests to generate your test objects which can help to make that code a bit more succint/readable/standardised in your Unit or Integeration tests

As well as the below documentation, there is an [examples project](https://github.com/alexwatts/spread-examples) that contains many different more realistic usages of Spread

## Getting Started
You can find <code>Spread</code> on Maven central and import it into a Maven or Gradle project with:

#### Maven
    <dependency>
        <groupId>io.github.alexwatts</groupId>
        <artifactId>spread</artifactId>
        <version>1.0.7</version>
        <scope>test</scope>
    </dependency>

#### Gradle
    testImplementation 'io.github.alexwatts:spread:1.0.7'

#### Usage
You can define a spread of values to be injected into test objects via constructor/factory method/mutatator methods or public fields.

For example, for a range of <code>LocalDateTime</code> values incrementing by hour:

```java
Spread<LocalDateTime> everyHour = 
      SpreadUtil
        .initial(LocalDateTime.MIN)
        .step(previousDate -> previousDate.plusHours(1));
``` 

You can also define 'cumulative' Spreads. The majority of Java number types are supported. for example, you can define a series of <code>BigDecimal</code> values with a cumulative total of 10000:

```java
Spread<BigDecimal> cumulativeReadings = SpreadUtil.cumulative(BigDecimal.valueOf(10000));
```    
If you need to change the type of the <code>Spread</code> you can map a spread to any other type eg:

```java
Spread<Instant> everyHour = 
      SpreadUtil
        .initial(LocalDateTime.MIN)
        .step(previousDate -> previousDate.plusHours(1))
        .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));
```
    
To inject a <code>Spread</code> into some test objects you can use a <code>Spreader</code> object and wrap constructor arguments with <code>Spread.in()</code> to inject <code>Spread</code> instances into your test object. <code>Spreader</code> will generate one object for every step

For example a simple object representing electricity readings with a constructor:

```java
public ElectricityReading(Instant timeField, BigDecimal bigDecimalField) {
    this.timeField = timeField;
    this.bigDecimalField = bigDecimalField;
}
```

Could be populated with data like this:

```java    
List<ElectricityReading> readings =
        new Spreader<ElectricityReading>()
            .factory(
                () -> new ElectricityReading(
                             Spread.in(everyHour), 
                             Spread.in(cumulativeReadings)
                      )
            )
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
```
                
This works in a similar way for factory methods:

```java
List<ElectricityReading> readings =
        new Spreader<ElectricityReading>()
            .factory(
                () -> ElectricityReading.newInstance(
                         Spread.in(everyHour), 
                         Spread.in(cumulativeReadings)
                      )
            )
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
``` 
                
 And where constructor or factory methods are unavailable, via mutator methods:

```java
 List<ElectricityReading> readings =
        new Spreader<ElectricityReading>()
            .factory(
                () -> ElectricityReading::new
            )
            .mutators(
                electricityReading -> electricityReading.setReadingTime(Spread.in(everyHour)),
                electricityReading -> electricityReading.setReading(Spread.in(cumulativeReadings))
            )
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
```

You can inject via public fields as below

```java
 Spread<BigDecimal> cumulativeReadings =
        SpreadUtil.cumulative(BigDecimal.valueOf(70000));

 List<TestDataObject> dataObjects =
     new Spreader<TestDataObject>()
         .factory(TestDataObject::new)
         .mutators(testDataObject -> testDataObject.publicBigDecimalField = Spread.in(cumulativeReadings))
         .steps(24 * 7)
         .spread()
         .collect(Collectors.toList());
```
                
The <code>spread()</code> method of the <code>Spreader</code> class returns a <code>Stream</code> of the target object type, meaning that we are free to collect the resultant stream into any desired groupings or collection types available to us via the Java stream API features, eg. <code>Map</code> <code>Set</code> etc.             

## Other Features

### Sequence
You can define a sequence of values in a <code>Spread</code> which will feed in and repeat for as many steps are defined eg.

```java
Spread<String> threeNames =
        SpreadUtil.sequence(
            "John",
            "Emma",
            "Sophie"
        );
```

The argument list for <code>sequence()</code> is a <code>varargs</code> so it's simple to specify dynamic ranges, eg. below, a range of 1000 <code>Integer</code>:

```java
Spread<Integer> rangeOfIntegers =
    SpreadUtil.sequence(
        IntStream.range(0, 1000)
            .boxed()
            .toArray(Integer[]::new)
    );
```

### Fixed
You can define fixed values which will fill as specified, for as many steps as specified eg:

```java
Spread<Double> fixedDouble = SpreadUtil.fixed(1.6d);
```

### Custom
You can define your own logic to fill values for the defined steps eg:

```java    
Spread<String> randomString = SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));
```

### Debug
The <code>Spreader</code> accepts a mode for debug. You can add a <code>.debug()</code> call to the <code>Spreader</code> chain to enable additional logging, including a tabular print of the string representation of the values being fed into objects. eg:

```java
List<Foo> foo =
        new Spreader<Foo>()
            .factory(Foo::new)
            .mutators(
                foo -> foo.setBar(Spread.in(bar))
            )
            .steps(20)
            .debug()
            .spread()
            .collect(Collectors.toList());
```

### Cumulative
The cumulative feature tries to feed values in as uniform a way as possible. Meaning that it strives to try to keep all values across a dataset as close as is possible, ideally, all the same value. This is not always possible tho depending on the cumulative value and the number of specified steps. If there is any fractional part of a cumulative target to be distributed across test objects, <code>Spreader</code> tries to break up a fractional portion of a number into 'fractional atoms' before spreading these across test objects. The default fractional atom is 0.01 but can be specified where required. Additionally, the <code>RoundingMode</code> can be configured. eg:

```java
Spread<BigDecimal> cumulativeReadings =
        SpreadUtil.cumulative(
            BigDecimal.valueOf(10000),
            RoundingMode.DOWN, //rounding mode
            BigDecimal.valueOf(0.01d) //fractional atom
        );
```

### Related
You can define a <code>Spread</code> that is based on the values of another 'related' spread. For instance, you can define a spread that will evaluate its value based on the step value of another spread. For instance as below a <code>Boolean</code> value that feeds in <code>true</code> if the step value of the related spread starts with an 'a' eg:

```java
Spread<String> threeLetterSpread =
        SpreadUtil.sequence("a", "b", "c");

Spread<Boolean> startsWithAnA =
    SpreadUtil.
        related(threeLetterSpread)
        .step(relatedValue -> relatedValue.startsWith("a"));
```

### Nesting Collection types
If you need to inject collection, or map types into a Test Object, you can wrap a <code>Spread</code> using a collection helper from <code>SpreadUtil</code>. You need to specify a nested number of steps, and <code>Spreader</code> will nest as many elements as specified steps into each generated test object

#### Lists
For example as below, where a nested <code>List<BigDecimal></code> containing 6 elements is nested in to each of the 168 test Objects

```java
Spread<List<BigDecimal>> cumulativeReadingsListed =
        SpreadUtil.list(
            SpreadUtil.cumulative(BigDecimal.valueOf(70000)),
            6
        );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
            .factory(TestDataObject::new)
            .mutators(testDataObject -> testDataObject.setListField(Spread.in(cumulativeReadingsListed)))
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
```   

#### Sets
For example as below, where a nested <code>Set<BigDecimal></code> containing 6 elements is nested in to each of the 168 test Objects

```java
Spread<Set<Integer>> cumulativeReadingsSetted =
        SpreadUtil.set(
            SpreadUtil.cumulative(6),
            6
        );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setSetField(Spread.in(cumulativeReadingsSetted)))
        .steps(24 * 7)
        .spread()
        .collect(Collectors.toList());
```

#### Maps
For example as below, where a nested <code>Map<String, Integer></code> containing 6 entries is nested in to each of the 168 test Objects

```java
Spread<String> randomMapKey =
    SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

Spread<Integer> cumulativeReadings = SpreadUtil.cumulative(70000);

Spread<Map<String, Integer>> readingsInMap =
    SpreadUtil.map(
        randomMapKey,
        cumulativeReadings,
        6
    );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setMapField(Spread.in(readingsInMap)))
        .steps(24 * 7)
        .spread()
        .collect(Collectors.toList());
```

### Nesting Collection Types with Spreader
As well as nesting simple types into nested collections, you can also nest complex types into nested collections, with <code>Spreader</code>.
For instance, as below, we can create a <code>Spreader</code> in the normal way, but instead of invoking <code>spread()</code> and collecting the target objects into a <code>List</code> or a <code>Map</code>, instead we can pass the Spreader instance to <code>SpreadUtil</code> to obtain a collection <code>Spread</code> that we can then use to nest a complex collection type in another object. This example gives us 1000 <code>TestDataObjects</code> each with a nested List field, with three values in it. The three Strings, "a", "b", "c":

#### Lists
Spread<String> someStrings = SpreadUtil.sequence("a", "b", "c");

```java
Spreader<AnotherTestDataObject> nestedObjectSpreader =
    new Spreader<AnotherTestDataObject>()
        .factory(AnotherTestDataObject::new)
        .mutator(anotherTestDataObject -> anotherTestDataObject.setStringField(Spread.in(someStrings)))
        .steps(3);

Spread<List<AnotherTestDataObject>> nestedObjectsMap =
    SpreadUtil.list(
        nestedObjectSpreader
    );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setNestedObjectListField(Spread.in(nestedObjectsMap)))
        .steps(1000)
        .spread()
        .collect(Collectors.toList());
```

#### Sets
Sets can be nested in almost the same way as lists:

```java
Spread<String> someStrings = SpreadUtil.sequence("a", "b", "c");

Spreader<AnotherTestDataObject> nestedObjectSpreader =
    new Spreader<AnotherTestDataObject>()
        .factory(AnotherTestDataObject::new)
        .mutator(anotherTestDataObject -> anotherTestDataObject.setStringField(Spread.in(someStrings)))
        .steps(3);

Spread<Set<AnotherTestDataObject>> nestedObjectsMap =
    SpreadUtil.set(
        nestedObjectSpreader
    );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setNestedObjectSetField(Spread.in(nestedObjectsMap)))
        .steps(1000)
        .spread()
        .collect(Collectors.toList());
```

#### Maps
You can do almost exact the same thing with Maps, but you need to supply one additional <code>Spreader</code> for the map key:

```java
Spread<String> someStrings = SpreadUtil.sequence("a", "b", "c");

Spreader<AnotherTestDataObject> nestedObjectSpreader =
    new Spreader<AnotherTestDataObject>()
        .factory(AnotherTestDataObject::new)
        .mutator(anotherTestDataObject -> anotherTestDataObject.setStringField(Spread.in(someStrings)))
        .steps(3);

Spread<String> mapKeysSpread =
    SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

Spreader<String> nestedMapKeySpreader =
    new Spreader<String>()
        .factory(() -> String.valueOf(Spread.in(mapKeysSpread)))
        .steps(3);

Spread<Map<String, AnotherTestDataObject>> nestedObjectsMap =
    SpreadUtil.map(
        nestedMapKeySpreader,
        nestedObjectSpreader
    );

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setNestedObjectMapField(Spread.in(nestedObjectsMap)))
        .steps(1000)
        .spread()
        .collect(Collectors.toList());
```
