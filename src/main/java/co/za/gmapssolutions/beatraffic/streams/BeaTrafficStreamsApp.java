package co.za.gmapssolutions.beatraffic.streams;

import co.za.gmapssolutions.beatraffic.Address;
import co.za.gmapssolutions.beatraffic.User;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

public class BeaTrafficStreamsApp {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "beaTraffic-application-destination-de");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.setProperty(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://127.0.0.1:8081");
        config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
//      config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
        Map<String, String> serdeConfig = Collections.singletonMap(SCHEMA_REGISTRY_URL_CONFIG,
                "http://127.0.0.1:8081");
//        config.setProperty(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        //Serdes
        final Serde<User> specificAvroValueUserSerde = new SpecificAvroSerde<>();
        specificAvroValueUserSerde.configure(serdeConfig, false);
        final Serde<Address> specificAvroValueAddressSerde = new SpecificAvroSerde<>();
        specificAvroValueAddressSerde.configure(serdeConfig, false);
        //Topology
        Topology topology = new Topology();
//        topology.addSource("source-request", "beaTraffic-Request");
        topology.addSource("source-departure", "beaTraffic-Departure");
        topology.addSource("source-destination", "beaTraffic-Destination");
        topology.addSource("source-routes", "beaTraffic-Routes");
        topology.addSource("source-active", "beaTraffic-Active");
        topology.addProcessor("beaTraffic", new StreamsProcessorSupplier<>(),
                "source-departure","source-destination","source-routes","source-active");
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("user"),
                        Serdes.Long(),
                        specificAvroValueUserSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("departure"),
                        Serdes.Long(),
                        specificAvroValueAddressSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("destination"),
                        Serdes.Long(),
                        specificAvroValueAddressSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");
//        topology.addStateStore(
//                Stores.keyValueStoreBuilder(
//                        Stores.inMemoryKeyValueStore("active"),
//                        Serdes.Long(),
//                        specificAvroValueAddressSerde).withLoggingDisabled(), // need to disable logging to allow store pre-populating
//                "beaTraffic");
        topology.addSink("forecastSink", "beaTraffic-Forecast", "beaTraffic");


        KafkaStreams streams = new KafkaStreams(topology, config);
       // streams.cleanUp();
       // streams.localThreadsMetadata();

        streams.start();


        //streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//        departureAddress.to("routes-with-traffic", Produced.with(Serdes.Long(),avroSchemaSerde));

    }
}
