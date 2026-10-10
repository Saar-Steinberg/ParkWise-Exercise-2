import java.io.File;
import java.sql.Connection;
import boundaries.IRSFileAdapter;
import boundaries.AdministratorUI;
import control.ConveyorsController;
import control.PricingController;
import control.ReportsController;
import utils.DBConnection;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        // --- Phase 1: Database Connection Testing ---
        System.out.println("--- Starting Database Connection Test ---");

        try {
            // Attempt to get the database connection singleton instance
            DBConnection dbInstance = DBConnection.getInstance();
            Connection conn = dbInstance.getConnection();

            if (conn != null) {
                System.out.println("Status: Connection established successfully.");
                System.out.println("Driver: " + conn.getMetaData().getDriverName());
            } else {
                System.err.println("Status: Failed. Connection object is null.");
            }

        } catch (Exception e) {
            System.err.println("Status: Critical Error during connection.");
            e.printStackTrace();
        }

        System.out.println("--- Test Finished ---");

        // --- Phase 2: IRS Integration Testing (JSON Import) ---
        System.out.println("\n====== Starting IRS Import Test ======");
        System.out.println("1. Initializing Controllers and Adapters...");
        
        PricingController pricingController = new PricingController();
        IRSFileAdapter adapter = new IRSFileAdapter();
        adapter.setController(pricingController);
        
        String filePath = "irs_prices_2026.json";
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.err.println("Error: File not found at: " + file.getAbsolutePath());
        } else {
            System.out.println("File found! Path: " + file.getAbsolutePath());
            System.out.println("2. Triggering the Adapter...");
            adapter.onPriceListReceived(filePath);
        }
        System.out.println("====== Test Finished ======");

        // --- Phase 3: Reports Generation Testing ---
        System.out.println("\n====== Starting Reports Controller Test ======");
        ReportsController reportController = new ReportsController();
        int testYear = 2025;

        // Testing Annual PDF Report
        System.out.println("\n--- Testing Annual Report (PDF) ---");
        File annualReport = reportController.generateAnnualReport(testYear, "PDF");
        if (annualReport != null) {
            System.out.println("Annual Report generated successfully: " + annualReport.getName());
        } else {
            System.err.println("Failed to generate Annual Report.");
        }

        // Testing Club Growth Report
        System.out.println("\n--- Testing Club Growth Report ---");
        File growthReport = reportController.generateClubGrowthReport(testYear);
        if (growthReport != null) {
            System.out.println("Club Growth Report generated successfully: " + growthReport.getName());
        } else {
            System.err.println("Failed to generate Growth Report.");
        }
        System.out.println("====== Test Finished ======");

        // --- Phase 4: Launching the Administrator GUI ---
        // This launches the UI created based on the system requirements
        System.out.println("\n====== Launching Administrator UI ======");
        
        // Initializing the ConveyorsController to handle UI actions
        ConveyorsController conveyorsController = new ConveyorsController();
        
        // Execute UI launch on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Creating the UI with required controllers
                AdministratorUI adminUI = new AdministratorUI(conveyorsController, pricingController);
                
                // Centering the window on the screen
                adminUI.setLocationRelativeTo(null);
                
                // Making the window visible to the user
                adminUI.setVisible(true);
                
                System.out.println("System Alert: Administrator UI is now active and visible.");
            } catch (Exception e) {
                System.err.println("Critical Error: Could not launch Administrator UI.");
                e.printStackTrace();
            }
        });
    }
}