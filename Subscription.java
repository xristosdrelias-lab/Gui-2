package Gui;

public class Subscription {
    private String productName;
    private int quantity;
    private String frequency;
    private String status;

    public Subscription(String name, int qty, String freq) {
        this.productName = name;
        this.quantity = qty;
        this.frequency = freq;
        this.status = "Ενεργή";
    }
    public String getProductName() {
        return productName;
    }
}
