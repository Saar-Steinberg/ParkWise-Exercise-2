package entity;

import enums.VehicleColor;
import enums.VehicleSize;
import java.awt.Image; 

public class Vehicle {
    private String licensePlate; // The car's ID
    private String type;
    private VehicleColor color;
    private VehicleSize size;
    private double weight;
    
    // Photos of the car
    private Image frontPhoto;
    private Image rearPhoto;
    private Image sidePhoto;

    // Constructor
    public Vehicle(String licensePlate, String type, double weight, VehicleColor color, VehicleSize size) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.weight = weight;
        this.color = color;
        this.size = size;
    }
    //Empty Constructor
    public Vehicle(){
        this.licensePlate = null;
        this.weight = -1;
        this.color = null;
        this.size = null;
        this.type = null;
    }
    // Getters and Setters
    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VehicleColor getColor() {
        return color;
    }

    public void setColor(VehicleColor color) {
        this.color = color;
    }

    public VehicleSize getSize() {
        return size;
    }

    public void setSize(VehicleSize size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    @Override
    public String toString() {
        return "Vehicle [licensePlate=" + licensePlate + ", type=" + type + ", color=" + color + ", size=" + size
                + ", weight=" + weight + "]";
    }
    
    
}