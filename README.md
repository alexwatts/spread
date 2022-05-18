# Spread
Spread is an ergonomic helper library for building test objects. It lets you define ranges of objects, either in a sequence, or a dynamic range, or by a cumulative total. Spread gathers <code>@In</code> definitions and injects values into your test objects for you. It simplifies some of the boiler plate logic you would need to write to construct objects, feed values through in sequence and manage collection types.

As well as the below documentation, there is an [examples project](https://github.com/alexwatts/spread-examples) that contains many usages of Spread

## Getting Started
You can find <code>Spread</code> on Maven central and import it into a Maven or Gradle project with:

#### Maven
    <dependency>
        <groupId>io.github.alexwatts</groupId>
        <artifactId>spread</artifactId>
        <version>2.0.5</version>
        <scope>test</scope>
    </dependency>

#### Gradle
    testImplementation 'io.github.alexwatts:spread:2.0.5'

#### Usage
To use spread, you need to initialise <code>Spread</code> for example, as below, in JUnit5. <code>SpreadUtil.initPackage()</code> takes two arguments. The instance of the test class where the <code>@In</code> annotations are defined, and the package name to scan for <code>@In</code> annotations.

```java
@BeforeEach
public void setUp() {
    SpreadUtil.initPackage(
        this,
        this.getClass().getPackage().getName()
    );
}
```

You can define a spread of values to be injected into test objects via constructor/factory method/mutatator methods or public fields.

For example, for a range of <code>LocalDateTime</code> values incrementing by hour:

```java
@In
private final Spread<LocalDateTime> EVERY_HOUR = 
      SpreadUtil
        .initial(LocalDateTime.MIN)
        .step(previousDate -> previousDate.plusHours(1));
``` 

You can also define 'cumulative' Spreads. The majority of Java number types are supported. for example, you can define a series of <code>BigDecimal</code> values with a cumulative total of 10000:

```java
@In
private final Spread<BigDecimal> READINGS_TOTALING_10000 = SpreadUtil.cumulative(BigDecimal.valueOf(10000));
```    
If you need to change the type of the <code>Spread</code> you can map a spread to any other type eg:

```java
@In
private final Spread<Instant> EVERY_HOUR = 
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
                             Spread.in(EVERY_HOUR), 
                             Spread.in(READINGS_TOTALING_10000)
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
                         Spread.in(EVERY_HOUR), 
                         Spread.in(READINGS_TOTALING_10000)
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
                electricityReading -> electricityReading.setReadingTime(Spread.in(EVERY_HOUR)),
                electricityReading -> electricityReading.setReading(Spread.in(READINGS_TOTALING_10000))
            )
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
```

You can inject via public fields as below

```java
 List<TestDataObject> dataObjects =
     new Spreader<TestDataObject>()
         .factory(TestDataObject::new)
         .mutators(testDataObject -> testDataObject.publicBigDecimalField = Spread.in(READINGS_TOTALING_70000))
         .steps(24 * 7)
         .spread()
         .collect(Collectors.toList());
```
                
The <code>spread()</code> method of the <code>Spreader</code> class returns a <code>Stream</code> of the target object type, meaning that we are free to collect the resultant stream into any desired groupings or collection types available to us via the Java stream API features, eg. <code>Map</code> <code>Set</code> etc.             

You can collect into a <code>Map</code> as below for example

```java
Map<String, BigDecimal> READINGS_MAP =
    new Spreader<Map.Entry<String, BigDecimal>>()
        .factory(() -> Map.entry(Spread.in(MAP_KEYS_SPREAD), Spread.in(READINGS_SPREAD)))
        .steps(5)
        .spread()
        .collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
        );
```

## Other Features

### Sequence
You can define a sequence of values in a <code>Spread</code> which will feed in and repeat for as many steps are defined eg.

```java
@In
private final Spread<String> THREE_NAMES =
        SpreadUtil.sequence(
            "John",
            "Emma",
            "Sophie"
        );
```

The argument list for <code>sequence()</code> is a <code>varargs</code> so it's simple to specify dynamic ranges, eg. below, a range of 1000 <code>Integer</code>:

```java
@In
private final Spread<Integer> RANGE_OF_INTEGERS =
    SpreadUtil.sequence(
        IntStream.range(0, 1000)
            .boxed()
            .toArray(Integer[]::new)
    );
```

### Fixed
You can define fixed values which will fill as specified, for as many steps as specified eg:

```java
@In
private final Spread<Double> FIXED_DOUBLE = SpreadUtil.fixed(1.6d);
```

### Custom
You can define your own logic to fill values for the defined steps eg:

```java    
@In
private final Spread<String> RANDOM_STRING = SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));
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
@In
private final Spread<BigDecimal> READINGS_TOTALING_10000 =
        SpreadUtil.cumulative(
            BigDecimal.valueOf(10000),
            RoundingMode.DOWN, //rounding mode
            BigDecimal.valueOf(0.01d) //fractional atom
        );
```

### Related
You can define a <code>Spread</code> that is based on the values of another 'related' spread. For instance, you can define a spread that will evaluate its value based on the step value of another spread. For instance as below a <code>Boolean</code> value that feeds in <code>true</code> if the step value of the related spread starts with an 'a' eg:

```java
@In
private final Spread<String> THREE_LETTERS =
        SpreadUtil.sequence("a", "b", "c");

@In
private final Spread<Boolean> STARTS_WITH_AN_A =
    SpreadUtil.
        related(threeLetterSpread)
        .step(relatedValue -> relatedValue.startsWith("a"));
```
### Complex Types
You can use all of the features of Spread using Complex Types as well as simple types. For example, as below, you can generate a <code>Spread</code> of <code>AnotherTestDataObject</code> and set each of these as a nested field of another object.

```java
@In
private final Spread<Integer> SOME_INTEGERS = SpreadUtil.sequence(1, 2, 3, 4, 5, 6, 7 ,8, 9);

@In
private final Spread<AnotherTestDataObject> COMPLEX_TYPE_SPREAD =
    SpreadUtil.complexType(
        new Spreader<AnotherTestDataObject>()
            .factory(AnotherTestDataObject::new)
            .mutator(object -> object.setIntField(Spread.in(SOME_INTEGERS)))
    );

...

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
    .factory(TestDataObject::new)
    .mutator(testDataObject -> testDataObject.setNestedObjectField(Spread.in(COMPLEX_TYPE_SPREAD)))
    .steps(9)
    .spread()
    .collect(Collectors.toList());

```

### Nesting Collection types
If you need to inject collection, or map types into a Test Object, you can embed a <code>Spread</code> using the <code>@Embed</code> annotation. You need to specify a nested number of steps and a collection type, and <code>Spreader</code> will nest as many elements as specified steps into each generated test object. You need to replace <code>Spread.in()</code> with <code>Spread.embed()</code> and cast the collection type.


#### Lists
For example as below, where a nested <code>List<BigDecimal></code> containing 6 elements is nested in to each of the 168 test Objects

```java
@In
@Embed(clazz = List.class, steps = 6)
private final Spread<BigDecimal> READINGS_TOTALING_70000 =
    SpreadUtil.cumulative(BigDecimal.valueOf(70000)),


...
    
List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
            .factory(TestDataObject::new)
            .mutators(testDataObject -> testDataObject.setListField((List<BigDecimal>)Spread.embed(READINGS_TOTALING_70000)))
            .steps(24 * 7)
            .spread()
            .collect(Collectors.toList());
```   

#### Sets
For example as below, where a nested <code>Set<BigDecimal></code> containing 6 elements is nested in to each of the 168 test Objects

```java
@In
@Embed(clazz = Set.class, steps = 6)
private final Spread<Integer> READINGS_TOTALLING_6 = SpreadUtil.cumulative(6),

...

List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setSetField((Set<Integer>)Spread.embed(READINGS_TOTALLING_6)))
        .steps(24 * 7)
        .spread()
        .collect(Collectors.toList());
```

#### Maps
For example as below, where a nested <code>Map<String, Integer></code> containing 6 entries is nested in to each of the 168 test Objects

```java
@In
@Embed(clazz = Map.class, steps = 6)
private final Spread<Integer> READINGS_TOTALLING_70000 = SpreadUtil.cumulative(70000);

@In
private final Spread<String> RANDOM_MAP_KEY =
    SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

...
    
    
List<TestDataObject> dataObjects =
    new Spreader<TestDataObject>()
        .factory(TestDataObject::new)
        .mutators(testDataObject -> testDataObject.setMapField(Spread.embedMap(READINGS_TOTALLING_70000, RANDOM_MAP_KEY)))
        .steps(24 * 7)
        .spread()
        .collect(Collectors.toList());
```

### Sequence of Spreads
You can create nested sequences of spreads which defines a different spread to be used for each embedding of a collection type.
If there is no collection type and the <code>Speader</code> is writing a flat list the the sequence will rotate for the specificed number of steps in the <code>Spread.in()</code>.

You need to mark a <code>Spread</code> making use of nested spreads in sequence with <code>@Dynamic</code> so that it will re-calculate its value array at the end of each cycle, instead of wrapping.

This nested sequence of spreads will generate an embedded list with 2 elements into a TestDataObject with the cumulative totals defined in the nested sequence

```java
@In
private final Spread<Integer> INTEGERS_TOTALING =
    SpreadUtil.sequence(
        SpreadUtil.cumulative(5),
        SpreadUtil.cumulative(10),
        SpreadUtil.cumulative(15),
        SpreadUtil.cumulative(20),
        SpreadUtil.cumulative(25)
    );
;

@In
@Dynamic
@Embed(clazz = List.class, steps = 2)
private final Spread<TestDataObject> DATA_OBJECTS =
    SpreadUtil.complexType(
        new Spreader<TestDataObject>()
            .factory(TestDataObject::new)
            .mutator(testDataObject -> testDataObject.setIntegerField(Spread.in(INTEGERS_TOTALING, 2)))
    );

@BeforeEach
public void setup() {
    SpreadUtil.initPackage(
        this,
        this.getClass().getPackage().getName()
    );

}

@Test
public void testDynamicSpreadOfSequences() {
    List<List<TestDataObject>> TEST_DATA_OBJECTS =
        new Spreader<List<TestDataObject>>()
            .factory(ArrayList::new)
            .mutator(list -> list.addAll(Spread.embed(DATA_OBJECTS)))
            .steps(5)
            .spread()
            .collect(
                Collectors.toList()
            );
    assertThat(
        TEST_DATA_OBJECTS
            .stream().flatMap(List::stream)
            .map(TestDataObject::getIntegerField)
            .reduce(0, Integer::sum)
    ).isEqualTo(Integer.valueOf(75));
}
```