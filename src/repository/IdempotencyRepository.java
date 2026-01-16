package repository;

public interface IdempotencyRepository {
    
    boolean exists(String key);

    String getPaymentId(String key);

    void save(String key, String paymentId);
}
