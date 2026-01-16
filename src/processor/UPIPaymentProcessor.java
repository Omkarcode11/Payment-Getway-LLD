package processor;

import enums.PaymentStatus;
import models.Payment;

public class UPIPaymentProcessor implements PaymentProcessor {

    public void process(Payment payment) {
        System.out.println("Processing UPI payment: " + payment.getPaymentId());

        payment.updateStatus(PaymentStatus.PROCESSING);

        payment.updateStatus(PaymentStatus.SUCCESS);
    }

}
