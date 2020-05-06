package co.za.gmapssolutions.beatraffic.streams;

import co.za.gmapssolutions.beatrafficrestproducer.Address;
import co.za.gmapssolutions.beatrafficrestproducer.User;
import io.confluent.kafka.serializers.KafkaAvroDecoder;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.AvroSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class BeaTrafficStreamsApp {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "beatraffic-application4");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//      config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
//      config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        //
      //config.setProperty(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://127.0.0.1:8081");

//      config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

//      config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        StreamsBuilder builder = new StreamsBuilder();
        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                "http://127.0.0.1:8081");
        final Serde<Long> longKeySerde = new Serdes.LongSerde();
        longKeySerde.configure(serdeConfig, true); // `true` for record keys
        final Serde<Address> specificAvroValueSerde = new SpecificAvroSerde();
        specificAvroValueSerde.configure(serdeConfig, false);
        final Serde<User> specificAvroUserValueSerde = new SpecificAvroSerde();
        specificAvroUserValueSerde.configure(serdeConfig, false); // `false` for record values

        System.out.println("Running");
            KStream<Long, Address> departureAddress =
                builder.stream("beatraffic-departure",Consumed.with(longKeySerde,specificAvroValueSerde));
//
        KStream<Long, Address> destinationAddress =
                builder.stream("beatraffic-destination",Consumed.with(longKeySerde,specificAvroValueSerde));

        KStream<Long, User> user =
                builder.stream("beatraffic-request",Consumed.with(longKeySerde,specificAvroUserValueSerde));

//        KStream<Long, Address> routes =
//                builder.stream("beatraffic-routes", Consumed.with(Serdes.Long(),avroSchemaSerde));
            user.print(Printed.toSysOut());
            departureAddress.print(Printed.toSysOut());
            destinationAddress.print(Printed.toSysOut());

            KafkaStreams streams = new KafkaStreams(builder.build(), config);
            streams.cleanUp();
            streams.start();

        streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        //departureAddress.to("routes-with-traffic", Produced.with(Serdes.Long(),avroSchemaSerde));

    }
}
