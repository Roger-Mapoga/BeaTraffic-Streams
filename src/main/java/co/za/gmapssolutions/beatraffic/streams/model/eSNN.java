package co.za.gmapssolutions.beatraffic.streams.model;
//Neuron@Network

import co.za.gmapssolutions.beatraffic.Route;
import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.lang.Math.*;

public class eSNN {
    private final Logger log = LoggerFactory.getLogger(eSNN.class.getSimpleName());
    private final int N;//number of receptive fields/input sample
    private final float overlapFactor; // β ∈ R[1, 2]

    private final List<Neuron> neuronRepository;
    private final double mod; //[0,1]
    private final double C; //[0,1]
//    private double threshold; //[0,1]
    private final double SIM; //[0,1] similarity parameter
    private final float spikeInterval ; // T;
//    private final List<BeaTrafficDataType> slidingWindow; //sliding window
    private final int neuronRepositoryMax;//N R_size
    private final int mergeCount = 0; //M -> number of previous merges
//    private double weight,PSP_max;
    //Data Reduction
    private final DataReductionTechnique reductionTechnique;
    private final ConceptDrift conceptDrift;
    //
    BeaTrafficDataType slidingWindowMin;
    BeaTrafficDataType slidingWindowMax = new BeaTrafficDataType(0.0,0.0);

    public eSNN(int neuronRepositoryMax,float overlapFactor,float spikeInterval,double sim,double mod,double C,int N){
        this.neuronRepositoryMax = neuronRepositoryMax;
        neuronRepository = new ArrayList<>();
//        slidingWindow = new ArrayList<>();
        this.overlapFactor = overlapFactor;
        this.spikeInterval = spikeInterval;
        this.SIM = sim;
        this.mod = mod;
        this.C = C;
        this.N = N; //number of receptive fields
        reductionTechnique = new DataReductionTechnique();
        conceptDrift = new ConceptDrift(0.95f,0.95f);
    }
    public void fit(BeaTrafficDataType input){
        //Update sliding window with sample input
//        slidingWindow.clear();
        if(slidingWindowMin == null){
            slidingWindowMin = input;
        }
        slidingWindowMin.setLongitude(Math.min(slidingWindowMin.getLongitude(),input.getLongitude()));
        slidingWindowMin.setLatitude(Math.min(slidingWindowMin.getLatitude(),input.getLatitude()));
        slidingWindowMax.setLongitude(Math.max(slidingWindowMax.getLongitude(),input.getLongitude()));
        slidingWindowMax.setLatitude(Math.max(slidingWindowMax.getLatitude(),input.getLatitude()));
//        slidingWindow.add(input);
        //encoding inputs
        List<Neuron> encodedInputNeurons = encodeInputGRF(input,slidingWindowMin,slidingWindowMax);
//        encodedInputNeurons.forEach(in->{
//            log.info("ID: "+in.getId()+" input: "+in.getInput().getLongitude()+" Firing times: "+in.getFiringTime() +" Excitation: "+in.getExcitation().getLongitude()
//            +" Weight: "+in.getWeight()+" Center: "+in.getCenter().getLongitude());
//        });
//        //Create a new output neuron i and the connection weights as //w ji = mod order(j)
        Neuron neuron = getOutputNeuronRankOrder(encodedInputNeurons);//= new Neuron(j,input,output,spikeInterval,PSP_max,PSP_max*C); //Pre-Synaptic Neuron
//        log.info("ID: "+neuron.getId()+" Input: "+neuron.getInput().getLongitude()+" PSP max: " + neuron.getPspMax() + " Threshold: "+neuron.getThreshold()
//                +" Center: "+neuron.getCenter().getLongitude() +" Width: "+neuron.getWidth().getLongitude()
//                +" Predicted Output: "+ 0.0);
//        if(neuronRepository.size() > 0)
//        reductionTechnique.populateClassFrequency(slidingWindowMin,neuronRepository);
        Neuron mostSimilarNeuron = null;
        if(neuronRepository.size() > 1)
            mostSimilarNeuron = FindMostSimilar(neuron, neuronRepository);

//        if(conceptDrift.DriftDetector(neuronRepository,reductionTechnique.getClassIntervals())){ //isDriftDetected
        if(mostSimilarNeuron != null && distance(neuron, mostSimilarNeuron) <= SIM) {
////            //Update the weight vector and threshold of the most similar neuron
            log.info("Drift detected: " +neuron.getInput().getLongitude()+" : "+ mostSimilarNeuron.getInput().getLongitude());
//////            log.info(mostSimilarNeuron.getThreshold() +" : "+mostSimilarNeuron.getWeight());
//            int Rmin = 1;
////            neuronRepository = reductionTechnique.drtSGP(neuronRepository,Rmin);
            mergeNeurons(neuron,mostSimilarNeuron);
        }else{
            if (neuronRepository.size() >= neuronRepositoryMax) {
                //Remove the oldest weight vector and its threshold, and put the
                //new ones into NR
                Neuron oldestNeuron = neuronRepository.stream().min(Comparator.comparing(Neuron::getId)).get();
                log.info("removed neuron : "+oldestNeuron.getInput().getLongitude());
                neuronRepository.remove(oldestNeuron);
            }
            //Add the weight vector and threshold of the new output neuron
            neuronRepository.add(neuron);
        }
    }

