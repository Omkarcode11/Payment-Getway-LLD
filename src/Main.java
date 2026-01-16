
import enums.PaymentMethod;
import java.util.HashMap;
import java.util.Map;
import models.Payment;
import processor.AsyncPaymentExecutor;
import processor.CardPaymentProcessor;
import processor.PaymentProcessor;
import processor.UPIPaymentProcessor;
import repository.IdempotencyRepository;
import repository.InMemoryIdempotencyRepository;
import repository.InMemoryPaymentRepository;
import repository.Repository;
import service.PaymentService;

public class Main {

    public static void main(String[] args) {

        // 1. Repository
        Repository paymentRepository = new InMemoryPaymentRepository();
        IdempotencyRepository idempotencyRepository = new InMemoryIdempotencyRepository();

        AsyncPaymentExecutor asyncPaymentExecutor = new AsyncPaymentExecutor();

        // 2. Processors
        Map<PaymentMethod, PaymentProcessor> processorMap = new HashMap<>();

        processorMap.put(PaymentMethod.CARD, new CardPaymentProcessor());
        processorMap.put(PaymentMethod.UPI, new UPIPaymentProcessor(asyncPaymentExecutor));

        // 3. Service
        PaymentService paymentService
                = new PaymentService(processorMap, paymentRepository, idempotencyRepository);

        // 4. Create payment
        Payment payment = paymentService.createPayment(1000.0, PaymentMethod.CARD, "asfsdf");
        System.out.println("Payment Created: " + payment.getPaymentId());

        // 5. Process payment
        paymentService.processPayment(payment.getPaymentId());

        // 6. Get status
        Payment finalPayment
                = paymentService.getPaymentStatus(payment.getPaymentId());

        System.out.println("Final Status: " + finalPayment.getStatus());
    }
}
