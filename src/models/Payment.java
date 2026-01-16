package models;

import enums.PaymentMethod;
import enums.PaymentStatus;

public class Payment {
    
    private final String paymentId;
    private final double amount;
    private final PaymentMethod method;
    private PaymentStatus status;

    public Payment(String paymentId, double amount, PaymentMethod method){
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.CREATED;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void updateStatus(PaymentStatus status){
        this.status = status;
    }
}
