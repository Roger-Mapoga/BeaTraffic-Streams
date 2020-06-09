package co.za.gmapssolutions.beatraffic.streamsTest;

import co.za.gmapssolutions.beatraffic.streams.BeaTraffic;
import co.za.gmapssolutions.beatraffic.streams.StreamsProcessorSupplier;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.junit.Assert.*;

public class StreamsTests {
    private Logger log = LoggerFactory.getLogger(StreamsTests.class.getSimpleName());
    private TopologyTestDriver testDriver;
    private Serializer<String> stringSerializer = new StringSerializer() ;
    private Serializer<Long> longSerializer = new LongSerializer();
    private Deserializer<String> stringDeserializer = new StringDeserializer() ;
    private Deserializer<Long> longDeserializer = new LongDeserializer();
    private TestInputTopic<String, Long> testInputTopic;
    private TestInputTopic<String, Long> testInputTopic2;
    private TestOutputTopic<String, Long> outputTopic;
    @Before
    public void setup() {
        // Processor API
        Topology topology = new Topology();
        topology.addSource("sourceProcessor", "input-topic1");
        topology.addSource("sourceProcessor1", "input-topic2");
        topology.addProcessor("aggregator", new StreamsProcessorSupplier<String,Long>(),"sourceProcessor");
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("aggStore"),
                        Serdes.String(),
                        Serdes.Long()).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "aggregator");

        topology.addProcessor("aggregator1", new StreamsProcessorSupplier<String,Double>(),"sourceProcessor1");

        topology.addSink("sinkProcessor", "output-topic", "aggregator");
        // setup test driver
        Properties props = new Properties();
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation");
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        testDriver = new TopologyTestDriver(topology, props);
        testInputTopic = testDriver.createInputTopic("input-topic1",stringSerializer, longSerializer);
        testInputTopic2 = testDriver.createInputTopic("input-topic2",stringSerializer, longSerializer);
        outputTopic = testDriver.createOutputTopic("output-topic", stringDeserializer, longDeserializer);

    }
    @After
    public void tearDown() {
        testDriver.close();
    }
    @Test
    public void test(){
        testInputTopic.pipeInput("a",1L);
        testInputTopic2.pipeInput("b",2L);
//        assertEquals(outputTopic.isEmpty(), false);

//        log.info(outputTopic.readKeyValue().toString());
//        Assert.assertEquals(outputTopic.readValue(),1L);
        //        testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
//        OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
//        Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
    }
}
