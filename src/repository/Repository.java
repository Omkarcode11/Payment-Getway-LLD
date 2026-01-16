package repository;

import models.Payment;

public interface Repository {
    public void save(Payment payment);
    public Payment getById(String paymentId);
    public void update(Payment payment);
}
