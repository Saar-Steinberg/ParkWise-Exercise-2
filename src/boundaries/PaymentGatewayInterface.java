package boundaries;

public interface PaymentGatewayInterface {
    /**
     * Sends a charge request to the external credit card company.
     * Returns true if payment approved, false otherwise.
     */
    boolean chargeCreditCard(String vehicleId, double amount, String creditCardToken);
}