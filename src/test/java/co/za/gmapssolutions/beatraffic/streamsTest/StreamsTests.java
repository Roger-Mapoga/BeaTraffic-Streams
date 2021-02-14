package co.za.gmapssolutions.beatraffic.streamsTest;

import co.za.gmapssolutions.beatraffic.*;
import co.za.gmapssolutions.beatraffic.streams.StreamsProcessorSupplier;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.Stores;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

public class StreamsTests {
    private Logger log = LoggerFactory.getLogger(StreamsTests.class.getSimpleName());
    private TopologyTestDriver testDriver;
    private TestInputTopic<Long, User> testInputUserTopic;
    private TestInputTopic<Long, Address> testInputDepartureTopic;
    private TestInputTopic<Long, Address> testInputDestinationTopic;
    private TestInputTopic<Long, Route> testInputRouteTopic;
    private TestInputTopic<Long, VehicleTimeStep> testInputActiveVehicleTopic;
    private TestOutputTopic<Long, Output> outputTopic;
    private Address departure;
    private Address destination;
    private Route route1;
    private Route route2;
    private VehicleTimeStep vehicleTimeStep;
    private User user;

    @Before
    public void setup() {
        // setup test driver
        Properties props = new Properties();
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "beaTrafficTests");
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        //props.setProperty(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.setProperty(SCHEMA_REGISTRY_URL_CONFIG, "mock://schema.registry:1234");
        //Serde config
        Map<String, String> serdeConfig = Collections.singletonMap(SCHEMA_REGISTRY_URL_CONFIG,
                "mock://schema.registry:1234");

        final Serde<Address> specificAvroValueAddressSerde = new SpecificAvroSerde<>();
        specificAvroValueAddressSerde.configure(serdeConfig, false);
        final Serde<User> specificAvroValueUserSerde = new SpecificAvroSerde<>();
        specificAvroValueUserSerde.configure(serdeConfig, false);
        final Serde<Route> routeSerde = new SpecificAvroSerde<>();
        routeSerde.configure(serdeConfig,false);
        final Serde<VehicleTimeStep> vehicleTimeStepSerde = new SpecificAvroSerde<>();
        vehicleTimeStepSerde.configure(serdeConfig,false);
        final Serde<Output> Output = new SpecificAvroSerde<>();
        Output.configure(serdeConfig,false);
        final Serde<Long> specificAvroLongValueSerde = Serdes.Long();
        specificAvroLongValueSerde.configure(serdeConfig, false);
        // Processor API
        Topology topology = new Topology();
        topology.addSource("user", "beaTraffic-Request")
                .addSource("departure", "beaTraffic-Departure")
                .addSource("destination", "beaTraffic-Destination")
                .addSource("routes", "beaTraffic-Routes")
                .addSource("active-vehicle-list", "beaTraffic-Active");
        topology.addProcessor("beaTraffic", new StreamsProcessorSupplier<>(),
                "user","departure","destination","routes","active-vehicle-list");
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("user"),
                        Serdes.Long(),
                        specificAvroValueUserSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("destination"),
                        Serdes.Long(),
                        specificAvroValueAddressSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("departure"),
                        Serdes.Long(),
                        specificAvroValueAddressSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        topology.addSink("sinkProcessor", "beaTraffic-Forecast", "beaTraffic");


        testDriver = new TopologyTestDriver(topology, props);

        testInputUserTopic = testDriver.createInputTopic("beaTraffic-Request",specificAvroLongValueSerde.serializer()
                ,specificAvroValueUserSerde.serializer());

        testInputDepartureTopic = testDriver.createInputTopic("beaTraffic-Departure",specificAvroLongValueSerde.serializer(),
                specificAvroValueAddressSerde.serializer());


        testInputDestinationTopic = testDriver.createInputTopic("beaTraffic-Destination",specificAvroLongValueSerde.serializer(),
                specificAvroValueAddressSerde.serializer());

        testInputRouteTopic = testDriver.createInputTopic("beaTraffic-Routes",specificAvroLongValueSerde.serializer(),
                routeSerde.serializer());
        testInputActiveVehicleTopic = testDriver.createInputTopic("beaTraffic-Active",
                specificAvroLongValueSerde.serializer(), vehicleTimeStepSerde.serializer());

        //testInputTopic2 = testDriver.createInputTopic("input-topic2",stringSerializer, longSerializer);
        //topology.addSink("forecastSink", "beaTraffic-Forecast", "beaTraffic");
        outputTopic = testDriver.createOutputTopic("beaTraffic-Forecast", specificAvroLongValueSerde.deserializer(),
                Output.deserializer());
        user = new User(1L,"car");
        departure = new Address(1L,"test","postal code","jhb","country",
                "provence",21.22,23.44);
        destination = new Address(1L,"test","postal code","jhb","country",
                "provence",21.42,23.70);
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(21.23,23.45));
        route1 = new Route(1L,1,  geoPoints);
        route2 = new Route(1L,2,  geoPoints);

    }
    @After
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void test(){
        testInputDepartureTopic.pipeInput(1L,departure);
        testInputDestinationTopic.pipeInput(1L,destination);
        testInputRouteTopic.pipeInput(1L,route1);
        testInputRouteTopic.pipeInput(1L,route2);
        testInputUserTopic.pipeInput(1L,user); //testing order
        for(float i =0.1f;i<100 ;i++) {
            vehicleTimeStep = new VehicleTimeStep(2L, "", "car", 21.22+i
                    ,23.44 + i);
            testInputActiveVehicleTopic.pipeInput(2L,vehicleTimeStep);
        }
        //testInputTopic2.pipeInput("b",2L);
//        assertEquals(outputTopic.isEmpty(), false);
        if(!outputTopic.isEmpty())
        log.info("Value form output topic: " + outputTopic.readValue());
//        Assert.assertEquals(outputTopic.readValue(),1L);
        //        testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
//        OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
//        Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
    }

}
