
import enums.PaymentMethod;
import java.util.HashMap;
import java.util.Map;
import models.Payment;
import processor.CardPaymentProcessor;
import processor.PaymentProcessor;
import processor.UPIPaymentProcessor;
import repository.InMemoryPaymentRepository;
import repository.Repository;
import service.PaymentService;

public class Main {

    public static void main(String[] args) {

        // 1. Repository
        Repository paymentRepository = new InMemoryPaymentRepository();

        // 2. Processors
        Map<PaymentMethod, PaymentProcessor> processorMap = new HashMap<>();
        processorMap.put(PaymentMethod.CARD, new CardPaymentProcessor());
        processorMap.put(PaymentMethod.UPI, new UPIPaymentProcessor());

        // 3. Service
        PaymentService paymentService
                = new PaymentService(processorMap, paymentRepository);

        // 4. Create payment
        models.Payment payment = paymentService.createPayment(1000.0, PaymentMethod.CARD);
        System.out.println("Payment Created: " + payment.getPaymentId());

        // 5. Process payment
        paymentService.processPayment(payment.getPaymentId());

        // 6. Get status
        Payment finalPayment
                = paymentService.getPaymentStatus(payment.getPaymentId());

        System.out.println("Final Status: " + finalPayment.getStatus());
    }
}
