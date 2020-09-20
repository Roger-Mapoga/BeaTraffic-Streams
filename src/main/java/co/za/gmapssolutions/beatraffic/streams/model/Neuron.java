package co.za.gmapssolutions.beatraffic.streams.model;

import co.za.gmapssolutions.beatraffic.streams.util.beaTrafficDataType;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class Neuron {
    private int id;
    private double firingTime;
    private beaTrafficDataType output;
    private double PSP = 0.0;
    private int M;//number of previous merges of similar neurons though the learning history
    private double weight; //Wij
    private double PSP_max;
    private double threshold; //γ (gamma)

    //T is referred to as spikeInterval in the firing time formula (Tj = T(1 - output))
    public Neuron(int id, beaTrafficDataType output, float spikeInterval,double weight,double PSP_max,double threshold) {
        this.id = id;
        this.output = output;
        this.firingTime = spikeInterval * (1 - ((this.output.getLongitude()+this.output.getLatitude()/2)));
        this.weight = weight;
        this.PSP_max = PSP_max;
        this.threshold = threshold;
    }
    public int getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
