package com.example.mapdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Food implements Serializable {

    private String thumbnails;
    private String cost;
    private String locationShop;
    private String description;
    private String costShip;
    private String name;
    private  String duration;
    private  String distance;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Food(String thumbnails, String cost, String locationShop, String description, String costShip, String name) {
        this.thumbnails = thumbnails;
        this.cost = cost;
        this.locationShop = locationShop;
        this.description = description;
        this.costShip = costShip;
        this.name = name;
    }

    public Food() {
    }

    public Food(String thumbnails, String cost, String locationShop, String description, String costShip) {
        this.thumbnails = thumbnails;
        this.cost = cost;
        this.locationShop = locationShop;
        this.description = description;
        this.costShip = costShip;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLocationShop() {
        return locationShop;
    }

    public void setLocationShop(String locationShop) {
        this.locationShop = locationShop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCostShip() {
        return costShip;
    }

    public void setCostShip(String costShip) {
        this.costShip = costShip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
