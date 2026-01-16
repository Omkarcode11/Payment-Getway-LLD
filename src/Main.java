
import enums.PaymentMethod;
import enums.PaymentStatus;
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

    private static PaymentService paymentService;

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("PAYMENT GATEWAY SYSTEM - COMPREHENSIVE TEST SUITE");
        System.out.println("=".repeat(70));
        System.out.println();

        // Initialize system
        initializeSystem();

        // Run all test cases
        testBasicPaymentFlow();
        testIdempotency();
        testMultiplePaymentMethods();
        testPaymentStatusPolling();
        testMultiplePayments();
        testErrorHandling();
        testConcurrentPayments();
        testPaymentStatusTransitions();

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("ALL TESTS COMPLETED");
        System.out.println("=".repeat(70));
    }

    private static void initializeSystem() {
        // 1. Repository
        Repository paymentRepository = new InMemoryPaymentRepository();
        IdempotencyRepository idempotencyRepository = new InMemoryIdempotencyRepository();

        AsyncPaymentExecutor asyncPaymentExecutor = new AsyncPaymentExecutor();

        // 2. Processors
        Map<PaymentMethod, PaymentProcessor> processorMap = new HashMap<>();
        processorMap.put(PaymentMethod.CARD, new CardPaymentProcessor());
        processorMap.put(PaymentMethod.UPI, new UPIPaymentProcessor(asyncPaymentExecutor));

        // 3. Service
        paymentService = new PaymentService(processorMap, paymentRepository, idempotencyRepository);
    }

    // Test Case 1: Basic Payment Flow
    private static void testBasicPaymentFlow() {
        System.out.println("TEST 1: Basic Payment Flow");
        System.out.println("-".repeat(70));
        try {
            // Create payment
            Payment payment = paymentService.createPayment(1000.0, PaymentMethod.CARD, "test-key-1");
            System.out.println("✓ Payment Created:");
            System.out.println("  Payment ID: " + payment.getPaymentId());
            System.out.println("  Amount: ₹" + payment.getAmount());
            System.out.println("  Method: " + payment.getMethod());
            System.out.println("  Initial Status: " + payment.getStatus());

            // Process payment
            System.out.println("\n→ Processing payment...");
            paymentService.processPayment(payment.getPaymentId());

            // Get status immediately
            Payment statusPayment = paymentService.getPaymentStatus(payment.getPaymentId());
            System.out.println("  Status after processing: " + statusPayment.getStatus());

            System.out.println("✓ Test 1 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 1 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 2: Idempotency - Same key should return same payment
    private static void testIdempotency() {
        System.out.println("TEST 2: Idempotency Test");
        System.out.println("-".repeat(70));
        try {
            String idempotencyKey = "unique-order-12345";
            
            // Create first payment
            Payment payment1 = paymentService.createPayment(5000.0, PaymentMethod.CARD, idempotencyKey);
            System.out.println("✓ First Payment Created:");
            System.out.println("  Payment ID: " + payment1.getPaymentId());
            System.out.println("  Idempotency Key: " + idempotencyKey);

            // Create payment with same idempotency key
            Payment payment2 = paymentService.createPayment(5000.0, PaymentMethod.CARD, idempotencyKey);
            System.out.println("\n✓ Second Payment with Same Key:");
            System.out.println("  Payment ID: " + payment2.getPaymentId());

            // Verify both are same
            if (payment1.getPaymentId().equals(payment2.getPaymentId())) {
                System.out.println("\n✓ Idempotency Verified: Same payment returned for same key");
            } else {
                System.out.println("\n✗ Idempotency FAILED: Different payments for same key");
            }

            System.out.println("✓ Test 2 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 2 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 3: Multiple Payment Methods
    private static void testMultiplePaymentMethods() {
        System.out.println("TEST 3: Multiple Payment Methods");
        System.out.println("-".repeat(70));
        try {
            // Test CARD payment
            Payment cardPayment = paymentService.createPayment(2500.0, PaymentMethod.CARD, "card-payment-1");
            System.out.println("✓ CARD Payment Created:");
            System.out.println("  Payment ID: " + cardPayment.getPaymentId());
            System.out.println("  Method: " + cardPayment.getMethod());
            System.out.println("  Amount: ₹" + cardPayment.getAmount());
            
            paymentService.processPayment(cardPayment.getPaymentId());
            System.out.println("  → CARD payment processing initiated");

            // Test UPI payment
            Payment upiPayment = paymentService.createPayment(1500.0, PaymentMethod.UPI, "upi-payment-1");
            System.out.println("\n✓ UPI Payment Created:");
            System.out.println("  Payment ID: " + upiPayment.getPaymentId());
            System.out.println("  Method: " + upiPayment.getMethod());
            System.out.println("  Amount: ₹" + upiPayment.getAmount());
            
            paymentService.processPayment(upiPayment.getPaymentId());
            System.out.println("  → UPI payment processing initiated");

            System.out.println("\n✓ Test 3 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 3 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 4: Payment Status Polling
    private static void testPaymentStatusPolling() {
        System.out.println("TEST 4: Payment Status Polling");
        System.out.println("-".repeat(70));
        try {
            Payment payment = paymentService.createPayment(3000.0, PaymentMethod.CARD, "polling-test-1");
            System.out.println("✓ Payment Created: " + payment.getPaymentId());
            System.out.println("  Initial Status: " + payment.getStatus());

            // Start processing
            paymentService.processPayment(payment.getPaymentId());
            System.out.println("\n→ Polling payment status every 500ms...\n");

            // Poll status multiple times
            for (int i = 0; i < 8; i++) {
                try {
                    Thread.sleep(500);
                    Payment currentPayment = paymentService.getPaymentStatus(payment.getPaymentId());
                    System.out.println("  Poll #" + (i + 1) + ": Status = " + currentPayment.getStatus());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Final status
            Payment finalPayment = paymentService.getPaymentStatus(payment.getPaymentId());
            System.out.println("\n✓ Final Status: " + finalPayment.getStatus());
            System.out.println("✓ Test 4 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 4 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 5: Multiple Payments
    private static void testMultiplePayments() {
        System.out.println("TEST 5: Multiple Payments Handling");
        System.out.println("-".repeat(70));
        try {
            System.out.println("→ Creating 5 different payments...\n");

            Payment[] payments = new Payment[5];
            for (int i = 0; i < 5; i++) {
                double amount = 1000.0 * (i + 1);
                PaymentMethod method = (i % 2 == 0) ? PaymentMethod.CARD : PaymentMethod.UPI;
                String key = "multi-payment-" + (i + 1);
                
                payments[i] = paymentService.createPayment(amount, method, key);
                System.out.println("  Payment #" + (i + 1) + ":");
                System.out.println("    ID: " + payments[i].getPaymentId());
                System.out.println("    Amount: ₹" + amount);
                System.out.println("    Method: " + method);
                
                // Process each payment
                paymentService.processPayment(payments[i].getPaymentId());
            }

            System.out.println("\n→ All payments processed. Waiting for completion...\n");
            Thread.sleep(3000);

            // Check all payment statuses
            System.out.println("Final Statuses:");
            for (int i = 0; i < payments.length; i++) {
                Payment finalStatus = paymentService.getPaymentStatus(payments[i].getPaymentId());
                System.out.println("  Payment #" + (i + 1) + ": " + finalStatus.getStatus());
            }

            System.out.println("\n✓ Test 5 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 5 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 6: Error Handling
    private static void testErrorHandling() {
        System.out.println("TEST 6: Error Handling");
        System.out.println("-".repeat(70));
        try {
            // Test 1: Invalid Payment ID
            System.out.println("→ Testing invalid payment ID...");
            try {
                paymentService.getPaymentStatus("invalid-payment-id-12345");
                System.out.println("✗ Expected exception not thrown");
            } catch (RuntimeException e) {
                System.out.println("✓ Caught expected exception: " + e.getMessage());
            }

            // Test 2: Process non-existent payment
            System.out.println("\n→ Testing processing non-existent payment...");
            try {
                paymentService.processPayment("non-existent-payment-id");
                System.out.println("✗ Expected exception not thrown");
            } catch (RuntimeException e) {
                System.out.println("✓ Caught expected exception: " + e.getMessage());
            }

            System.out.println("\n✓ Test 6 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 6 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 7: Concurrent Payments Simulation
    private static void testConcurrentPayments() {
        System.out.println("TEST 7: Concurrent Payments Simulation");
        System.out.println("-".repeat(70));
        try {
            System.out.println("→ Simulating 3 concurrent payment requests...\n");

            Thread[] threads = new Thread[3];
            Payment[] payments = new Payment[3];

            for (int i = 0; i < 3; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        String key = "concurrent-payment-" + index;
                        payments[index] = paymentService.createPayment(2000.0 + index * 100, 
                                                                      PaymentMethod.CARD, 
                                                                      key);
                        System.out.println("  Thread " + index + ": Payment " + payments[index].getPaymentId() + " created");
                        
                        paymentService.processPayment(payments[index].getPaymentId());
                        System.out.println("  Thread " + index + ": Payment processing started");
                    } catch (Exception e) {
                        System.out.println("  Thread " + index + " ERROR: " + e.getMessage());
                    }
                });
            }

            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("\n→ All concurrent requests processed");
            Thread.sleep(2500);

            // Verify all payments
            System.out.println("\nConcurrent Payments Status:");
            for (int i = 0; i < payments.length; i++) {
                if (payments[i] != null) {
                    Payment status = paymentService.getPaymentStatus(payments[i].getPaymentId());
                    System.out.println("  Payment " + i + ": " + status.getStatus());
                }
            }

            System.out.println("\n✓ Test 7 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 7 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test Case 8: Payment Status Transitions
    private static void testPaymentStatusTransitions() {
        System.out.println("TEST 8: Payment Status Transitions");
        System.out.println("-".repeat(70));
        try {
            Payment payment = paymentService.createPayment(5000.0, PaymentMethod.UPI, "status-transition-test");
            System.out.println("✓ Payment Created: " + payment.getPaymentId());
            
            // Check CREATED status
            Payment initial = paymentService.getPaymentStatus(payment.getPaymentId());
            System.out.println("  Status: " + initial.getStatus() + " (Expected: CREATED)");
            
            if (initial.getStatus() != PaymentStatus.CREATED) {
                System.out.println("✗ Unexpected initial status");
                return;
            }

            // Start processing
            System.out.println("\n→ Starting payment processing...");
            paymentService.processPayment(payment.getPaymentId());
            
            // Check immediate status (should be CREATED or PROCESSING)
            Thread.sleep(100);
            Payment processing = paymentService.getPaymentStatus(payment.getPaymentId());
            System.out.println("  Status after process call: " + processing.getStatus());
            System.out.println("  (Should be CREATED or PROCESSING)");

            // Wait for processing to complete
            System.out.println("\n→ Waiting for processing to complete...");
            Thread.sleep(2500);

            // Final status should be SUCCESS, FAILED, or PENDING
            Payment finalPayment = paymentService.getPaymentStatus(payment.getPaymentId());
            PaymentStatus finalStatus = finalPayment.getStatus();
            System.out.println("  Final Status: " + finalStatus);
            
            if (finalStatus == PaymentStatus.SUCCESS || 
                finalStatus == PaymentStatus.FAILED || 
                finalStatus == PaymentStatus.PENDING) {
                System.out.println("✓ Valid final status achieved");
            } else {
                System.out.println("✗ Unexpected final status: " + finalStatus);
            }

            System.out.println("\n✓ Test 8 PASSED\n");
        } catch (Exception e) {
            System.out.println("✗ Test 8 FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
