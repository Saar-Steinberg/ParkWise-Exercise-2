package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import entity.Conveyor;
import enums.ConveyorStatus;
import utils.DBConnection;

public class ConveyorsController {

    private Connection conn;
    private ExecutorService executor; // Thread manager

    public ConveyorsController() {
        this.conn = DBConnection.getInstance().getConnection();
        // Creating a thread pool (4 parallel) to prevent overload
        this.executor = Executors.newFixedThreadPool(4); 
    }

    // --- Data Retrieval ---
    
    public List<Conveyor> getAllConveyors() {
        List<Conveyor> list = new ArrayList<>();
        String sql = "SELECT * FROM Conveyors";
        
        synchronized(this) { 
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Conveyor c = mapResultSetToConveyor(rs);
                    list.add(c);
                }
            } catch (SQLException e) { System.err.println("DB Error: " + e.getMessage()); }
        }
        return list;
    }

    public synchronized Conveyor loadConveyorById(int id) {
        String sql = "SELECT * FROM Conveyors WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConveyor(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- Global System Operations ---

    public void activateAllConveyors() {
        List<Integer> ids = getAllConveyorIds();
        for (int id : ids) {
            executor.submit(() -> turnOnConveyor(id));
        }
    }

    public void shutdownAllConveyors() {
        List<Integer> ids = getAllConveyorIds();
        for (int id : ids) {
            executor.submit(() -> turnOffConveyor(id));
        }
    }

    // --- Single Conveyor Operations ---

    public void turnOnConveyor(int id) {
        Conveyor c = loadConveyorById(id);
        if (c == null) return;
        
        // Integrity checks before activation
        if (c.getStatus() == ConveyorStatus.OPERATIONAL || 
            c.getStatus() == ConveyorStatus.WORKING || 
            c.isWeightChanged()) return; 

        c.resetAttempts();
        c.setStatus(ConveyorStatus.INTEGRITY_CHECK);
        updateConveyorStatus(c);
        
        startManualDiagnostics(id);
    }

    public void turnOffConveyor(int id) {
        Conveyor c = loadConveyorById(id);
        if (c != null && c.getStatus() != ConveyorStatus.WORKING) {
            // History saving is handled within the Entity's Setter
            c.setStatus(ConveyorStatus.OFF);
            updateConveyorStatus(c);
        }
    }

    public void pauseConveyor(int id) {
        Conveyor c = loadConveyorById(id);
        if (c != null && c.getStatus() != ConveyorStatus.OFF && c.getStatus() != ConveyorStatus.PAUSED) {
            // The Entity automatically saves the previous status when switching to PAUSED
            c.setStatus(ConveyorStatus.PAUSED);
            updateConveyorStatus(c);
        }
    }

    public void restartConveyor(int conveyorId) {
        executor.submit(() -> {
            Conveyor c = loadConveyorById(conveyorId);
            
            // Restart works only if we are in PAUSED state
            if (c != null && c.getStatus() == ConveyorStatus.PAUSED) {
                ConveyorStatus targetStatus = c.getPreviousStatus();
                // If no history is saved, default to integrity check
                if (targetStatus == null) targetStatus = ConveyorStatus.INTEGRITY_CHECK;

                switch (targetStatus) {
                    case INTEGRITY_CHECK:
                        c.resetAttempts();
                        c.setStatus(ConveyorStatus.INTEGRITY_CHECK);
                        updateConveyorStatus(c);
                        startManualDiagnostics(conveyorId);
                        break;

                    case WORKING:
                        c.setStatus(ConveyorStatus.WORKING);
                        updateConveyorStatus(c);
                        simulateConveyorWork(conveyorId); 
                        break;

                    case OPERATIONAL:
                        c.setStatus(ConveyorStatus.OPERATIONAL);
                        updateConveyorStatus(c);
                        break;

                    default: // OFF or unexpected state
                        c.resetAttempts();
                        c.setStatus(ConveyorStatus.INTEGRITY_CHECK);
                        updateConveyorStatus(c);
                        startManualDiagnostics(conveyorId);
                        break;
                }
            }
        });
    }

    public boolean updateConveyorMaxWeight(int conveyorId, double weight) {
        Conveyor c = loadConveyorById(conveyorId);
        if (c != null && c.getStatus() == ConveyorStatus.OFF) {
            c.setMaxWeight(weight);
            c.setWeightChanged(false);
            updateConveyorStatus(c);
            return true;
        }
        return false;
    }

    // --- Simulations and Processes ---

    public void startManualDiagnostics(int conveyorId) {
        Conveyor c = loadConveyorById(conveyorId);
        if (c == null) return;

        while (c.getAttempts() < 3 && c.getStatus() != ConveyorStatus.OPERATIONAL) {
            try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } 
            
            // Reload to ensure the conveyor wasn't turned off during the check
            c = loadConveyorById(conveyorId);
            if (c.getStatus() == ConveyorStatus.PAUSED || c.getStatus() == ConveyorStatus.OFF) return;

            boolean isSuccess = Math.random() > 0.5; 
            if (isSuccess) {
                c.resetAttempts();
                c.setStatus(ConveyorStatus.OPERATIONAL);
            } else {
                c.incrementAttempts();
                if (c.getAttempts() >= 3) {
                    c.setStatus(ConveyorStatus.PAUSED); // Will switch to PAUSED and save INTEGRITY_CHECK as history
                } else {
                    c.setStatus(ConveyorStatus.INTEGRITY_CHECK);
                }
            }
            updateConveyorStatus(c);
        }
    }

    public void simulateConveyorWork(int id) {
        executor.submit(() -> {
            Conveyor c = loadConveyorById(id);
            if (c != null && (c.getStatus() == ConveyorStatus.OPERATIONAL || c.getStatus() == ConveyorStatus.WORKING)) {
                
                if (c.getStatus() == ConveyorStatus.OPERATIONAL) {
                    c.setStatus(ConveyorStatus.WORKING);
                    updateConveyorStatus(c);
                }

                try { Thread.sleep(5000); } catch (InterruptedException e) { }
                
                c = loadConveyorById(id);
                // Ensure it wasn't stopped in the middle
                if (c.getStatus() == ConveyorStatus.WORKING) {
                    c.setStatus(ConveyorStatus.OPERATIONAL);
                    updateConveyorStatus(c);
                }
            }
        });
    }

    // --- Helpers ---

    private List<Integer> getAllConveyorIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT ID FROM Conveyors";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt("ID"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    private Conveyor mapResultSetToConveyor(ResultSet rs) throws SQLException {
        Conveyor c = new Conveyor(
            rs.getInt("ID"), 
            rs.getInt("FloorNum"), 
            rs.getInt("X_Pos"), 
            rs.getInt("Y_Pos"), 
            rs.getDouble("MaxWeight")
        );
        c.setAttempts(rs.getInt("Attempts"));
        c.setWeightChanged(rs.getBoolean("IsWeightChanged"));
        c.setStatus(ConveyorStatus.valueOf(rs.getString("Status")));
        
        // Loading history - Important!
        String prev = rs.getString("PreviousStatus");
        if (prev != null && !prev.isEmpty()) {
            try {
                c.setPreviousStatus(ConveyorStatus.valueOf(prev));
            } catch (IllegalArgumentException e) {
                c.setPreviousStatus(ConveyorStatus.OFF);
            }
        }
        return c;
    }

    private synchronized void updateConveyorStatus(Conveyor c) {
        // Includes updating the new PreviousStatus column
        String sql = "UPDATE Conveyors SET Status = ?, Attempts = ?, IsWeightChanged = ?, MaxWeight = ?, PreviousStatus = ? WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getStatus().name());
            pstmt.setInt(2, c.getAttempts());
            pstmt.setBoolean(3, c.isWeightChanged());
            pstmt.setDouble(4, c.getMaxWeight());
            
            if (c.getPreviousStatus() != null)
                pstmt.setString(5, c.getPreviousStatus().name());
            else
                pstmt.setString(5, null);

            pstmt.setInt(6, c.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("Database Update Error: " + e.getMessage()); 
        }
    }
    
    public void closeController() {
        if (executor != null) executor.shutdownNow();
    }
}