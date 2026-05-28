package Gui;

public class GroupPurchase {
    private String id;
    private String productName;
    private double requiredQuantity;
    private double currentQuantity;
    private String location;

    public GroupPurchase(String id, String productName, double requiredQuantity, double currentQuantity, String location) {
        this.id = id;
        this.productName = productName;
        this.requiredQuantity = requiredQuantity;
        this.currentQuantity = currentQuantity;
        this.location = location;
    }

    public String getId() { return id; }
    public String getProductName() { return productName; }
    public double getRequiredQuantity() { return requiredQuantity; }
    public double getCurrentQuantity() { return currentQuantity; }
    public String getLocation() { return location; }
    public void setCurrentQuantity(double currentQuantity) { this.currentQuantity = currentQuantity; }
}
