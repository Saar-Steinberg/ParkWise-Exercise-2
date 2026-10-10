package entity;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID; 

public class ParkingSession {
    
    private String id;
    private Vehicle vehicle; // Which vehicle parked
    private int parkingLotId; // Which parking lot did he park
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean isPaid; // Check if the client paid for the session
    private double paymentAmount;

    // Constructor
    public ParkingSession(Vehicle vehicle, int parkingLotId) {
        this.id = UUID.randomUUID().toString(); // Generating a uniqe id for this parking session
        this.vehicle = vehicle;
        this.parkingLotId = parkingLotId;
        
        this.entryTime = LocalDateTime.now(); // Clock starts running when the session is created
        this.exitTime = null; // Session is active so exit time is yet to be defined
        this.isPaid = false; // Session is active so payment did not happen yet
        this.paymentAmount = 0; // Session is active so payment did not happen yet
    }
    //Empty Cpnstructors
    public ParkingSession(){
        this.id = "-1";
        this.vehicle = null;
        this.parkingLotId = -1;
        this.entryTime = null;
        this.exitTime = null;
        this.isPaid = false;
        this.paymentAmount = -1;
    }

    /*
     * Effect: Ending the parking session - exit time is now
     */
    public void endSession() {
        this.exitTime = LocalDateTime.now();
    }


    /*
     * Effect: Calculating the duration of the parking session
     */
    public int getDurationInHours() {
        LocalDateTime endTimeCalc = (exitTime == null) ? LocalDateTime.now() : exitTime;

        // Difference in minutes
        long minutes = Duration.between(entryTime, endTimeCalc).toMinutes();
        
        if (minutes <= 0) return 0; 

        int hours = (int) (minutes / 60);
        
        if (minutes % 60 > 0) {
            hours++;
        }
        
        return hours;
    }
    /*
     * Returns if the parking session is active
     */
    public boolean isActive() {
        return exitTime == null;
    }

    // Getters & Setters 
    public String getId(){ 
        return id;
    }
    
    public int getParkingLotId() {
        return parkingLotId;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public Vehicle getVehicle(){
         return vehicle; 
    }
    
    public double getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    public LocalDateTime getEntryTime(){
         return entryTime;
    }
    
    public LocalDateTime getExitTime(){
         return exitTime;
    }
    @Override
    public String toString() {
        return "ParkingSession [id=" + id + ", vehicle=" + vehicle + ", parkingLotId=" + parkingLotId + ", entryTime="
                + entryTime + ", exitTime=" + exitTime + ", isPaid=" + isPaid + "]";
    }

    
    
}