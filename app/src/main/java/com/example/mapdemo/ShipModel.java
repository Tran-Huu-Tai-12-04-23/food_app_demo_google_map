package com.example.mapdemo;

public class ShipModel {

    private Order order;

    private int status = 1;

    public ShipModel() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
