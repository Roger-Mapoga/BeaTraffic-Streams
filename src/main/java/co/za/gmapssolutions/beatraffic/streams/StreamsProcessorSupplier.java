package co.za.gmapssolutions.beatraffic.streams;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;

public class StreamsProcessorSupplier<K,V> implements ProcessorSupplier<K, V> {
    @Override
    public Processor<K, V> get() {
        return new BeaTraffic<>();
    }
}
