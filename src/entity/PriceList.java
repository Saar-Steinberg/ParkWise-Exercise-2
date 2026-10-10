package entity;

import java.util.Date;

public class PriceList {
    private int id;
    private Date effectiveDate; // Validation date of this price list
    private double firstHourPrice;
    private double extraHourPrice;
    private double fullDayPrice;
    
    //Constructor
    public PriceList(int id, Date effectiveDate, double firstHourPrice, double extraHourPrice, double fullDayPrice) {
        this.id = id;
        this.effectiveDate = effectiveDate;
        this.firstHourPrice = firstHourPrice;
        this.extraHourPrice = extraHourPrice;
        this.fullDayPrice = fullDayPrice;
    }
    //Empty Constructor
    public PriceList(){
        this.id = -1;
        this.effectiveDate = null;
        this.firstHourPrice = -1;
        this.extraHourPrice = -1;
        this.fullDayPrice = -1;
    }
    //Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public double getFirstHourPrice() {
        return firstHourPrice;
    }
    public void setFirstHourPrice(double firstHourPrice) {
        this.firstHourPrice = firstHourPrice;
    }
    public double getExtraHourPrice() {
        return extraHourPrice;
    }
    public void setExtraHourPrice(double extraHourPrice) {
        this.extraHourPrice = extraHourPrice;
    }
    public double getFullDayPrice() {
        return fullDayPrice;
    }
    public void setFullDayPrice(double fullDayPrice) {
        this.fullDayPrice = fullDayPrice;
    }
    @Override
    public String toString() {
        return "PriceList [id=" + id + ", effectiveDate=" + effectiveDate + ", firstHourPrice=" + firstHourPrice
                + ", extraHourPrice=" + extraHourPrice + ", fullDayPrice=" + fullDayPrice + "]";
    }
    
    
    
}