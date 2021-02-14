package co.za.gmapssolutions.beatraffic.streams.util;

import co.za.gmapssolutions.beatraffic.streams.model.Neuron;

import java.util.Comparator;
import java.util.List;

public class Stats {
    public static BeaTrafficDataType getMin(List<BeaTrafficDataType> slidingWindow){
        return new BeaTrafficDataType(slidingWindow.stream().min(Comparator.comparing(BeaTrafficDataType::getLongitude)).get().getLongitude(),
                slidingWindow.stream().min(Comparator.comparing(BeaTrafficDataType::getLatitude)).get().getLatitude());
    }

    public static BeaTrafficDataType getMax(List<BeaTrafficDataType> slidingWindow){
        return new BeaTrafficDataType(slidingWindow.stream().max(Comparator.comparing(BeaTrafficDataType::getLongitude)).get().getLongitude(),
                slidingWindow.stream().max(Comparator.comparing(BeaTrafficDataType::getLatitude)).get().getLatitude());
    }
    public static BeaTrafficDataType getMinOut(List<Neuron> neuronRepository){
        return new BeaTrafficDataType(neuronRepository.stream().min(Comparator.comparing(Neuron::getOutputLongitude)).get().getOutputLongitude(),
                neuronRepository.stream().min(Comparator.comparing(Neuron::getOutputLatitude)).get().getOutputLatitude());
    }
    public static BeaTrafficDataType getMaxOut(List<Neuron> neuronRepository){
        return new BeaTrafficDataType(neuronRepository.stream().max(Comparator.comparing(Neuron::getOutputLongitude)).get().getOutputLongitude(),
                neuronRepository.stream().max(Comparator.comparing(Neuron::getOutputLatitude)).get().getOutputLatitude());
    }
    public static BeaTrafficDataType getMean(){

        return null;
    }

}
