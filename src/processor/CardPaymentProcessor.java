package processor;

import models.Payment;

public class CardPaymentProcessor implements PaymentProcessor {

    private final AsyncPaymentExecutor executor = new AsyncPaymentExecutor();
    
    public void process(Payment payment){
        System.err.println("Submitting Card payment async");
        executor.execute(payment);
    }
}
