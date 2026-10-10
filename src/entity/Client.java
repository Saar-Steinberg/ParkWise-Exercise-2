package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.LocalDate;

public class Client {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isMember;
    private LocalDate memberRegistrationDate; // Registration Date
    private List<Vehicle> vehicles; // Client's vehicles
    private Map<LocalDate, Integer> preferredLotsHistory; // key = picking date, value = lotID ----- only if member-----

    // Constructor
    public Client(int id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isMember = false; // A new Client is yet to be a member
        this.vehicles = new ArrayList<>();
        this.memberRegistrationDate = null;
        this.preferredLotsHistory = new TreeMap<>(); // We chose TreeMap so dates will be chroicly saved
    }
    //Empty Constructor
    public Client(){
        this.id = -1;
        this.firstName = null;
        this.lastName = null;
        this.phoneNumber = null;
        this.isMember = false;
        this.vehicles = new ArrayList<>();
        this.memberRegistrationDate = null;
        this.preferredLotsHistory = new TreeMap<>();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public LocalDate getMemberRegistrationDate() {
        return memberRegistrationDate;
    }
    public void setMemberRegistrationDate(LocalDate memberRegistrationDate) {
        this.memberRegistrationDate = memberRegistrationDate;
    }
    public Map<LocalDate, Integer> getPreferredLotsHistory() {
        return preferredLotsHistory;
    }
    public void setPreferredLotsHistory(Map<LocalDate, Integer> preferredLotsHistory) {
        this.preferredLotsHistory = preferredLotsHistory;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean isMember() {
        return isMember;
    }
    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
    @Override
    public String toString() {
        return "Client [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
                + phoneNumber + ", isMember=" + isMember + ", vehicles=" + vehicles + "]";
    }

    

    
}