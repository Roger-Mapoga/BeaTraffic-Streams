package co.za.gmapssolutions.beatraffic.streams.model;

public class Location {
    private double longitude;
    private double latitude;
    public Location(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public double getLocation(){
        return (longitude + latitude) / 2;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
