package com.example.denish.bloodbank;

/**
 * Created by denish on 5/3/18.
 */

public class DataItem {

    private String name;
    private String phoneno;
    private String bloodgroup;
    private String lat;
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public DataItem(String name, String phoneno, String bloodgroup, String lat, String lon) {
        this.name = name;
        this.phoneno = phoneno;
        this.bloodgroup = bloodgroup;
        this.lat = lat;
        this.lon = lon;
    }

    public DataItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    @Override
    public String toString() {
        return name + " ,"+
                phoneno + ", "+
                bloodgroup + ", "+
                lat + ", "+
                lon;

    }
}
