package entity;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private int id;
    private String name;
    private String address;
    private String city;
    private int availableSpaces; // 0 if parking lot is full
    
    private List<Conveyor> conveyors; // ArrayList to store all conveyors of this parking lot
    private List<PriceList> priceHistory; // ArrayList to store all price Lists of this parking lot

    private List<ParkingSession> activeSessions; // ArrayList to store all active parking sessions
    private List<ParkingSession> historySessions; // ArrayList to store all non - active parking sessions
    
    //Constructor
    public ParkingLot(int id, String name, String address, String city, int space) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.availableSpaces = space;
        this.conveyors = new ArrayList<>();
        this.priceHistory = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.historySessions = new ArrayList<>();
    }
    //Empty Constructor
    public ParkingLot() {
        this.id = -1;
        this.name = null;
        this.address = null;
        this.city = null;
        this.availableSpaces = -1;
        this.conveyors = new ArrayList<>();
        this.priceHistory = new ArrayList<>();
        this.activeSessions = new ArrayList<>();
        this.historySessions = new ArrayList<>();
    }
    //Getters and Setters
    public List<Conveyor> getConveyors() { return conveyors; }
    public List<PriceList> getPriceHistory(){return priceHistory;}
    public List<ParkingSession> getActiveSessions() { return activeSessions; }
    public List<ParkingSession> getHistorySessions() { return historySessions; }

    /**
     * Finds a specific conveyor by its unique ID.
     * Required by ConveyorsController for manual maintenance actions.
     * @param id The ID of the conveyor to search for.
     * @return The Conveyor object if found, null otherwise.
     */
    public Conveyor getConveyorById(int id) {
        for (Conveyor c : conveyors) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    @Override
    public String toString() {
        return "ParkingLot [id=" + id + ", name=" + name + ", address=" + address + ", city=" + city
                + ", availableSpaces=" + availableSpaces + ", conveyors=" + conveyors + ", priceHistory=" + priceHistory
                + "]";
    }
}