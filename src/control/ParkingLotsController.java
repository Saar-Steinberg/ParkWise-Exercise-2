package control;

import entity.ParkingLot;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingLotsController {

    private Connection conn;

    public ParkingLotsController() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Loads a parking lot from DB using the full column name 'AvailableSpaces'.
     */
    public ParkingLot loadParkingLotById(int id) {
        String sql = "SELECT * FROM ParkingLots WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingLot(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Address"),
                        rs.getString("City"),
                        rs.getInt("AvailableSpaces") // Updated from AvailableSpa
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading lot (Check if column 'AvailableSpaces' exists): " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the number of available spaces.
     */
    public void updateAvailability(int lotId, int newCount) {
        String sql = "UPDATE ParkingLots SET AvailableSpaces = ? WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newCount);
            pstmt.setInt(2, lotId);
            pstmt.executeUpdate();
            System.out.println("DB Sync: Parking lot " + lotId + " updated to " + newCount + " spaces.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addParkingLot(ParkingLot lot) {
        String sql = "INSERT INTO ParkingLots (ID, Name, Address, City, AvailableSpaces) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lot.getId());
            pstmt.setString(2, lot.getName());
            pstmt.setString(3, lot.getAddress());
            pstmt.setString(4, lot.getCity());
            pstmt.setInt(5, lot.getAvailableSpaces());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}