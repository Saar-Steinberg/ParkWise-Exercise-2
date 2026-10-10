package boundaries;

import control.ConveyorsController;
import control.PricingController;

import javax.swing.*;
import java.awt.*;

public class ClientUI extends JFrame {
    // Controllers
    private ConveyorsController conveyorsController;
    private PricingController pricingController;

    public ClientUI(ConveyorsController cc, PricingController pc) {
        this.conveyorsController = cc;
        this.pricingController = pc;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ParkWise - Payment Station");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel lblInstruction = new JLabel("Please enter your License Plate to exit:");
        JTextField txtLicensePlate = new JTextField(10); 
        JButton btnPayAndExit = new JButton("Pay & Retrieve Vehicle");

        add(lblInstruction);
        add(txtLicensePlate);
        add(btnPayAndExit);

        //
        btnPayAndExit.addActionListener(e -> {
            String plate = txtLicensePlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate.");
                return;
            }


        });
    }
}