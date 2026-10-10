package boundaries;

import control.PricingController;

public class IRSFileAdapter implements IRSystemInterface {

    private PricingController myController;

    public void setController(PricingController controller) {
        this.myController = controller;
    }

    @Override
    public void onPriceListReceived(String jsonFilePath) {
        if (this.myController != null) {
            System.out.println(">> IRS Adapter: Passing file path to controller: " + jsonFilePath);
            this.myController.importPriceList(jsonFilePath);
        } else {
            System.err.println(">> Error: No controller connected to IRS Adapter!");
        }
    }
}