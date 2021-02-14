package co.za.gmapssolutions.beatraffic.streams.model;

import co.za.gmapssolutions.beatraffic.streams.util.BeaTrafficDataType;
import co.za.gmapssolutions.beatraffic.streams.util.ClassIntervals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static co.za.gmapssolutions.beatraffic.streams.util.Stats.getMaxOut;
import static co.za.gmapssolutions.beatraffic.streams.util.Stats.getMinOut;

public class DataReductionTechnique {
    private final Logger log = LoggerFactory.getLogger(DataReductionTechnique.class.getSimpleName());
    private final List<BeaTrafficDataType> classIntervals = new ArrayList<>();
    ;
    private BeaTrafficDataType classWidth;
    private final List<Neuron> mergedRepository = new ArrayList<>();
    private int mergeCount = 0;

    //Data Reduction Techniques - > Self-generating prototypes (SGP)
    public List<Neuron> drtSGP(List<Neuron> neuronRepository,int Rmin){
        log.info("Size : " + neuronRepository.size());
        log.info("Log : " + Math.log10(5));
//        c = (int) Math.ceil(1 + 3.3*(Math.log10(neuronRepository.size())));
        BeaTrafficDataType max = getMaxOut(neuronRepository);
        BeaTrafficDataType min = getMinOut(neuronRepository);
//        classWidth = new BeaTrafficDataType((int)Math.ceil( (max.getLongitude() - min.getLongitude())/c),
//                (int)Math.ceil((max.getLatitude() - min.getLatitude())/c));

        log.info("MaxLon " + max.getLongitude());
        log.info("MaxLat " + max.getLatitude());
        log.info("MinLon : " + min.getLongitude());
        log.info("MinLat : " + min.getLatitude());
//        log.info("Class Intervals : " + c);
        log.info("Class width : " + classWidth);
//        populateClassFrequency(min,neuronRepository);
        GeneralizationSGP(Rmin);
        merge(neuronRepository);
        log.info("ID : Class : InputLon : OutLon : ErrorLon : InputLat : OutLat : ErrorLat");
        mergedRepository.forEach(n -> {
            log.info(n.getId() +" : "+ n.Class +" : "+ n.getInputLongitude()+" : "+n.getOutputLongitude()+" : "
                            + n.getError().getLongitude() +" : " +n.getInputLatitude()+" : "+n.getOutputLatitude()+" : "+n.getError().getLatitude() );
        });
//        log.info("classInterval : classMidpoints : frequency : mean : stdDeviation");
//        AtomicInteger i = new AtomicInteger();
//        //AtomicInteger il = new AtomicInteger();
//        classIntervals.stream().forEach(
//                c -> {log.info("\t"+c.getLongitudeClassIntervals().lowerLimit +" : "+c.getLongitudeClassIntervals().upperLimit +" \t \t \t "+ c.getLongitudeClassIntervals().classMidpoints+
//                        " \t \t \t "+c.getLongitudeClassIntervals().getFrequency() +" \t \t \t"+c.getLongitudeClassIntervals().getMean() +"\t \t \t "
//                + neuronRepository.get(i.get()).Class +":"+neuronRepository.get(i.get()).getError().getLongitude());
//                    log.info("\t"+c.getLatitudeClassIntervals().lowerLimit +" : "+c.getLatitudeClassIntervals().upperLimit +" \t \t \t "+ c.getLatitudeClassIntervals().classMidpoints+
//                            " \t \t \t "+c.getLatitudeClassIntervals().getFrequency() +" \t \t \t"+c.getLatitudeClassIntervals().getMean() +"\t \t \t "
//                            + neuronRepository.get(i.get()).Class +":"+neuronRepository.get(i.get()).getError().getLatitude());
//                    i.getAndIncrement();
//                });

        return mergedRepository;
    }

