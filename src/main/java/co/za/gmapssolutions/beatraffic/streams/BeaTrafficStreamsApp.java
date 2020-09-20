package co.za.gmapssolutions.beatraffic.streams;

import co.za.gmapssolutions.beatraffic.Address;
import co.za.gmapssolutions.beatraffic.Route;
import co.za.gmapssolutions.beatraffic.User;
import co.za.gmapssolutions.beatraffic.VehicleTimeStep;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.Stores;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class BeaTrafficStreamsApp {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "beatraffic-application");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
       // config.setProperty(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        config.setProperty(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://127.0.0.1:8081");
        config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
//      config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
        //Topology
        Topology topology = new Topology();
        topology.addSource("sourceProcessor", "input-topic1");
        topology.addSource("sourceProcessor1", "input-topic2");
        topology.addProcessor("beaTraffic", new StreamsProcessorSupplier<>(),"sourceProcessor");
        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("aggStore"),
                        Serdes.String(),
                        Serdes.Long()).withLoggingDisabled(), // need to disable logging to allow store pre-populating
                "beaTraffic");

        //topology.addSink("sourceProcessor", "output-topic", "aggregator");

        //StreamsBuilder builder = new StreamsBuilder();
        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                "http://127.0.0.1:8081");
        final Serde<Long> longKeySerde = new Serdes.LongSerde();
        longKeySerde.configure(serdeConfig, true); // `true` for record keys

        final Serde<Float> floatKeySerde = new Serdes.FloatSerde();
        floatKeySerde.configure(serdeConfig, true); // `true` for record keys

        final Serde<Address> specificAvroValueSerde = new SpecificAvroSerde();
        specificAvroValueSerde.configure(serdeConfig, false);

        final Serde<User> specificAvroUserValueSerde = new SpecificAvroSerde();
        specificAvroUserValueSerde.configure(serdeConfig, false); // `false` for record values

        final Serde<Route> specificAvroRouteSerde = new SpecificAvroSerde();
        specificAvroUserValueSerde.configure(serdeConfig, false);

        final Serde<VehicleTimeStep> specificAvroVehicleTimeStepSerde = new SpecificAvroSerde();
        specificAvroVehicleTimeStepSerde.configure(serdeConfig, false);

        System.out.println("Running");
//        KTable<Long, Address> departureAddress =
//            builder.table("beatraffic-departure",Consumed.with(longKeySerde,specificAvroValueSerde));
////
//        KTable<Long, Address> destinationAddress =
//                builder.table("beatraffic-destination",Consumed.with(longKeySerde,specificAvroValueSerde));
//
//        KTable<Long, User> user =
//                builder.table("beatraffic-request",Consumed.with(longKeySerde,specificAvroUserValueSerde));
//
//        KTable<Long, Route> routes =
//                builder.table("beatraffic-routes", Consumed.with(longKeySerde,specificAvroRouteSerde));
//
//        KTable<Float, VehicleTimeStep> timeStep =
//                builder.table("beaTraffic-active", Consumed.with(floatKeySerde,specificAvroVehicleTimeStepSerde));

        //TODO logic to check routes with traffic
        //TODO Step 1 get current cars in routes of the requesting user
        //TODO configure all topics to be log compacted
        //TODO Read latest records of the topic
        //TODO KNN with latest records (KTables)

        //TODO question to answer, should the read data be committed after read ?


        //TODO LSTM

        //routes.print(Printed.toSysOut());

//        timeStep.print(Printed.toSysOut());
//
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.cleanUp();
        streams.start();

        //streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//        departureAddress.to("routes-with-traffic", Produced.with(Serdes.Long(),avroSchemaSerde));

    }
}
