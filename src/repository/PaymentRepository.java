package repository;

import models.Payment;

public class PaymentRepository implements Repository {
    public void save(Payment payment){
        
    }

    public Payment getById(String id){
        return new Payment(id, 0, null);
    }

    public void update(Payment payment){

    }
}
