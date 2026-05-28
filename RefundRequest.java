package Gui;

public class RefundRequest {
    private final int refundId;
    private final int orderId;
    private final double requestedAmount;
    private RefundStatus status;

    public RefundRequest(int refundId, int orderId, double amount) {
        this.refundId = refundId;
        this.orderId = orderId;
        this.requestedAmount = amount;
        this.status = RefundStatus.PENDING;
    }

    public int getRefundId() { return refundId; }
    public int getOrderId() { return orderId; }
    public double getRequestedAmount() { return requestedAmount; }
    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
}