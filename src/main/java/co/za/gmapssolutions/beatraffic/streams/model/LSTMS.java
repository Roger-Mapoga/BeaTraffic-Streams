package co.za.gmapssolutions.beatraffic.streams.model;

import java.util.List;

public class LSTMS {

    //https://colah.github.io/posts/2015-08-Understanding-LSTMs/
    private int hiddenStateVector = 0;
    private int cellStateVector = 0;
    private int j = 0;
    public Double activationFunction(Double x){
        //        Math.atan() // arctan - hardSigmoid
        return Math.tanh(x); //hyperbolic
    }
    public void softmax(){

    }
    public Double getRankExponentWeight(int k, int z, List<Double> ranks, int j){
        Double sum = 0.0;
        for(Double r : ranks){
            sum += Math.pow(k - r + 1,z);
        }
        return Math.pow(k - ranks.get(j) + 1,z) / sum;
    }
    public Double getNeuralNetworkInput(double nearestNeighbour,int k, int z, List<Double> ranks, int j){
        Double a = 0.0;
        for(this.j = 0; this.j < k;this.j++ ){
            a += (getRankExponentWeight(k,z,ranks,j) * nearestNeighbour)
                ;//+ ();
        }
        return a;
    }
    public void forgetGate(){

    }
    public void inputGate(){
        //N is Number of inputs
        //H is Number of cells
        //C is the number of memory cells
        //T is input sequence length
    }
    public void cell(){
        //N is Number of inputs
        //H is Number of cells
        //C is the number of memory cells
        //T is input sequence length


    }

}
