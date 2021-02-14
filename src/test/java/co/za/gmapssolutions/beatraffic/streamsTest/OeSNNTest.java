package co.za.gmapssolutions.beatraffic.streamsTest;

import co.za.gmapssolutions.beatraffic.streams.model.ConceptDrift;
import co.za.gmapssolutions.beatraffic.streams.model.DataReductionTechnique;
import co.za.gmapssolutions.beatraffic.streams.model.Neuron;
import co.za.gmapssolutions.beatraffic.streams.model.eSNN;
import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static co.za.gmapssolutions.beatraffic.streams.util.Stats.getMinOut;

public class OeSNNTest {
    private Logger log = LoggerFactory.getLogger(OeSNNTest.class.getSimpleName());

    private List<Neuron> arr = new ArrayList<>();
    private List<Double> sampleInputs = new ArrayList<>();
    private ConceptDrift conceptDrift;
    private DataReductionTechnique dataReductionTechnique;
    @Before
    public void init(){
        //
        sampleInputs.add(0.5);
        sampleInputs.add(0.3);
        sampleInputs.add(0.4);
        sampleInputs.add(0.3);
        sampleInputs.add(0.6);
        sampleInputs.add(0.2);
        sampleInputs.add(1.0);
        sampleInputs.add(0.4);
        sampleInputs.add(0.3);
        sampleInputs.add(0.4);
        sampleInputs.add(0.2);
        sampleInputs.add(0.4);
        sampleInputs.add(0.1);
        sampleInputs.add(0.5);
//        arr.add(new Neuron(1,new BeaTrafficDataType(0.30000000149011613,-0.20000000149011612 ),new BeaTrafficDataType(0.9985218037341171,0.9844964377312747),20,1,2.5725,1.9293749999999998));
//        arr.add(new Neuron(2,new BeaTrafficDataType(0.20000000149011612,-0.30000000149011613 ),new BeaTrafficDataType(1.0 ,1.0 ),20,1.85,4.43113125,3.3233484375));
//        arr.add(new Neuron(3,new BeaTrafficDataType(0.40000000149011616,-0.40000000149011616),new BeaTrafficDataType(0.9562379279962897,0.6916865247395028),20,3.186625,6.388117328124999,4.79108799609375));
//        arr.add(new Neuron(4,new BeaTrafficDataType(0.5000000014901161,-0.5000000014901161),new BeaTrafficDataType(5.195749305525899E-4,0.38216293530182904),20,3.70863125,8.32404601957031,6.243034514677733));
//        arr.add(new Neuron(5,new BeaTrafficDataType(0.6000000014901161,-0.6000000014901161 ),new BeaTrafficDataType(0.0023809609062955058,1.1729652643025147E-17 ),20,14.529486078124999,10.166459811639548,7.624844858729661));
//        arr.add(new Neuron(6,new BeaTrafficDataType(0.7000000014901161,-0.7000000014901161 ),new BeaTrafficDataType(2.7403913214699347E-10,3.701827664509618E-52 ),20,16.6666659805746145,10.166459811639548,7.624844858729661));
        conceptDrift = new ConceptDrift(0.95f,0.95f);
        dataReductionTechnique = new DataReductionTechnique();
    }
    @Test
    public void testOeSNN(){

        eSNN OeSNN = new eSNN(10,1,1,0.15,0.5,0.8,7);
        BeaTrafficDataType departure = new BeaTrafficDataType(0.1,0.1);
        BeaTrafficDataType target = new BeaTrafficDataType(1.0,1.0);
        int j = 0;
//       for(double x = 2.1f;x<100;x+=1.59){
        for(Double x : sampleInputs){
            OeSNN.fit(new BeaTrafficDataType(-x,x));
//            j++;
        }
//       OeSNN.predict(departure,target);
    }

  //  @Test
    public void testDRT(){
        dataReductionTechnique.populateClassFrequency(getMinOut(arr),arr);

        dataReductionTechnique.drtSGP(arr,1);
    }
 //   @Test
    public void conceptDriftTest(){
        dataReductionTechnique.populateClassFrequency(getMinOut(arr),arr);

        boolean drift = conceptDrift.DriftDetector(arr,dataReductionTechnique.getClassIntervals());
        log.info("isDrift : "+ drift);
    }
}
