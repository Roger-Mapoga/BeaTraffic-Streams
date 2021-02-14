package co.za.gmapssolutions.beatraffic.streams.model;

import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Neuron {
    private int id;
    public static long mergeCount = 0;
    private double firingTime;
    private BeaTrafficDataType excitation;
    private BeaTrafficDataType input;
    private BeaTrafficDataType output;
    private double PSP = 0.0;
    private int M;//number of previous merges of similar neurons though the learning history
    private double weight; //Wij
    private double pspMax;
    private double threshold; //γ (gamma) ∈ R[0,1]
    private BeaTrafficDataType error; //error value between predicted and input value
    public int Class;
    //
    private BeaTrafficDataType center, width;
    public Neuron(){

    }
    public Neuron(int id, BeaTrafficDataType center,BeaTrafficDataType width){
        this.id = id;
        this.center =center;
        this.width = width;
    }
    //T is referred to as spikeInterval in the firing time formula (Tj = T(1 - output))
    public Neuron(int id, BeaTrafficDataType center,BeaTrafficDataType width,BeaTrafficDataType output,float spikeInterval){
        this.id = id;
        this.center = center;
        this.width = width;
        this.output = output;
        this.firingTime = spikeInterval * (1 - ((this.output.getLongitude()+this.output.getLatitude())));
    }
    public Neuron(int id, BeaTrafficDataType input , BeaTrafficDataType output, float spikeInterval, double pspMax, double threshold) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.firingTime = spikeInterval * (1 - ((this.output.getLongitude()+this.output.getLatitude()/2)));
//        this.weight = weight;
        this.pspMax = pspMax;
        this.threshold = threshold;
      //  this.error = new BeaTrafficDataType(abs(input.getLongitude() - output.getLongitude()),abs(input.getLatitude()-output.getLatitude()));
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BeaTrafficDataType getCenter() {
        return center;
    }

    public BeaTrafficDataType getWidth() {
        return width;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight=weight;
    }

    public void setPSP(double PSP) {
        this.PSP = PSP;
    }

    public double getPSP() {
        return PSP;
    }

    public void setPspMax(double pspMax) {
        this.pspMax += pspMax;
    }

    public double getPspMax() {
        return pspMax;
    }

    public void setExcitation(BeaTrafficDataType excitation) {
        this.excitation = excitation;
    }

    public BeaTrafficDataType getExcitation() {
        return excitation;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public BeaTrafficDataType getOutput() {
        return output;
    }
    //input
    public void setInput(BeaTrafficDataType input) {
        this.input = input;
    }

    public BeaTrafficDataType getInput() {
        return input;
    }

    public double getInputLongitude(){
        return input.getLongitude();
    }
    public double getInputLatitude(){
        return input.getLatitude();
    }
    public double getOutputLongitude(){
        return output.getLongitude();
    }
    public double getOutputLatitude(){
        return output.getLatitude();
    }
    public void setOutput(BeaTrafficDataType output) {
        this.output = output;
    }


    public void setClass(int aClass) {
        Class = aClass;
    }

    public void setFiringTime(double firingTime) {
        this.firingTime = firingTime;
    }

    public double getFiringTime() {
        return firingTime;
    }

    //error
    public BeaTrafficDataType getError() {
        return error;
    }

    public void setError(BeaTrafficDataType error) {
        this.error = error;
    }


}
