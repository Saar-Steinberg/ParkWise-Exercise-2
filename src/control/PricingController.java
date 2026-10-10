package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import boundaries.IRSystemInterface;
import boundaries.IRSFileAdapter;
import utils.DBConnection;
import utils.IRSJsonParser;
import utils.IRSJsonParser.PriceListDTO;

public class PricingController {

    private IRSystemInterface irsInterface;
    private Connection conn;

    public PricingController() {
        this.conn = DBConnection.getInstance().getConnection();
        IRSFileAdapter adapter = new IRSFileAdapter();
        adapter.setController(this); 
        this.irsInterface = adapter;
    }
    /**
     * Input: A file path
     * Effect: Imports data from file using parsePriceList function and inserting it to data base using insertPriceListToDB
     */
    public void importPriceList(String jsonFilePath) {
        System.out.println(">> PricingController: Controller received path. Asking Parser to work...");
        
        PriceListDTO[] importedLists = IRSJsonParser.parsePriceList(jsonFilePath);

        if (importedLists == null || importedLists.length == 0) {
            System.out.println(">> Controller: Parsing returned empty or null. Aborting.");
            return;
        }

        int successCount = 0;
        int duplicateCount = 0; 

        for (PriceListDTO dto : importedLists) {
            if (insertPriceListToDB(dto)) {
                successCount++;
            } else {
                duplicateCount++;
            }
        }
        
        System.out.println(">> Controller: Done. Added " + successCount + " new lists. Skipped " + duplicateCount + " duplicates.");
    }

    /**
     * Inserts price list to data base 
     */
    private boolean insertPriceListToDB(PriceListDTO dto) {
        LocalDate date = LocalDate.parse(dto.effectiveDate, DateTimeFormatter.ISO_LOCAL_DATE);
        Timestamp sqlDate = Timestamp.valueOf(date.atStartOfDay());

        // Checking for duplicates
        if (checkIfPriceListExists(dto.parkingLotId, sqlDate)) {
            System.out.println("   -> DB Info: Price list already exists for Lot " + dto.parkingLotId + " on date " + dto.effectiveDate + ". Skipping.");
            return false; 
        }

        // If does not exits - add to DB
        String sql = "INSERT INTO PriceLists " +
                     "(ParkingLotID, EffectiveDate, FirstHourPrice, ExtraHourPrice, FullDayPrice) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dto.parkingLotId);
            pstmt.setTimestamp(2, sqlDate);
            pstmt.setDouble(3, dto.rates.firstHour);
            pstmt.setDouble(4, dto.rates.extraHour);
            pstmt.setDouble(5, dto.rates.fullDay);

            pstmt.executeUpdate();
            System.out.println("   -> DB Success: Inserted price list for Lot ID: " + dto.parkingLotId);
            return true;

        } catch (SQLException e) {
            System.err.println("   -> DB Error for Lot ID " + dto.parkingLotId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Input: A parking lot id, an effective date
     * Effect: Checking if the price list already exists for this parking lot in this year
     */
    private boolean checkIfPriceListExists(int parkingLotId, Timestamp effectiveDate) {
        String query = "SELECT ParkingLotID FROM PriceLists WHERE ParkingLotID = ? AND EffectiveDate = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, parkingLotId);
            pstmt.setTimestamp(2, effectiveDate);
            
            try (ResultSet rs = pstmt.executeQuery()) { // Returns true if exists
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking duplication: " + e.getMessage());
            return false; 
        }
    }
}