package co.za.gmapssolutions.beatraffic.streams.model;

import co.za.gmapssolutions.beatraffic.streams.util.beaTrafficDataType;
import co.za.gmapssolutions.beatraffic.streams.util.LocationComp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;

public class eSNN {
    private Neuron neuron;
    private beaTrafficDataType center;
    private beaTrafficDataType width;
    private List<Neuron> neuronRepository;
    private double mod; //[0,1]
    private double C; //[0,1]
    private double threshold; //[0,1]
    private double SIM; //[0,1] similarity parameter
    private double overlapFactor,T;
    private int N;//number of receptive fields/input sample
    private List<Location> slidingWindow; //sliding window
    private int neuronRepositoryMax;//N R_size
    private int mergeCount = 0; //M -> number os previous merges
    private double weight,PSP_max;
    public eSNN(Neuron neuron,int neuronRepositoryMax){
        this.neuron = neuron;
        this.neuronRepositoryMax = neuronRepositoryMax;
        neuronRepository = new ArrayList<>(neuronRepositoryMax);
    }
    public void fit(Location input,Location target,int j,double mod,double C,double sim){
        //Update sliding window with sample input
        slidingWindow.clear();
        slidingWindow.add(input);
        //Calculate I max , I min for the W samples in the sliding window
        //Calculate C j and W j over the sliding window for their encoding into
        //firing time of multiple pre-synaptic neurons j
        center = calculateCenter(slidingWindow.stream().min(Comparator.comparing(Location::getLocation)).get(),target,j);
        width = calculateWidth(slidingWindow.stream().min(Comparator.comparing(Location::getLocation)).get(),target);
        //Create a new output neuron i and the connection weights as
        //w ji = mod order(j)
        weight += order(mod,j);
        PSP_max += weight * order(mod,j);
        neuron = new Neuron(j,calculateOutput(input,center,width),0.5f, weight,PSP_max,PSP_max*C); //Pre-Synaptic Neuron
        Neuron mostSimilarNeuron = FindMostSimilar(neuron,neuronRepository);
        if(distance(neuron,mostSimilarNeuron) <= sim){
            //Update the weight vector and threshold of the most similar neuron
            mergeNeurons(neuron,mostSimilarNeuron);
        }else{
            if(neuronRepository.size() < neuronRepositoryMax){
                //Add the weight vector and threshold of the new output neuron
                neuronRepository.add(j,neuron);
            }else{
                //Remove the oldest weight vector and its threshold, and put the
                //new ones into NR
                Neuron oldestNeuron = neuronRepository.stream().min(Comparator.comparing(Neuron::getId)).get();
                neuronRepository.remove(oldestNeuron);
            }
        }
        //weight =

    }
    private void mergeNeurons(Neuron newNeuron,Neuron mostSimilarNeuron){
        neuronRepository.get(mostSimilarNeuron.getId()).setWeight(
                merge(newNeuron.getWeight(),mostSimilarNeuron.getWeight())
        );
        neuronRepository.get(mostSimilarNeuron.getId()).setThreshold(
                merge(newNeuron.getThreshold(),mostSimilarNeuron.getThreshold())
        );
        mergeCount++;
    }
    private double merge(double newValue,double oldValue){
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
        return sqrt(pow(neuronA.getWeight(),2) - pow(neuronB.getWeight(),2));
    }
    private beaTrafficDataType calculateCenter(Location Imin, Location Imax, int j){
        return new beaTrafficDataType(((2*j-3)/2)*((Imax.getLongitude()-Imin.getLongitude())/N-2),
                ((2*j-3)/2)*((Imax.getLatitude()-Imin.getLatitude())/N-2));
    }
    private beaTrafficDataType calculateWidth(Location Imin,Location Imax){
        return new beaTrafficDataType((1/overlapFactor)*((Imax.getLongitude()-Imin.getLongitude())/(N-2)),
                (1/overlapFactor)*((Imax.getLatitude()-Imin.getLatitude())/(N-2)));
    }
    private beaTrafficDataType calculateOutput(Location input,beaTrafficDataType center,beaTrafficDataType width){
        return new beaTrafficDataType(exp(-(pow(input.getLongitude()-center.getLongitude(),2)/pow(2*width.getLongitude(),2))),
                exp(-(pow(input.getLatitude()-center.getLatitude(),2)/pow(2*width.getLatitude(),2))));
    }
    private double order(double mod,int j){ //mod order j
        return pow(mod,j);
    }
    
}
