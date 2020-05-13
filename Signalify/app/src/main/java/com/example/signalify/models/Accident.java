package com.example.signalify.models;

import android.content.Intent;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Accident {

    private String type;
    private GeoPoint location;
    private ArrayList<String> description;
    private ArrayList<String> image;
    private String veracity;

    public Accident(){

    }

    public Accident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image,int veracity){
        this.type = type;
        this.location = location;
        this.description = description;
        this.image = image;
        this.veracity = Integer.toString(veracity);
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

    public String getVeracity() { return this.veracity;}

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

    public void setVeracity(int veracity) {
        this.veracity = Integer.toString(veracity);
    }

    public String toString(){

        return getType() + " " + getDescription().get(0) + " " + getLocation().getLatitude() + " " +getLocation().getLongitude();
    }

}
