package com.driver.model;

import javax.persistence.*;

public class Cab{
    public Cab(){

    }

    public Cab(int id, int perKmRate, boolean available) {
        Id = id;
        this.perKmRate = perKmRate;
        this.available = available;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    int perKmRate;
    boolean available;

    //Relationship --child
    @OneToOne
    @JoinColumn
    Driver driver;
}