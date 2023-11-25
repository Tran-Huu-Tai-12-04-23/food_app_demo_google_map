package com.example.mapdemo;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private String total;
    private String address;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    private Food food;
    private LatLng location;
    private Date orderDate;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getOrderDate() {
        return orderDate;
    }


    public Order(String total, String address, Food food) {
        this.total = total;
        this.address = address;
        this.food = food;
    }

    public Order() {
        status  = "Shipper chưa nhận đơn!";
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