    private List<Neuron> merge(List<Neuron> neuronRepository){
        int i = 0;
        classIntervals.forEach(c -> {
            neuronRepository.forEach(n -> {
                if(n.Class == c.getClassType()){
                    if(i < mergedRepository.size()){
                        //merger
                        merger(i,n);
                    }else{
                        //add
                        mergedRepository.add(i,n);
                    }
                }
            });
        });

        return neuronRepository;
    }
    private List<Neuron> merger(int i,Neuron n){
        //mergedRepository.get(i).setWeight(merge(n.getWeight(),mergedRepository.get(i).getWeight(),mergeCount));
        mergedRepository.get(i).setThreshold(merge(n.getThreshold(),mergedRepository.get(i).getThreshold(),mergeCount));
        mergedRepository.get(i).setOutput(new BeaTrafficDataType(merge(n.getOutputLongitude(),mergedRepository.get(i).getOutputLongitude(),mergeCount),
                merge(n.getOutputLatitude(),mergedRepository.get(i).getOutputLatitude(),mergeCount)));
        mergeCount++;
        return mergedRepository;
    }
    private double merge(double newValue,double oldValue,int mergeCount){
        return (newValue + (oldValue * mergeCount)) / (mergeCount + 1);
    }
    private void GeneralizationSGP(int Rmin){
        Predicate<BeaTrafficDataType> isQualified = item -> item.getLatitudeFrequency() <= Rmin;
        classIntervals.stream().filter(isQualified).forEach(q->{});
        classIntervals.removeIf(isQualified);
    }
    private List<BeaTrafficDataType>  populateClassIntervals(BeaTrafficDataType min,int c,int size){
        int longitudeLowerLimit =(int) min.getLongitude();
        int latitudeLowerLimit =(int) min.getLatitude();
        int longitudeUpperLimit = (int) min.getLongitude() + c;
        int latitudeUpperLimit = (int) min.getLatitude() + c;
        int classType = 1;
        classIntervals.add(new BeaTrafficDataType(new ClassIntervals(classType,longitudeLowerLimit,longitudeUpperLimit,
                (longitudeLowerLimit+longitudeUpperLimit)/2.0f),
                new ClassIntervals(classType,latitudeLowerLimit,latitudeUpperLimit,(latitudeLowerLimit+latitudeUpperLimit)/2.0f)));
        while (size > classType){
            longitudeLowerLimit = longitudeUpperLimit;
            latitudeLowerLimit = latitudeUpperLimit;
            longitudeUpperLimit += c;
            latitudeUpperLimit += c;
            classIntervals.add(new BeaTrafficDataType(new ClassIntervals(classType,longitudeLowerLimit,longitudeUpperLimit,
                    (longitudeLowerLimit+longitudeUpperLimit)/2.f),
                    new ClassIntervals(classType,latitudeLowerLimit,latitudeUpperLimit,(latitudeLowerLimit+latitudeUpperLimit)/2.0f)));
//            classInterval. += classWidth;
            classType++;
        }
        return classIntervals;
    }

    public void populateClassFrequency(BeaTrafficDataType min,List<Neuron> neuronRepository){
        //c - C(x j ) ∈ {1, 2, . . . , K} number of class
        int C = (int) Math.ceil(1 + 3.3*(Math.log(neuronRepository.size())));
        populateClassIntervals(min,neuronRepository.size(),C).stream().forEach(
                c ->  neuronRepository.stream().forEach(
                        s -> {
                            int valid = isValueInBetween(c.getLongitudeClassIntervals().lowerLimit,
                                    c.getLongitudeClassIntervals().upperLimit, s.getOutputLongitude());
                            if(valid == 1) s.setClass(c.getLongitudeClassIntervals().getClassType());
                            c.getLongitudeClassIntervals().setFrequency(valid);
                            valid = isValueInBetween(c.getLatitudeClassIntervals().lowerLimit,
                                    c.getLatitudeClassIntervals().upperLimit, s.getOutputLatitude());
                            if(valid == 1) s.setClass(c.getLatitudeClassIntervals().getClassType());
                            c.getLatitudeClassIntervals().setFrequency(valid);
                        }
                )
        );
    }
    private int isValueInBetween(int lowerLimit,int upperLimit,double observation){
        if(observation >= lowerLimit && observation < upperLimit){
            return 1;
        }
        return 0;
    }

    //
    public List<BeaTrafficDataType> getClassIntervals(){
        return classIntervals;
    }
}
