package co.za.gmapssolutions.beatraffic.streams;

import co.za.gmapssolutions.beatraffic.*;
import co.za.gmapssolutions.beatraffic.streams.model.eSNN;
import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeaTraffic<K,V> implements Processor<K,V> {
    private Logger log = LoggerFactory.getLogger(BeaTraffic.class.getSimpleName());
    private ProcessorContext processorContext;
//    private KeyValueStore<Long, User> userStore;
    private KeyValueStore<Long, Address> destinationStore;
    private KeyValueStore<Long, Address> departureStore;
//    private User user = null;
    private Address departure;
    private Address destination = null;
    private VehicleTimeStep vehicleTimeStep = null;
    private List<VehicleTimeStep> vehicleTimeSteps = new ArrayList<>();
    private List<Route> routes;
    private int j = 0;
    private eSNN OeSNN = new eSNN(1000,1.5f,20,0.01,0.85,0.75,150);

    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        Map<String,Object> test = processorContext.appConfigs();
//        userStore = (KeyValueStore<Long, User>) processorContext.getStateStore("user");
        destinationStore = (KeyValueStore<Long, Address>) processorContext.getStateStore("destination");
        departureStore = (KeyValueStore<Long, Address>) processorContext.getStateStore("departure");
        routes = new ArrayList<>();

    }
    @Override
    public void process(K key, V value) {
        log.info("Offset: "+ processorContext.topic().intern());
        if(processorContext.topic().equals("beaTraffic-Departure") ) {
            departure = (Address) value;
            departureStore.put((Long)key,departure);
            log.info("DepartureTest : " + departure);
        }
        if(processorContext.topic().equals("beaTraffic-Destination")){
            destination = (Address) value;
            destinationStore.put((Long) key, (Address) value);
            log.info("DestinationTest : " + destination);
        }
        if(processorContext.topic().equals("beaTraffic-Routes") ){ //possible routes from departure & destination
            routes.clear();
            routes.add((Route) value);
        }

        if(processorContext.topic().equals("beaTraffic-Active")){ // online training model
            vehicleTimeStep = (VehicleTimeStep) value;
            if(vehicleTimeStep.getLongitude() != 0.0 && vehicleTimeStep.getLatitude() != 0.0) {
                vehicleTimeSteps.add(vehicleTimeStep);
                log.info("Test and Train data: " + vehicleTimeStep.getLongitude() + " : " + vehicleTimeStep.getLatitude());
                OeSNN.fit(new BeaTrafficDataType(vehicleTimeStep.getLongitude(), vehicleTimeStep.getLatitude()));
            }
        }

        //predict traffic
        //Check if theres a request
//        if(userStore.all().hasNext() && departureStore.all().hasNext() && destinationStore.all().hasNext())
        if(departureStore.all().hasNext() && destinationStore.all().hasNext() && routes.size() > 0)
        {
//            if(userStore.all().next().key.equals(departureStore.all().next().key)
//            && userStore.all().next().key.equals(destinationStore.all().next().key))
            if(departureStore.all().next().key.equals(destinationStore.all().next().key)) //make sure we predict for same request
            {

//                List<BeaTrafficDataType> predictedForecast = OeSNN.predict(new BeaTrafficDataType(departureStore.all().next().value.getLongitude(),
//                                departureStore.all().next().value.getLatitude()),
//                        new BeaTrafficDataType(destinationStore.all().next().value.getLongitude(),
//                                destinationStore.all().next().value.getLatitude()));
                List<BeaTrafficDataType> predictedForecast = OeSNN.predict(routes);
                if (predictedForecast.size() > 0) {
                    for (BeaTrafficDataType forecast : predictedForecast) {
                        processorContext.forward(departureStore.all().next().key, new Output(departureStore.all().next().key,
                                forecast.getLatitude(), forecast.getLongitude()));
                        log.info("Predicted value: " + forecast.getLongitude() + " : "+forecast.getLatitude());
                    }
                } else {
                    log.info("No known traffic in route requested");
                }
                departureStore.delete(departureStore.all().next().key);
                destinationStore.delete(destinationStore.all().next().key);
            }
        }
    }
    @Override
    public void close() {
        log.info("Size of train data: "+vehicleTimeSteps.size());
        processorContext.commit();
        log.info("Closing processor");
    }
}
