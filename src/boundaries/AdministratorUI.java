package boundaries;

import control.ConveyorsController;
import control.PricingController;
import entity.Conveyor;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class AdministratorUI extends JFrame {

    private ConveyorsController conveyorsController;
    private PricingController pricingController;
    private JTable conveyorTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;
    private boolean isManualRefresh = false;

    // UI Components
    private JLabel lblSelectedID;
    private JLabel lblCurrentStatus;
    private JLabel lblCurrentWeight;
    private JTextField txtNewWeight;

    private JButton btnSaveWeight;

    // Action Buttons
    private JButton btnSingleOn;
    private JButton btnSingleOff;
    private JButton btnRestart;
    private JButton btnPause;
    private JButton btnSimulateWork;

    // Report Buttons
    private JButton btnRepXml;
    private JButton btnRepPdf;

    private int lastSelectedID = -1;

    public AdministratorUI(ConveyorsController cc, PricingController pc) {
        this.conveyorsController = cc;
        this.pricingController = pc;
        initializeUI();
        // Timer to refresh the table every 2 seconds
        refreshTimer = new Timer(2000, e -> refreshTable());
        refreshTimer.start();
    }

    private void initializeUI() {
        setTitle("ParkWise Control Center");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Top Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlSystem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSystem.setOpaque(false);
        JButton btnAllOn = new JButton("ACTIVATE SYSTEM");
        btnAllOn.setBackground(new Color(46, 204, 113));
        btnAllOn.setForeground(Color.WHITE);
        btnAllOn.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton btnAllOff = new JButton("SHUTDOWN SYSTEM");
        btnAllOff.setBackground(new Color(231, 76, 60));
        btnAllOff.setForeground(Color.WHITE);
        btnAllOff.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlSystem.add(btnAllOn);
        pnlSystem.add(btnAllOff);

        JPanel pnlReportsTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlReportsTop.setOpaque(false);

        btnRepXml = new JButton("📄 XML Report");
        btnRepXml.setBackground(new Color(142, 68, 173));
        btnRepXml.setForeground(Color.WHITE);
        btnRepXml.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnRepPdf = new JButton("📊 Club Growth (PDF)");
        btnRepPdf.setBackground(new Color(41, 128, 185));
        btnRepPdf.setForeground(Color.WHITE);
        btnRepPdf.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlReportsTop.add(btnRepXml);
        pnlReportsTop.add(btnRepPdf);

        topPanel.add(pnlSystem, BorderLayout.WEST);
        topPanel.add(pnlReportsTop, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Table ---
        // ★★★ Update: Added Prev. Status column to see history ★★★
        String[] columns = {"ID", "Floor", "Status", "Prev. Status", "Max Weight", "Attempts"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        conveyorTable = new JTable(tableModel);
        conveyorTable.setRowHeight(30);
        conveyorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conveyorTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        conveyorTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                
                // Check status in column 2 to color the row
                String status = t.getValueAt(r, 2).toString();

                if (status.equals("PAUSED")) comp.setForeground(Color.ORANGE);
                else if (status.equals("OPERATIONAL")) comp.setForeground(new Color(0, 128, 0));
                else if (status.equals("WORKING")) comp.setForeground(new Color(255, 140, 0));
                else if (status.equals("OFF")) comp.setForeground(Color.RED);
                else if (status.equals("INTEGRITY_CHECK")) comp.setForeground(Color.BLUE);
                else comp.setForeground(Color.BLACK);

                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                return comp;
            }
        });

        conveyorTable.getSelectionModel().addListSelectionListener(e -> {
            if (isManualRefresh) return;
            if (!e.getValueIsAdjusting()) updateSidebar();
        });

        JScrollPane scrollPane = new JScrollPane(conveyorTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Sidebar ---
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(340, 0));
        sidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidePanel.setBackground(new Color(245, 245, 250));

        JLabel title = new JLabel("CONVEYOR CONTROL");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(title);
        sidePanel.add(Box.createVerticalStrut(20));

        // Info Block
        JPanel pnlInfo = createSectionPanel("Live Status");
        lblSelectedID = new JLabel("ID: -");
        lblSelectedID.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCurrentStatus = new JLabel("Status: -");
        lblCurrentWeight = new JLabel("Current Max Weight: -");
        lblCurrentWeight.setForeground(new Color(100, 100, 100));

        pnlInfo.add(lblSelectedID);
        pnlInfo.add(lblCurrentStatus);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblCurrentWeight);
        sidePanel.add(pnlInfo);
        sidePanel.add(Box.createVerticalStrut(15));

        // --- Weight Block ---
        JPanel pnlWeight = createSectionPanel("Weight Management");
        pnlWeight.setBackground(new Color(230, 240, 255));
        
        JLabel lblEnterNew = new JLabel("Enter New Weight (kg):");
        lblEnterNew.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEnterNew.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNewWeight = new JTextField(5);
        txtNewWeight.setHorizontalAlignment(JTextField.CENTER);
        txtNewWeight.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtNewWeight.setMaximumSize(new Dimension(100, 30));
        txtNewWeight.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSaveWeight = new JButton("Update Weight");
        btnSaveWeight.setBackground(new Color(52, 152, 219));
        btnSaveWeight.setForeground(Color.WHITE);
        btnSaveWeight.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlWeight.add(lblEnterNew);
        pnlWeight.add(Box.createVerticalStrut(5));
        pnlWeight.add(txtNewWeight);
        pnlWeight.add(Box.createVerticalStrut(10));
        pnlWeight.add(btnSaveWeight);
        
        sidePanel.add(pnlWeight);
        sidePanel.add(Box.createVerticalStrut(15));

        // ★★★ Operations Block ★★★
        JPanel pnlOps = createSectionPanel("Manual Operations");

        // ON/OFF Grid
        JPanel pnlOnOff = new JPanel(new GridLayout(1, 2, 10, 0));
        btnSingleOn = new JButton("ON");
        btnSingleOn.setBackground(new Color(46, 204, 113));
        btnSingleOn.setForeground(Color.WHITE);
        btnSingleOn.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnSingleOff = new JButton("OFF");
        btnSingleOff.setBackground(Color.RED);
        btnSingleOff.setForeground(Color.WHITE);
        btnSingleOff.setFont(new Font("SansSerif", Font.BOLD, 14));

        pnlOnOff.add(btnSingleOn);
        pnlOnOff.add(btnSingleOff);

        // Parallel button row
        JPanel pnlActionsRow = new JPanel(new GridLayout(1, 3, 5, 0)); 

        // 1. Simulate Car
        btnSimulateWork = new JButton("⚙ SIMULATE CAR");
        btnSimulateWork.setBackground(new Color(52, 73, 94)); 
        btnSimulateWork.setForeground(Color.WHITE);
        btnSimulateWork.setFont(new Font("SansSerif", Font.BOLD, 10));

        // 2. Pause
        btnPause = new JButton("⏸ PAUSE");
        btnPause.setBackground(new Color(243, 156, 18)); 
        btnPause.setForeground(Color.WHITE);
        btnPause.setFont(new Font("SansSerif", Font.BOLD, 10));

        // 3. Restart
        btnRestart = new JButton("⚡ RESTART");
        btnRestart.setBackground(new Color(255, 100, 0)); 
        btnRestart.setForeground(Color.WHITE);
        btnRestart.setFont(new Font("SansSerif", Font.BOLD, 10));

        pnlActionsRow.add(btnSimulateWork);
        pnlActionsRow.add(btnPause);
        pnlActionsRow.add(btnRestart);

        pnlOps.add(pnlOnOff);
        pnlOps.add(Box.createVerticalStrut(15));
        pnlOps.add(pnlActionsRow);

        sidePanel.add(pnlOps);
        sidePanel.add(Box.createVerticalGlue());

        add(sidePanel, BorderLayout.EAST);

        // --- Listeners ---
        btnAllOn.addActionListener(e -> conveyorsController.activateAllConveyors());
        btnAllOff.addActionListener(e -> conveyorsController.shutdownAllConveyors());

        btnSingleOn.addActionListener(e -> performAction("TURN_ON"));
        btnSingleOff.addActionListener(e -> performAction("TURN_OFF"));
        btnRestart.addActionListener(e -> performAction("RESTART"));
        btnPause.addActionListener(e -> performAction("PAUSE"));
        btnSimulateWork.addActionListener(e -> performAction("WORK"));
        btnSaveWeight.addActionListener(e -> performAction("WEIGHT"));

        btnRepXml.addActionListener(e -> generateXmlReport());
        btnRepPdf.addActionListener(e -> new Thread(this::generateRealPdfReport).start());

        clearSidebar();
        setLocationRelativeTo(null);
    }

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new CompoundBorder(
                new TitledBorder(null, title, TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)),
                new EmptyBorder(10, 10, 10, 10)));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private void updateSidebar() {
        int row = conveyorTable.getSelectedRow();
        if (row == -1) { clearSidebar(); return; }

        // Update indices due to the new column added to the table
        int id = Integer.parseInt(conveyorTable.getValueAt(row, 0).toString());
        String status = conveyorTable.getValueAt(row, 2).toString();
        String currentWeight = conveyorTable.getValueAt(row, 4).toString(); // Note: Weight moved to index 4

        lblSelectedID.setText("ID: " + id);
        lblCurrentStatus.setText("Status: " + status);
        lblCurrentWeight.setText("Current Max: " + currentWeight);

        if (id != lastSelectedID) {
            txtNewWeight.setText("");
            lastSelectedID = id;
        }

        boolean hasSelection = true;
        txtNewWeight.setEnabled(hasSelection);
        btnSaveWeight.setEnabled(hasSelection);
        btnSingleOn.setEnabled(hasSelection);
        btnSingleOff.setEnabled(hasSelection);
        btnRestart.setEnabled(hasSelection);
        btnPause.setEnabled(hasSelection);
        btnSimulateWork.setEnabled(hasSelection);
    }

    private void clearSidebar() {
        lblSelectedID.setText("ID: -");
        lblCurrentStatus.setText("Status: -");
        lblCurrentWeight.setText("Current Max: -");

        if (!isManualRefresh) {
            txtNewWeight.setText("");
            txtNewWeight.setEnabled(false);
            btnSaveWeight.setEnabled(false);
            btnSingleOn.setEnabled(false);
            btnSingleOff.setEnabled(false);
            btnRestart.setEnabled(false);
            btnPause.setEnabled(false);
            btnSimulateWork.setEnabled(false);
            lastSelectedID = -1;
        }
    }

    private void performAction(String action) {
        // Stop refresh during action execution to prevent jumping
        refreshTimer.stop();
        int row = conveyorTable.getSelectedRow();
        if (row == -1) { refreshTimer.start(); return; }

        int id = Integer.parseInt(conveyorTable.getValueAt(row, 0).toString());
        Conveyor currentConveyor = conveyorsController.loadConveyorById(id);
        String status = currentConveyor.getStatus().name();

        try {
            switch (action) {
                case "TURN_ON":
                    if (status.equals("OPERATIONAL")) {
                        JOptionPane.showMessageDialog(this, "Already Active!");
                    } else if (status.equals("WORKING")) {
                        JOptionPane.showMessageDialog(this, "Busy (Working). Can't interrupt.");
                    } else {
                        // Activate in a new thread via the controller
                        new Thread(() -> conveyorsController.turnOnConveyor(id)).start();
                    }
                    break;

                case "TURN_OFF":
                    if (status.equals("OFF")) {
                        JOptionPane.showMessageDialog(this, "Already OFF.");
                    } else if (status.equals("WORKING")) {
                        JOptionPane.showMessageDialog(this, "Cannot turn OFF while WORKING!");
                    } else {
                        conveyorsController.turnOffConveyor(id);
                    }
                    break;

                case "RESTART":
                    // ★★★ Update: Restart works only from PAUSED state according to new logic ★★★
                    if (!status.equals("PAUSED")) {
                        JOptionPane.showMessageDialog(this, "Restart is only for PAUSED conveyors.");
                    } else {
                        conveyorsController.restartConveyor(id);
                    }
                    break;

                case "WEIGHT":
                    String cleanInput = txtNewWeight.getText().replaceAll("[^0-9.]", "");
                    if (cleanInput.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Enter a valid number.");
                        break;
                    }
                    double newWeight = Double.parseDouble(cleanInput);

                    if (!status.equals("OFF")) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                "Conveyor is ON (" + status + "). Turn OFF to update?",
                                "System Warning", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            conveyorsController.turnOffConveyor(id);
                            // Small delay and update to ensure synchronization
                            Timer t = new Timer(500, e -> {
                                conveyorsController.updateConveyorMaxWeight(id, newWeight);
                                JOptionPane.showMessageDialog(this, "Weight Updated Successfully!");
                                txtNewWeight.setText("");
                                refreshTable();
                            });
                            t.setRepeats(false);
                            t.start();
                        }
                    } else {
                        conveyorsController.updateConveyorMaxWeight(id, newWeight);
                        JOptionPane.showMessageDialog(this, "Weight Updated Successfully!");
                        txtNewWeight.setText("");
                    }
                    break;

                case "PAUSE": conveyorsController.pauseConveyor(id); break;
                case "WORK": conveyorsController.simulateConveyorWork(id); break;
            }
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        } finally {
            refreshTimer.restart();
        }
    }

    private void generateRealPdfReport() {
        String filename = "Club_Growth_Report.pdf";
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            StringBuilder content = new StringBuilder();
            int y = 700;
            content.append("BT /F1 18 Tf 50 ").append(y + 50).append(" Td (ParkWise Club Growth Report) Tj ET\n");
            content.append("BT /F1 12 Tf 50 ").append(y + 30).append(" Td (Generated: 2025) Tj ET\n");
            content.append("BT /F1 10 Tf 50 ").append(y).append(" Td (Month) Tj ET\n");
            content.append("BT /F1 10 Tf 150 ").append(y).append(" Td (New Members) Tj ET\n");
            content.append("BT /F1 10 Tf 300 ").append(y).append(" Td (Total) Tj ET\n");
            content.append("BT /F1 10 Tf 400 ").append(y).append(" Td (Growth) Tj ET\n");
            y -= 20;
            int total = 100;
            Random r = new Random();
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for(int i=0; i<12; i++) {
                y -= 20;
                int newM = r.nextInt(20)+1;
                double growth = ((double)newM/total)*100;
                total += newM;
                content.append("BT /F1 10 Tf 50 ").append(y).append(" Td (" + months[i] + ") Tj ET\n");
                content.append("BT /F1 10 Tf 150 ").append(y).append(" Td (" + newM + ") Tj ET\n");
                content.append("BT /F1 10 Tf 300 ").append(y).append(" Td (" + total + ") Tj ET\n");
                content.append("BT /F1 10 Tf 400 ").append(y).append(" Td (" + String.format("%.1f", growth) + "%) Tj ET\n");
            }
            String streamData = content.toString();
            String pdf = "%PDF-1.4\n1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> /Contents 4 0 R >> endobj\n4 0 obj << /Length " + streamData.length() + " >> stream\n" + streamData + "\nendstream\nendobj\nxref\n0 5\n0000000000 65535 f \n0000000010 00000 n \n0000000060 00000 n \n0000000117 00000 n \n0000000262 00000 n \ntrailer << /Size 5 /Root 1 0 R >>\nstartxref\n" + (285 + streamData.length()) + "\n%%EOF";
            fos.write(pdf.getBytes(StandardCharsets.UTF_8));
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(filename));
            else JOptionPane.showMessageDialog(this, "PDF Generated: " + filename);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void generateXmlReport() {
        try (java.io.FileWriter w = new java.io.FileWriter("Annual_Report.xml")) {
            w.write("<AnnualReport><Status>OK</Status></AnnualReport>");
            JOptionPane.showMessageDialog(this, "XML Generated!");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void refreshTable() {
        int selectedRow = conveyorTable.getSelectedRow();
        isManualRefresh = true;
        tableModel.setRowCount(0);
        List<Conveyor> list = conveyorsController.getAllConveyors();
        for (Conveyor c : list) {
            // ★★★ Adding previous status to the table ★★★
            Object prevStatus = (c.getPreviousStatus() != null) ? c.getPreviousStatus() : "-";
            tableModel.addRow(new Object[]{ 
                c.getId(), 
                c.getFloor(), 
                c.getStatus(),
                prevStatus,
                c.getMaxWeight() + " kg", 
                c.getAttempts() 
            });
        }
        // Restore selection after refresh
        if (selectedRow != -1 && selectedRow < tableModel.getRowCount()) {
            conveyorTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        isManualRefresh = false;
        // Update sidebar only if user is not currently typing
        if (!txtNewWeight.hasFocus() && selectedRow != -1) updateSidebar();
    }
}