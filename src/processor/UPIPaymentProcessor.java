package processor;

import models.Payment;

public class UPIPaymentProcessor implements PaymentProcessor {

    private final AsyncPaymentExecutor paymentExecutor;

    public UPIPaymentProcessor(AsyncPaymentExecutor asyncPaymentExecutor) {
        this.paymentExecutor = asyncPaymentExecutor;
    }

    public void process(Payment payment) {
        System.out.println("Processing UPI payment: " + payment.getPaymentId());

        paymentExecutor.execute(payment);
    }

}
