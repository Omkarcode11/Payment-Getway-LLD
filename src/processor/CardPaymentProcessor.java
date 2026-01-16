package processor;

import enums.PaymentStatus;
import models.Payment;

public class CardPaymentProcessor implements PaymentProcessor {
    
    public void process(Payment payment){

        // Simulate bank call

        payment.updateStatus(PaymentStatus.PROCESSING);


        // Assume success for now 

        payment.updateStatus(PaymentStatus.SUCCESS);
    }
}
