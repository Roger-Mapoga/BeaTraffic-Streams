package co.za.gmapssolutions.beatraffic.streams;

import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BeaTraffic<K,V> implements Processor<K,V> {
    private Logger log = LoggerFactory.getLogger(BeaTraffic.class.getSimpleName());
    private ProcessorContext processorContext;
    private KeyValueStore<K, V> kvStore;
    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        Map<String,Object> test = processorContext.appConfigs();
        test.forEach((k,v) -> log.info("Key : "+ k + " Value : "+ v));
//        log.info(t.toString());

    }
    @Override
    public void process(K key, V value) {
        log.info("testing from processor : " + processorContext.topic());
        if(!processorContext.topic().isEmpty() && processorContext.topic().equals("input-topic1")) {
            kvStore = (KeyValueStore<K, V>) processorContext.getStateStore("aggStore");
            kvStore.put(key,value);
            log.info("Key store key : "+kvStore.get(key));
        }
        log.info("Key : " + key);
        log.info("Value : " + value);
       // kvStore.put(s,s2);
    }

    @Override
    public void close() {

    }
}
