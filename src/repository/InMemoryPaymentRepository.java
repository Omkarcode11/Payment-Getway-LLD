package repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import models.Payment;

public class InMemoryPaymentRepository implements Repository {

    private final Map<String, Payment> store = new ConcurrentHashMap<>();
    
    public void save(Payment payment) {
        store.put(payment.getPaymentId(), payment);
    }

    public Payment getById(String paymentId) {
        Payment payment = store.get(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        return payment;
    }

    public void update(Payment payment) {
        store.put(payment.getPaymentId(), payment);
    }

}
