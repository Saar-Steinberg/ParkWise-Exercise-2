package enums;

public enum ConveyorStatus {
    OFF, // Initial state
    WORKING, // Busy 
    /** Ready to receive commands within the Active state. */
    OPERATIONAL,
    INTEGRITY_CHECK, // Going through tests
    PAUSED // Paused for some reason
}
