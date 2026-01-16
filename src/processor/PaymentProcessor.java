package processor;

import models.Payment;

public interface PaymentProcessor {
    void process(Payment payment);
}
