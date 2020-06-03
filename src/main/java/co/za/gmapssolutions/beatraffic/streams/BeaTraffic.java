package co.za.gmapssolutions.beatraffic.streams;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

public class BeaTraffic implements Processor<String,String> {
    private ProcessorContext processorContext;
    private KeyValueStore<String, Integer> kvStore;
    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    @Override
    public void process(String s, String s2) {

    }

    @Override
    public void close() {

    }
}
