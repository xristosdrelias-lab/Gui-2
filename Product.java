package Gui;

public class Product {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String pricingRules;
    private boolean available;
    private double producerRating;
    private String deliveryMethod;
    private double distance;

    public Product(String productId, String name, int quantity, double price, String pricingRules, boolean available, double producerRating, String deliveryMethod, double distance) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.pricingRules = pricingRules;
        this.available = available;
        this.producerRating = producerRating;
        this.deliveryMethod = deliveryMethod;
        this.distance = distance;
    }

    public String getProductId() { return productId; }
    public String getProductName() { return name; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getStock() { return quantity; }
    public double getPrice() { return price; }
    public String getPricingRules() { return pricingRules; }
    public boolean isAvailable() { return available; }
    public double getProducerRating() { return producerRating; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public double getDistance() { return distance; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStock(int stock) { this.quantity = stock; }
}
