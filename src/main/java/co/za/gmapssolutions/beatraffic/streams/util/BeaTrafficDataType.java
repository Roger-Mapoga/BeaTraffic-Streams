package co.za.gmapssolutions.beatraffic.streams.util;

import java.util.Objects;

public class BeaTrafficDataType {
    private Double longitude;
    private ClassIntervals longitudeClassIntervals;
    private Double latitude;
    private ClassIntervals latitudeClassIntervals;
    private BeaTrafficDataType error,stdDev;
    public BeaTrafficDataType(){

    }
    public BeaTrafficDataType(Double longitude, Double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public BeaTrafficDataType add(BeaTrafficDataType e){
        longitude+=e.getLongitude();
        latitude+=e.getLatitude();
        return this;
    }

    public BeaTrafficDataType(ClassIntervals longitudeClassIntervals, ClassIntervals latitudeClassIntervals){
        this.longitudeClassIntervals = longitudeClassIntervals;
        this.latitudeClassIntervals = latitudeClassIntervals;
    }

    public ClassIntervals getLongitudeClassIntervals() {
        return longitudeClassIntervals;
    }

    public ClassIntervals getLatitudeClassIntervals() {
        return latitudeClassIntervals;
    }
    public int getLongitudeFrequency(){
        return longitudeClassIntervals.getFrequency();
    }
    public int getLatitudeFrequency(){
        return latitudeClassIntervals.getFrequency();
    }
    public int getClassType(){
        return longitudeClassIntervals.getClassType();
    }

    public BeaTrafficDataType(BeaTrafficDataType error,BeaTrafficDataType stdDev){
        this.error = error ;
        this.stdDev = stdDev;
    }

    public BeaTrafficDataType getError() {
        return error;
    }

    public BeaTrafficDataType getStdDev() {
        return stdDev;
    }

}
