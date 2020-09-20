package co.za.gmapssolutions.beatraffic.streams.util;

public class beaTrafficDataType {
    private double longitude;
    private double latitude;
    public beaTrafficDataType(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
