package boundaries;

public interface IRSystemInterface {
    
    /*
     * Triggered when a new price list file is detected.
     */
    void onPriceListReceived(String jsonFilePath);
}