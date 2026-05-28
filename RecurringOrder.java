package Gui;

public class RecurringOrder {
    private final String orderId;
    private final Product product;
    private final int quantity;
    private final String frequency;
    private String status;

    public RecurringOrder(String orderId, Product product, int quantity, String frequency) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.frequency = frequency;
        this.status = "ACTIVE";
    }

    public String getOrderId() { return orderId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getFrequency() { return frequency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}