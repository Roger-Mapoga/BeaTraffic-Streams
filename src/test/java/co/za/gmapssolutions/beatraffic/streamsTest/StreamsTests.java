package co.za.gmapssolutions.beatraffic.streamsTest;

import co.za.gmapssolutions.beatraffic.*;
import co.za.gmapssolutions.beatraffic.streams.StreamsProcessorSupplier;
import co.za.gmapssolutions.beatraffic.streams.model.LSTMS;
import co.za.gmapssolutions.beatraffic.streams.model.Neuron;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.Stores;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.jackson.FieldSelection;
import org.datavec.api.records.reader.impl.jackson.JacksonLineRecordReader;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.shade.jackson.core.JsonFactory;
import org.nd4j.shade.jackson.databind.ObjectMapper;
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
    private TestOutputTopic<Long, Address> outputTopic;
    private Address departure;
    private Address destination;
    private Route route1;
    private Route route2;
    private VehicleTimeStep vehicleTimeStep;
    private User user;

    //@Before
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
        final Serde<Long> longKeySerde = new Serdes.LongSerde();
        longKeySerde.configure(serdeConfig, true); // `true` for record keys
        final Serde<Float> floatKeySerde = new Serdes.FloatSerde();
        floatKeySerde.configure(serdeConfig, true); // `true` for record keys
        final Serde<Address> specificAvroValueAddressSerde = new SpecificAvroSerde<>();
        specificAvroValueAddressSerde.configure(serdeConfig, false);
        final Serde<User> specificAvroValueUserSerde = new SpecificAvroSerde<>();
        specificAvroValueUserSerde.configure(serdeConfig, false);
        final Serde<Route> routeSerde = new SpecificAvroSerde<>();
        routeSerde.configure(serdeConfig,false);
        final Serde<VehicleTimeStep> vehicleTimeStepSerde = new SpecificAvroSerde<>();
        vehicleTimeStepSerde.configure(serdeConfig,false);
        final Serde<Long> specificAvroLongValueSerde = Serdes.Long();
        specificAvroLongValueSerde.configure(serdeConfig, false);
        // Processor API
        Topology topology = new Topology();
        topology.addSource("user", "user-topic")
                .addSource("departure", "departure-topic")
                .addSource("routes", "routes-topic")
                .addSource("active-vehicle-list", "active-vehicle-topic")
                .addSource("destination", "destination-topic");
        topology.addProcessor("beaTraffic", new StreamsProcessorSupplier<>(),
                "user","departure","routes","active-vehicle-list","destination");
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("user"),
                        Serdes.Long(),
                        specificAvroValueUserSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");


        topology.addSink("sinkProcessor", "output-topic", "beaTraffic");


        testDriver = new TopologyTestDriver(topology, props);

        testInputUserTopic = testDriver.createInputTopic("user-topic",specificAvroLongValueSerde.serializer()
                ,specificAvroValueUserSerde.serializer());

        testInputDepartureTopic = testDriver.createInputTopic("departure-topic",specificAvroLongValueSerde.serializer(),
                specificAvroValueAddressSerde.serializer());


        testInputDestinationTopic = testDriver.createInputTopic("destination-topic",specificAvroLongValueSerde.serializer(),
                specificAvroValueAddressSerde.serializer());

        testInputRouteTopic = testDriver.createInputTopic("routes-topic",specificAvroLongValueSerde.serializer(),
                routeSerde.serializer());
        testInputActiveVehicleTopic = testDriver.createInputTopic("active-vehicle-topic",
                specificAvroLongValueSerde.serializer(), vehicleTimeStepSerde.serializer());

        //testInputTopic2 = testDriver.createInputTopic("input-topic2",stringSerializer, longSerializer);
//        outputTopic = testDriver.createOutputTopic("output-topic", specificAvroLongValueSerde.deserializer(),
//                specificAvroValueSerde.deserializer());
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
   // @After
    public void tearDown() {
        testDriver.close();
    }
    @Test
    public void test(){
        testInputUserTopic.pipeInput(1L,user);
        testInputDepartureTopic.pipeInput(1L,departure);
        testInputDestinationTopic.pipeInput(1L,destination);
        testInputRouteTopic.pipeInput(1L,route1);
        testInputRouteTopic.pipeInput(1L,route2);
        for(int i =0;i<100 ;i++) {
            vehicleTimeStep = new VehicleTimeStep(2L*i, "", "car", 31.10+(.1*i),
                    42.21+(-.1*i));
            testInputActiveVehicleTopic.pipeInput((long) (i+1),vehicleTimeStep);
        }
        //testInputTopic2.pipeInput("b",2L);
//        assertEquals(outputTopic.isEmpty(), false);

//        log.info(outputTopic.readKeyValue().toString());
//        Assert.assertEquals(outputTopic.readValue(),1L);
        //        testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
//        OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
//        Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
    }
    @Test
    public void testRankExponentWeight(){
        LSTMS lstm = new LSTMS();
        log.info(":"+lstm.getNeuralNetworkInput(1.5,2,2, new ArrayList<>(Arrays.asList(1.90, 1.7, 1.2, 1.0)),1));
    }
    @Test
    public void testNeuron(){
        Neuron neuron = new Neuron(1,42.21,1,1,0,5,42.21,42.71);
        log.info(String.valueOf(neuron.getId()));
    }
    @Test
    public void testDeepLearning4jLSTM(){
        INDArray input = Nd4j.create(new int[] {1, 1,
                1}, 'f');
    }
}