    public List<BeaTrafficDataType> predict(List<Route> routes){
        //predicted values must equals the time period that the pattern of variation takes before it repeats its self
        List<BeaTrafficDataType> predictedValue = new ArrayList<>();
        neuronRepository.forEach(on->{
                on.setPSP(on.getPspMax());
                if(on.getPSP() > on.getThreshold()) { //is neuron fired ?
                    if(on.getPSP() - on.getThreshold() > 0.0){
                        on.setError(new BeaTrafficDataType(on.getInputLongitude()-on.getOutputLongitude(),
                                on.getInputLatitude()-on.getOutputLatitude()));
                        float errorFactor = 0.001f;
                        on.setOutput(new BeaTrafficDataType(on.getInputLongitude()+(on.getInputLongitude() - on.getOutputLongitude() ) * errorFactor,
                                on.getInputLatitude()+(on.getInputLatitude() - on.getOutputLatitude() ) * errorFactor));
                        routes.forEach(route -> route.getRoute().forEach(match->{
                            double dist = distance(new BeaTrafficDataType(match.getLatitude(),match.getLongitude()),on.getOutput());
                            if( dist < errorFactor && dist > 0.0 ){
                                    predictedValue.add(new BeaTrafficDataType(match.getLatitude(),match.getLongitude()));
//                                log.info("Distance: "+dist);
//                                log.info("Input point: "+ on.getInputLongitude() +" : "+on.getInputLatitude());
//                                log.info("Route point: "+ match.getLatitude() +" : "+match.getLongitude()); // routes lonlat are vice vesa from osmdroid
//                                log.info("Forecast: "+ on.getOutput().getLongitude() +" : "+on.getOutput().getLatitude());
                            }
                        }));
                    }
                }
            on.setPSP(0.0); //Post synaptic potential back to zero
        });
        return predictedValue;
    }
    private Neuron getOutputNeuronRankOrder(List<Neuron> encodedInputNeurons){ // returns firing neuron
        Neuron neuron = new Neuron(neuronRepository.size(),
                encodedInputNeurons.stream().min(Comparator.comparing(Neuron::getFiringTime)).get().getCenter(),
                encodedInputNeurons.stream().min(Comparator.comparing(Neuron::getFiringTime)).get().getWidth());
        encodedInputNeurons.forEach(n->{
//            n.setWeight(order(mod, order.get()),order.get());
//            n.setPspMax(n.getWeight().get(order.get()) * order(mod, order.get()));
//            n.setThreshold(n.getPspMax()*C);
//            order.getAndIncrement();
            neuron.setWeight(neuron.getWeight()+n.getWeight());
            neuron.setPspMax(n.getWeight() * order(mod, n.getId()));
        });
        //neuron.setId(neuronRepository.size() + 1);
        neuron.setInput(encodedInputNeurons.stream().min(Comparator.comparing(Neuron::getFiringTime)).get().getInput());
//        neuron.setOutput(encodedInputNeurons.stream().min(Comparator.comparing(Neuron::getFiringTime)).get().getCenter());
       // neuron.set();
        //neuron.setError(encodedInputNeurons.stream().min(Comparator.comparing(Neuron::getFiringTime)).get().getError());
        neuron.setThreshold(neuron.getPspMax()*C); //set threshold
        //predict output
        Random random = new Random(System.currentTimeMillis()); //seeding random number
        //calculate average & standard deviation of current window.
         BeaTrafficDataType mean = calculateMean();
         BeaTrafficDataType stdDev = calculateStdDev(mean);
         double rand = random.nextGaussian();
         neuron.setOutput(new BeaTrafficDataType(stdDev.getLongitude()*rand+mean.getLongitude(),
                 stdDev.getLatitude()*rand+mean.getLatitude())); //predicted value.
        return neuron;
    }

