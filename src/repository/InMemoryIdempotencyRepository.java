package repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIdempotencyRepository implements IdempotencyRepository {

    public final Map<String, String> store = new ConcurrentHashMap<>();

    public boolean exists(String key) {
        return store.containsKey(key);
    }

    public String getPaymentId(String key) {
        return store.get(key);
    }

    public void save(String key, String paymentId) {
        store.put(key, paymentId);
    }
}
