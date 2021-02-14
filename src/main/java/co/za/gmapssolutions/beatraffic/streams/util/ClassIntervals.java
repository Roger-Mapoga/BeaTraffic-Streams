package co.za.gmapssolutions.beatraffic.streams.util;

public class ClassIntervals {
    public int lowerLimit;
    private int classType;
    public int upperLimit;
    private int frequency;
    public float classMidpoints;
    private float mean;
    private float stdDeviation;
    public ClassIntervals(int classType,int lowerLimit, int upperLimit,float classMidpoints){
        this.classType = classType;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.classMidpoints = classMidpoints;
    }

    public int getClassType() {
        return classType;
    }

    public void setObservationCount(int frequency) {
        this.frequency = frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency += frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public float getMean(){
        return (frequency*classMidpoints)/frequency;
    }
    public Double getStdDeviation(){
        if( frequency > 0)
            return  Math.sqrt((frequency*Math.pow(classMidpoints,2)) -
                (1.0/frequency)*(Math.pow(frequency*classMidpoints,2))/(frequency-1));
        else
            return 0.0;
    }
}