    public List<Neuron> encodeInputGRF(BeaTrafficDataType input,BeaTrafficDataType Imin,BeaTrafficDataType Imax){ //Gaussian Receptive Field encoder
        //Calculate I max , I min for the W samples in the sliding window
        //BeaTrafficDataType Imin = origin;//new BeaTrafficDataType(-2.0,-2.0);//getMin(slidingWindow);
        //BeaTrafficDataType Imax = target;//new BeaTrafficDataType(2.0,2.0);//getMax(slidingWindow);
        //Calculate C j and W j over the sliding window for their encoding into
        //firing time of multiple pre-synaptic neurons j
        BeaTrafficDataType width = calculateWidth(Imin, Imax);
        if(width.getLongitude() == 0.0 && width.getLatitude() == 0.0){ //default width to 1 if calculated is zero
            width.setLongitude(1.0);
            width.setLatitude(1.0);
        }
        List<Neuron> inputNeurons = new ArrayList<>();
        for(int j = 0 ; j < N ; j++) {
            BeaTrafficDataType center = calculateCenter(Imin, Imax, j);
            inputNeurons.add(new Neuron(j, center, width));
        }
        AtomicInteger order = new AtomicInteger();
        order.set(0);
        inputNeurons.forEach(in ->{
            in.setInput(input);
          //  n.setOutput();
           // n.setError(new BeaTrafficDataType(abs(input.getLongitude() - n.getOutput().getLongitude()),abs(input.getLatitude()-n.getOutput().getLatitude())));
            in.setExcitation(calculateExcitation(input,in.getCenter(),in.getWidth()));
            in.setFiringTime(spikeInterval*(1-((in.getExcitation().getLongitude()+in.getExcitation().getLatitude()))));
            in.setWeight(order(mod, order.get()));
            order.getAndIncrement();
        });
        inputNeurons.sort(Comparator.comparing(Neuron::getFiringTime));

        return inputNeurons;
    }

    private BeaTrafficDataType calculateCenter(BeaTrafficDataType Imin, BeaTrafficDataType Imax, int j){
        return new BeaTrafficDataType(Imin.getLongitude()+((2*j-3)/2.0)*((Imax.getLongitude()-(Imin.getLongitude()))/(N-2)),
                Imin.getLatitude()+((2*j-3)/2.0)*((Imax.getLatitude()-Imin.getLatitude())/(N-2)));
    }
    private BeaTrafficDataType calculateWidth(BeaTrafficDataType Imin, BeaTrafficDataType Imax){
        return new BeaTrafficDataType((1/overlapFactor)*((Imax.getLongitude()-Imin.getLongitude())/(N-2)),
                (1/overlapFactor)*((Imax.getLatitude()-Imin.getLatitude())/(N-2)));
    }
    private BeaTrafficDataType calculateExcitation(BeaTrafficDataType input, BeaTrafficDataType center, BeaTrafficDataType width){
        return new BeaTrafficDataType(exp(-(pow(input.getLongitude()-center.getLongitude(),2)/(2*pow(width.getLongitude(),2)))),
                exp(-(pow(input.getLatitude()-center.getLatitude(),2)/(2*pow(width.getLatitude(),2)))));
    }
    private double order(double mod,int j){ //mod order j
        return pow(mod,j);
    }

