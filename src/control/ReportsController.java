package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DBConnection;

public class ReportsController {

    private Connection conn;

    public ReportsController() {
        
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Generates an Annual Summary Report 
     * Includes: List of parking lots, total income, and count of "Other" colored vehicles.
     * Ordered by City (DESC) and Total Amount (ASC).
     */
    public File generateAnnualReport(int year, String format) {
        System.out.println(">> Generating Annual Report for year " + year + " in format: " + format);
        
        String sql = "SELECT L.City, L.Name, SUM(S.PaymentAmount) as TotalIncome, " +
                     "COUNT(CASE WHEN V.Color = 'OTHER' THEN 1 END) as OtherColorCount " +
                     "FROM ParkingLots L " +
                     "JOIN ParkingSessions S ON L.ID = S.ParkingLotID " +
                     "JOIN Vehicles V ON S.VehicleID = V.ID " +
                     "WHERE YEAR(S.EntryTime) = ? " +
                     "GROUP BY L.City, L.Name " +
                     "ORDER BY L.City DESC, TotalIncome ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                File reportFile = new File("AnnualReport_" + year + "." + format.toLowerCase());
                
                if (format.equalsIgnoreCase("PDF")) {
                    generatePDF(rs, reportFile);
                } else if (format.equalsIgnoreCase("XML")) {
                    generateXML(rs, reportFile);
                } else {
                    System.err.println("Unsupported format: " + format);
                    return null;
                }
                
                return reportFile;
            }
        } catch (SQLException e) {
            System.err.println("Error generating annual report: " + e.getMessage());
            return null;
        }
    }
    /**
     * Generates a Customer Club Growth Report based on source [63-65].
     * Ordered by month ASC.
     */
    public File generateClubGrowthReport(int year) {
        System.out.println(">> Generating Club Growth Report for year " + year);
        
        
        String sql = "SELECT MONTH(MemberRegistrationDate) as Month, " +
                     "COUNT(*) as NewMembers " +
                     "FROM Clients " +
                     "WHERE YEAR(MemberRegistrationDate) = ? AND IsMember = TRUE " +
                     "GROUP BY MONTH(MemberRegistrationDate) " +
                     "ORDER BY Month ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                File reportFile = new File("ClubGrowthReport_" + year + ".txt");
                
                try (FileWriter writer = new FileWriter(reportFile)) {
                    writer.write("Month\tNew Members\tGrowth %\n");
                    
                    int previousMonthTotal = 0; 
                    
                    while (rs.next()) {
                        int month = rs.getInt("Month");
                        int newMembers = rs.getInt("NewMembers");
                        
                        String growth = (previousMonthTotal == 0) ? "N/A" : ((double)newMembers / previousMonthTotal * 100) + "%";
                        
                        writer.write(month + "\t" + newMembers + "\t" + growth + "\n");
                        
                        previousMonthTotal += newMembers; 
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                System.out.println(">> Report created at: " + reportFile.getAbsolutePath());
                return reportFile;
            }
        } catch (SQLException e) {
            System.err.println("Error generating growth report: " + e.getMessage());
            return null;
        }
    }

    // --- Helper Methods ---
    private void generatePDF(ResultSet rs, File file) {
        System.out.println("   [Simulating PDF creation logic for " + file.getName() + "]");
    }

    private void generateXML(ResultSet rs, File file) {
        System.out.println("   [Simulating XML creation logic for " + file.getName() + "]");
    }
}