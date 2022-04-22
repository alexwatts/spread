# Spread
Spread is a ergonomic helper library for building test objects quickly and fluently without needing to write builders.
Spread tries to preserve full control of the generated objects whist also doing what you would expect where things are unspecified.

## Getting Started
You can define a spread of values to be injected into test objects via constructor/factory method/mutatator methods.

For example, for a range of <code>LocalDateTime</code> values incrementing by hour:

    Spread<LocalDateTime> everyHour = 
              SpreadUtil
                .initial(LocalDateTime.MIN)
                .step(previousDate -> previousDate.plusHours(1));
 

You can also define 'cumulative' Spreads, for example, a series of <code>BigDecimal</code> values with a cumulative total of 10000:

    Spread<BigDecimal> cumulativeReadings = SpreadUtil.cumulative(BigDecimal.valueOf(10000));
    
If you need to change the type of the <code>Spread</code> you can map a spread to any other type eg:

    Spread<Instant> everyHour = 
              SpreadUtil
                .initial(LocalDateTime.MIN)
                .step(previousDate -> previousDate.plusHours(1))
                .map(localDateTime -> localDateTime.toInstant(ZoneOffset.UTC));
    
To inject <code>Spread</code> ranges into test objects you can use a <code>Spreader</code> object and wrap constructor arguments with <code>Spread.in()</code> to inject <code>Spread</code> instances into your test object: 
    
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
                
This works in a similar way for factory methods:

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
                
 And where constructor or factory methods are unavailable, via mutator methods:
 
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
                
 The <code>spread()</code> method of the <code>Spreader</code> class returns a <code>Stream</code> of the target object type, meaning that we are free to collect the resultant stream into any desired groupings or collection types available to us via the Java stream API features, eg. <code>Map</code> <code>Set</code> etc.             

## Other Features

### Sequence
You can define a sequence of values in a <code>spread</code> which will feed in and repeat for as many steps are defined eg.

    Spread<String> threeNames =
            SpreadUtil.sequence(
                "John",
                "Emma",
                "Sophie"
            );

### Fixed
You can define fixed values which will fill as specified, for as many steps as specified eg:

    Spread<Double> fixedDouble = SpreadUtil.fixed(1.6d);

### Custom
You can define your own logic to fill values for the defined steps eg:
    
    Spread<String> randomString = SpreadUtil.custom((String) -> RandomStringUtils.random(7, true, true));

### Debug
The <code>Spreader</code> accepts a mode for debug. You can add a <code>.debug()</code> call to the <code>Spreader</code> chain to enable additional logging, including a tabular print of the string representation of the values being fed into objects. eg:

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