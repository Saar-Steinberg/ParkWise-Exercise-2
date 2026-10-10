package entity;

import enums.ConveyorStatus;

public class Conveyor {
    
    private int id;
    private int floor;
    private int xCoordinate;
    private int yCoordinate;
    private double maxWeight;
    
    private ConveyorStatus status; 
    // Field to save the previous state for Restart / Deep History logic
    private ConveyorStatus previousStatus; 
    
    private int attempts; // Count of inspection failures
    private boolean weightChanged; 
    private Vehicle currentVehicle; // The current vehicle being handled by the conveyor (can be null)

    // --- Constructors ---

    public Conveyor(int id, int floor, int x, int y, double maxWeight) {
        this.id = id;
        this.floor = floor;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.maxWeight = maxWeight;
        
        // Default initialization
        this.status = ConveyorStatus.OFF;
        this.previousStatus = ConveyorStatus.OFF; 
        this.attempts = 0;
        this.weightChanged = false;
    }

    // Empty constructor (for flexibility or frameworks)
    public Conveyor() {
        this.id = -1;
        this.floor = -1;
        this.status = ConveyorStatus.OFF;
        this.previousStatus = ConveyorStatus.OFF;
    }

    // --- Getters & Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getxCoordinate() { return xCoordinate; }
    public void setxCoordinate(int xCoordinate) { this.xCoordinate = xCoordinate; }

    public int getyCoordinate() { return yCoordinate; }
    public void setyCoordinate(int yCoordinate) { this.yCoordinate = yCoordinate; }

    public double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(double maxWeight) { this.maxWeight = maxWeight; }

    public ConveyorStatus getStatus() { return status; }

    /**
     * Smart Setter:
     * Updates the status and manages the Deep History mechanism.
     * The history is automatically saved only if we are leaving an "active" state
     * in favor of "PAUSED" or "OFF".
     */
    public void setStatus(ConveyorStatus newStatus) {
        // If the new status is PAUSED or OFF, and the current status was active (check, working, or operational),
        // we save the current status as history so we can return to it later.
        if ((newStatus == ConveyorStatus.PAUSED || newStatus == ConveyorStatus.OFF) && 
            (this.status != ConveyorStatus.PAUSED && this.status != ConveyorStatus.OFF)) {
            
            this.previousStatus = this.status;
        }
        
        this.status = newStatus;
    }

    public ConveyorStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ConveyorStatus previousStatus) { this.previousStatus = previousStatus; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public void incrementAttempts() { this.attempts++; }
    public void resetAttempts() { this.attempts = 0; }

    public boolean isWeightChanged() { return weightChanged; }
    public void setWeightChanged(boolean weightChanged) { this.weightChanged = weightChanged; }

    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(Vehicle currentVehicle) { this.currentVehicle = currentVehicle; }

    // --- Business Logic Helpers ---
    // (Note: The Math.random logic was moved to the Controller; only data-related logic remains here)

    /**
     * Confirms that the weight has been changed and resets the flag (called by the Controller).
     */
    public void confirmWeightChange() {
        this.weightChanged = false;
    }

    public void calculateOptimalPath() {
        if (currentVehicle != null) {
            // In practice, the algorithm logic would be here or in a separate route management class
            System.out.println("Conveyor " + id + ": Calculating path for vehicle " + currentVehicle.getLicensePlate());
        }
    }

    @Override
    public String toString() {
        return "Conveyor [id=" + id + ", status=" + status + ", prev=" + previousStatus + 
               ", attempts=" + attempts + ", maxWeight=" + maxWeight + "]";
    }
}