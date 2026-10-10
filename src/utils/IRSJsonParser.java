package utils;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class IRSJsonParser {

    // Inner classes for json file
    public class PriceListDTO {
        public int parkingLotId;
        public String effectiveDate;
        public String currency;
        public RatesDTO rates;
    }

    public class RatesDTO {
        public double firstHour;
        public double extraHour;
        public double fullDay;
    }


    /**
     * Input: A file path
     * Effect: Turns the data from file to array of objects
     * Output: Returns null if failed, array of objects otherwise
     */
    public static PriceListDTO[] parsePriceList(String filePath) {
        Gson gson = new Gson();
        
        try (Reader reader = new FileReader(filePath)) {
            
            System.out.println(">> Parser: Reading JSON from disk...");
            return gson.fromJson(reader, PriceListDTO[].class);
            
        } catch (IOException e) {
            System.err.println(">> Parser Error: Could not read file. " + e.getMessage());
            return null;
        }
    }
}