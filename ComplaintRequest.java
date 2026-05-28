package Gui;

public class ComplaintRequest {
    private int requestID;
    private String status;
    private int orderID;
    private double refundAmount;

    public ComplaintRequest(int requestID, int orderID, double refundAmount) {
        this.requestID = requestID;
        this.status = "Εκκρεμής";
        this.orderID = orderID;
        this.refundAmount = refundAmount;
    }

    public int getRequestID() { return requestID; }
    public String getStatus() { return status; }
    public int getOrderID() { return orderID; }
    public double getRefundAmount() { return refundAmount; }
    public void setStatus(String status) { this.status = status; }
}
