package com.example.signalify.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Accident {

    private String type;
    private GeoPoint location;
    private ArrayList<String> description;
    private ArrayList<String> image;

    public Accident(){

    }

    public Accident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image){
        this.type = type;
        this.location = location;
        this.description = description;
        this.image = image;
    }

    // GETTERS

    public String getType(){
        return type;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public GeoPoint getLocation() {
        return location;
    }

    // SETTERS

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String toString(){

        return getType() + " " + getDescription().get(0) + " " + getLocation().getLatitude() + " " +getLocation().getLongitude();
    }

}
