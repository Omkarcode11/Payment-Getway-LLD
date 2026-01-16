package processor;

import enums.PaymentStatus;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import models.Payment;

public class AsyncPaymentExecutor {

    private final ExecutorService executor
            = Executors.newFixedThreadPool(5);

    public void execute(Payment payment) {
        executor.submit(() -> {
            try {
                payment.updateStatus(PaymentStatus.PROCESSING);

                Thread.sleep(2000);

                boolean success = new Random().nextBoolean();

                if (success) {
                    payment.updateStatus(PaymentStatus.SUCCESS);
                } else {
                    payment.updateStatus(PaymentStatus.FAILED);
                }
            } catch (Exception e) {
                payment.updateStatus(PaymentStatus.PENDING);
            }
        });
    }
}
