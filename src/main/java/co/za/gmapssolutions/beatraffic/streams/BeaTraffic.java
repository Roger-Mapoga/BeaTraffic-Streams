package co.za.gmapssolutions.beatraffic.streams;

import co.za.gmapssolutions.beatraffic.*;
import co.za.gmapssolutions.beatraffic.streams.model.KNN;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeaTraffic<K,V> implements Processor<K,V> {
    private Logger log = LoggerFactory.getLogger(BeaTraffic.class.getSimpleName());
    private ProcessorContext processorContext;
    private KeyValueStore<K, V> kvStore;
    private User user;
    private Address departure;
    private Address destination;
    private List<Route> routes;
    private KNN knn;
    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        Map<String,Object> test = processorContext.appConfigs();
        kvStore = (KeyValueStore<K, V>) processorContext.getStateStore("user");
        routes = new ArrayList<>();
        knn = new KNN();
//        test.forEach((k,v) -> log.info("Key : "+ k + " Value : "+ v));
//        log.info(t.toString());
    }
    @Override
    public void process(K key, V value) {

        if(processorContext.topic().equals("user-topic")){
            user = (User) value;
            kvStore.put(key, (V) user);
            log.info("user");
            log.info("Key : " + key);
            log.info("Value : " + user);
        }
        if(processorContext.topic().equals("departure-topic") && kvStore.all().next().value.equals(user)) {
            departure = (Address) value;
            log.info("departure");
            log.info("Key : " + key);
            log.info("Value : " + departure);
        }
        if(processorContext.topic().equals("destination-topic") && kvStore.all().next().value.equals(user)){
            destination = (Address) value;
            log.info("destination");
            log.info("Key : " + key);
            log.info("Value : " + destination);
        }
        if(processorContext.topic().equals("routes-topic") && kvStore.all().next().value.equals(user)){
            routes.add((Route) value);
            log.info("routes");
            log.info("Key : " + key);
            log.info("Value : " + routes.size());
        }
        if(processorContext.topic().equals("active-vehicle-topic") && kvStore.all().next().value.equals(user)){
            VehicleTimeStep vehicleTimeStep = (VehicleTimeStep) value;
            for(Route route : routes) {
                log.info("vehicleTimeStep");
                log.info("Key : " + key);
                log.info("Value : " + vehicleTimeStep);
                log.info("Route  : " + route.getRouteId());
                for(GeoPoint geoPoint : route.getRoute()) {






                }
            }
        }
    }

    @Override
    public void close() {
        //processorContext.commit();
        log.info("Closing processor");
    }
}