    private BeaTrafficDataType calculateStdDev(BeaTrafficDataType mean){
        BeaTrafficDataType stdDev = new BeaTrafficDataType(0.0,0.0);
        neuronRepository.forEach(n->{
            stdDev.setLongitude(stdDev.getLongitude() + pow(n.getInputLongitude(),2.0));
            stdDev.setLatitude(stdDev.getLatitude() + pow(n.getInputLatitude(),2.0));
        });
        int size = neuronRepository.size();
        if(size > 0) {
            stdDev.setLongitude(sqrt((stdDev.getLongitude() - (1.0 / size) * pow(mean.getLongitude(), 2.0)) / size - 1));
            stdDev.setLatitude(sqrt((stdDev.getLatitude() - (1.0 / size) * pow(mean.getLatitude(), 2.0)) / size - 1));
        }
        return stdDev;
    }
    private BeaTrafficDataType calculateMean(){
        BeaTrafficDataType average = new BeaTrafficDataType(0.0,0.0);
        int size = neuronRepository.size();
        if(size>0){
            neuronRepository.forEach(n -> {
                average.setLongitude(average.getLongitude() + n.getInputLongitude());
                average.setLatitude(average.getLatitude() + n.getInputLatitude());
            });
            average.setLongitude(average.getLongitude()/size);
            average.setLatitude(average.getLatitude()/size);
        }
        return average;
    }
    ///
    private void mergeNeurons(Neuron newNeuron,Neuron mostSimilarNeuron){
        Predicate<Neuron> isQualified = item -> item.getId() == mostSimilarNeuron.getId();
        neuronRepository.stream().filter(isQualified).findFirst().ifPresent(m -> {
            m.setWeight(merge(newNeuron.getWeight(), mostSimilarNeuron.getWeight(), Neuron.mergeCount));
            m.setThreshold(merge(newNeuron.getThreshold(),mostSimilarNeuron.getThreshold(), Neuron.mergeCount));
            m.setId((int) merge(newNeuron.getId(),mostSimilarNeuron.getId(), Neuron.mergeCount));
            Neuron.mergeCount++;
//            log.info("ID: " +m.getId()+ " Merge count : "+ Neuron.mergeCount);
        });
    }
    private double merge(double newValue,double oldValue,long mergeCount){
        return (newValue + (oldValue * mergeCount)) / (mergeCount + 1);
    }
    private Neuron FindMostSimilar(Neuron neuron,List<Neuron> neuronRepository){
        double mostCom = distance(neuron,neuronRepository.get(0));
        Neuron mostSimNeuron = null;
        for(Neuron n : neuronRepository){
            double dist = distance(neuron,n);
            if( dist < mostCom){
                mostCom = dist;
                mostSimNeuron = n;
            }
        }
        return mostSimNeuron ;
    }
    private double distance(Neuron neuronA,Neuron neuronB){
        double diff = 0.0;
//        for(int j =0; j < neuronA.getWeight().size();j++){
            diff = pow(neuronA.getWeight(),2) - pow(neuronB.getWeight(),2);
//        }
        return sqrt(diff);
    }
    private double distance(BeaTrafficDataType a,BeaTrafficDataType b){
        double diff = pow((b.getLongitude() - a.getLongitude()),2) + pow((b.getLatitude() - a.getLatitude()),2);
        return sqrt(diff);
    }

}
