package co.za.gmapssolutions.beatraffic.streams.model;

public class KNN {

    public Double getDistanceBetween(double mLon,double mLat,double nLon,double nLat){
       return Math.sqrt(
                Math.pow((nLon - mLon),2) +
                Math.pow((nLat - mLat),2)
        );
    }
}
