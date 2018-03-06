package com.example.denish.bloodbank;

/**
 * Created by denish on 5/3/18.
 */

public class DataItem {

    private String name;
    private String phoneno;
    private String bloodgroup;

    public DataItem(String name, String phoneno, String bloodgroup) {
        this.name = name;
        this.phoneno = phoneno;
        this.bloodgroup = bloodgroup;
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
}
