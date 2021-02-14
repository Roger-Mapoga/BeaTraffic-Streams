package co.za.gmapssolutions.beatraffic.streams.model;

import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConceptDrift {
    private float warningLevel;
    private final float driftLevel;
    private BeaTrafficDataType avgErrorMax,stdDevMax;
    public ConceptDrift(float warningLevel,float driftLevel){
        this.warningLevel = warningLevel;
        this.driftLevel = driftLevel;
        avgErrorMax = new BeaTrafficDataType();
        stdDevMax = new BeaTrafficDataType();
    }
    public boolean DriftDetector(List<Neuron> neuronRepository,List<BeaTrafficDataType> classIntervals ){
        List<BeaTrafficDataType> EDDM = new ArrayList<>();
        for(BeaTrafficDataType Class : classIntervals) {
          for (int i = 1; i < neuronRepository.size(); i++) {
              if(neuronRepository.get(i - 1).Class == Class.getClassType() && neuronRepository.get(i).Class == Class.getClassType()) {
                  BeaTrafficDataType avgError = distance(neuronRepository.get(i - 1), neuronRepository.get(i));
                  BeaTrafficDataType stdDev = new BeaTrafficDataType(Class.getLongitudeClassIntervals().getStdDeviation(),
                          Class.getLatitudeClassIntervals().getStdDeviation());
                  avgErrorMax.add(avgError);
                  stdDevMax.add(stdDev);
                  EDDM.add(new BeaTrafficDataType(avgError,stdDev));
              }
          }
        }
      AtomicBoolean drift = new AtomicBoolean(false);
      EDDM.forEach(d ->{
          double lonDriftOrWarn = isDriftOrWarning(d.getError().getLongitude(),avgErrorMax.getLongitude(),d.getStdDev().getLongitude(),stdDevMax.getLongitude());
          double latDriftOrWarn = isDriftOrWarning(d.getError().getLatitude(),avgErrorMax.getLatitude(),d.getStdDev().getLatitude(),stdDevMax.getLatitude());
          if( lonDriftOrWarn > driftLevel && latDriftOrWarn > driftLevel){
                //possible drift
                drift.set(true);
          }
      });
      return drift.get();
    }
    private double isDriftOrWarning(Double error,Double maxError,Double stdDev,Double maxStdDev){
        return (error+2*stdDev)/(maxError+2*maxStdDev);
    }
    private BeaTrafficDataType distance(Neuron neuronA, Neuron neuronB){
        return new BeaTrafficDataType(distance(neuronA.getError().getLongitude(),neuronB.getError().getLongitude()),
                distance(neuronA.getError().getLatitude(),neuronB.getError().getLatitude()));
    }
    private double distance(Double errorA, Double errorB){
        return (errorA - errorB);
    }
}
