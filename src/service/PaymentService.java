package service;

import enums.*;
import java.util.Map;
import java.util.UUID;
import models.Payment;
import processor.*;
import repository.Repository;

public class PaymentService {

    private final Map<PaymentMethod, PaymentProcessor> processorMap;
    private final Repository paymentRepository;

    public PaymentService(Map<PaymentMethod, PaymentProcessor> processMap, Repository paymentRepository2){
        this.processorMap = processMap;
        this.paymentRepository = paymentRepository2;
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

    public Payment getPaymentStatus(String paymentId){
        return paymentRepository.getById(paymentId);
    }
}
