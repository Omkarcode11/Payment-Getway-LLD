package service;

import enums.*;
import java.util.Map;
import java.util.UUID;
import models.Payment;
import processor.*;
import repository.PaymentRepository;

public class PaymentService {

    private final Map<PaymentMethod, PaymentProcessor> processorMap;
    private final PaymentRepository paymentRepository;

    public PaymentService(Map<PaymentMethod, PaymentProcessor> processMap, PaymentRepository paymentRepository){
        this.processorMap = processMap;
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(double amount, PaymentMethod method){
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, amount, method);

        paymentRepository.save(payment);

        return payment;
    } 

    public void processPayment(String paymentId){
        Payment payment = paymentRepository.getById(paymentId);

        PaymentProcessor processor = processorMap.get(payment.getMethod());

        if(processor == null){
            throw new RuntimeException("No processor found for method");
        }

        processor.process(payment);
        paymentRepository.update(payment);
    }
}
