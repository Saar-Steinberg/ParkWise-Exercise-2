package boundaries;

/**
 * Interface defining the contract with the physical Gate Sensor hardware.
 */
public interface GateSensorInterface {
    
    /**
     * Detects a vehicle at the gate.
     * Returns the license plate number as a String, or null if no vehicle is detected.
     */
    String detectVehicle();

    /**
     * Commands the hardware to open the barrier.
     */
    void openBarrier();

    /**
     * Commands the hardware to close the barrier.
     */
    void closeBarrier();
}