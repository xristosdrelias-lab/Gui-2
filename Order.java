package Gui;

public class Order {
    private final int orderId;
    private final int customerId;
    private final String productName;
    private final double price;
    private String status;

    public Order(int orderId, int customerId, String productName, double price) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productName = productName;
        this.price = price;
        this.status = "Εκκρεμής";
    }

    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}